package com.example.urlshortner.service;

import java.net.URI;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.urlshortner.domain.UrlEntity;
import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UpdateUrlRequest;
import com.example.urlshortner.dto.UrlAnalyticsResponse;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.exception.AliasAlreadyExistsException;
import com.example.urlshortner.exception.ExpiredUrlException;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.repository.UrlRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private static final String DETAILS_CACHE_PREFIX = "url:details:";
    private static final String REDIRECT_CACHE_PREFIX = "url:redirect:";
    private static final String CLICK_TOPIC = "url-click-events";

    private final UrlRepository repository;
    private final CacheOperations cacheOperations;
    private final ClickEventPublisher clickEventPublisher;
    private final ObjectMapper objectMapper;

    public UrlService(UrlRepository repository, CacheOperations cacheOperations, ClickEventPublisher clickEventPublisher,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.cacheOperations = cacheOperations;
        this.clickEventPublisher = clickEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UrlResponse createUrl(CreateUrlRequest request) {
        String normalizedAlias = normalizeAlias(request.alias());
        validateTargetUrl(request.targetUrl());
        if (normalizedAlias != null && repository.existsByAlias(normalizedAlias)) {
            throw new AliasAlreadyExistsException("alias already exists");
        }

        UrlEntity entity = new UrlEntity();
        entity.setTargetUrl(request.targetUrl());
        entity.setAlias(normalizedAlias != null ? normalizedAlias : generateAlias());
        entity.setExpiresAt(request.expiresAt());
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        UrlEntity saved;
        try {
            saved = repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new AliasAlreadyExistsException("alias already exists", ex);
        }
        cacheDetails(saved);
        cacheRedirect(saved.getAlias(), saved.getTargetUrl());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public UrlResponse getUrl(UUID id) {
        String cached = cacheOperations.get(DETAILS_CACHE_PREFIX + id);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, UrlResponse.class);
            } catch (JsonProcessingException ex) {
                log.warn("Failed to read cached URL details", ex);
            }
        }

        UrlEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("url not found"));
        UrlResponse response = mapToResponse(entity);
        cacheDetails(response);
        return response;
    }

    @Transactional
    public UrlResponse updateUrl(UUID id, UpdateUrlRequest request) {
        validateTargetUrl(request.targetUrl());
        UrlEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("url not found"));

        String normalizedAlias = normalizeAlias(request.alias());
        if (normalizedAlias != null && repository.existsByAliasAndIdNot(normalizedAlias, id)) {
            throw new AliasAlreadyExistsException("alias already exists");
        }

        entity.setTargetUrl(request.targetUrl());
        entity.setAlias(normalizedAlias != null ? normalizedAlias : entity.getAlias());
        entity.setExpiresAt(request.expiresAt());
        entity.setUpdatedAt(Instant.now());

        UrlEntity saved;
        try {
            saved = repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new AliasAlreadyExistsException("alias already exists", ex);
        }
        evictCaches(saved.getId(), saved.getAlias());
        cacheDetails(saved);
        cacheRedirect(saved.getAlias(), saved.getTargetUrl());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteUrl(UUID id) {
        UrlEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("url not found"));
        repository.delete(entity);
        evictCaches(id, entity.getAlias());
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> listUrls() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(UrlEntity::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UrlAnalyticsResponse getAnalytics() {
        List<UrlEntity> entities = repository.findAll();
        long activeCount = entities.stream().filter(e -> e.isActive() && !e.isExpired()).count();
        long expiredCount = entities.stream().filter(e -> e.isActive() && e.isExpired()).count();
        long totalClicks = entities.stream().mapToLong(UrlEntity::getClickCount).sum();
        return new UrlAnalyticsResponse(entities.size(), activeCount, expiredCount, totalClicks);
    }

    @Transactional
    public String redirectToTarget(String alias) {
        String normalizedAlias = normalizeAlias(alias);
        if (normalizedAlias == null) {
            throw new ResourceNotFoundException("url not found");
        }

        UrlEntity entity = repository.findByAlias(normalizedAlias)
                .orElseThrow(() -> new ResourceNotFoundException("url not found"));

        if (!entity.isActive()) {
            throw new ResourceNotFoundException("url not found");
        }
        if (entity.isExpired()) {
            throw new ExpiredUrlException("url has expired");
        }

        entity.setClickCount(entity.getClickCount() + 1);
        entity.setUpdatedAt(Instant.now());
        UrlEntity saved = repository.saveAndFlush(entity);
        evictCaches(saved.getId(), saved.getAlias());
        cacheDetails(saved);
        publishClickEvent(saved);
        return saved.getTargetUrl();
    }

    private void validateTargetUrl(String targetUrl) {
        try {
            URI uri = URI.create(targetUrl);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("targetUrl must be a valid absolute URL");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("targetUrl must be a valid absolute URL", ex);
        }
    }

    private String normalizeAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }
        String normalized = alias.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("^[a-z0-9-]+$")) {
            throw new IllegalArgumentException("alias must contain only letters, numbers, or hyphens");
        }
        return normalized;
    }

    private String generateAlias() {
        return "short-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private UrlResponse mapToResponse(UrlEntity entity) {
        return new UrlResponse(entity.getId(), entity.getTargetUrl(), entity.getAlias(), entity.isActive(),
                entity.getCreatedAt(), entity.getUpdatedAt(), entity.getExpiresAt(), entity.getClickCount());
    }

    private void cacheDetails(UrlEntity entity) {
        cacheDetails(mapToResponse(entity));
    }

    private void cacheDetails(UrlResponse response) {
        try {
            cacheOperations.set(DETAILS_CACHE_PREFIX + response.id(), objectMapper.writeValueAsString(response), 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to cache URL details", ex);
        }
    }

    private void cacheRedirect(String alias, String targetUrl) {
        cacheOperations.set(REDIRECT_CACHE_PREFIX + alias, targetUrl, 10, TimeUnit.MINUTES);
    }

    private void evictCaches(UUID id, String alias) {
        cacheOperations.delete(DETAILS_CACHE_PREFIX + id);
        if (alias != null) {
            cacheOperations.delete(REDIRECT_CACHE_PREFIX + alias);
        }
    }

    private void publishClickEvent(UrlEntity entity) {
        try {
            String payload = objectMapper.writeValueAsString(java.util.Map.of(
                    "alias", entity.getAlias(),
                    "targetUrl", entity.getTargetUrl(),
                    "clickedAt", Instant.now().toString()));
            clickEventPublisher.publish(CLICK_TOPIC, payload);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to publish click event", ex);
        }
    }
}
