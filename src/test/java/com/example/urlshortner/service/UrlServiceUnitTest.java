package com.example.urlshortner.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito. when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.urlshortner.domain.UrlEntity;
import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.exception.AliasAlreadyExistsException;
import com.example.urlshortner.exception.ExpiredUrlException;
import com.example.urlshortner.repository.UrlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@ExtendWith(MockitoExtension.class)
public class UrlServiceUnitTest {
    @Mock
    private UrlRepository repository;

    @Mock
    private CacheOperations cacheOperations;

    @Mock
    private ClickEventPublisher clickEventPublisher;

    private UrlService urlService;

    @BeforeEach
    void setUp(){
        ObjectMapper objectMapper = new ObjectMapper ();
        objectMapper.registerModule(new JavaTimeModule());
        urlService = new UrlService(repository, cacheOperations, clickEventPublisher, objectMapper);
    }
    
    @Test
    void createUrlShouldPersistCache(){
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "docs", null);
        when(repository.existsByAlias("docs")).thenReturn(false);

        when(repository.saveAndFlush(any(UrlEntity.class))).thenAnswer((InvocationOnMock invocation) -> {
            UrlEntity e = invocation.getArgument(0, UrlEntity.class);
            e.setId(UUID.randomUUID());
            return e;
        });

        UrlResponse response = urlService.createUrl(request);

        assertThat(response.alias()).isEqualTo("docs");
        verify(cacheOperations).set(anyString(), anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }
    
    @Test
    void createUrlShouldRejectDuplicateAlias() {
        when(repository.existsByAlias("docs")).thenReturn(true);
        assertThrows(AliasAlreadyExistsException.class, () -> urlService.createUrl(new CreateUrlRequest("https://example.com", "docs", null)));
    }

    @Test
    void createUrlShouldRejectInvalidTarget() {
        assertThrows(IllegalArgumentException.class, () -> urlService.createUrl(new CreateUrlRequest("not-a-url", "docs", null)));
    }

    @Test
    void redirectShouldRejectExpiredUrls() {
        UrlEntity entity = new UrlEntity();
        entity.setId(UUID.randomUUID());
        entity.setAlias("exp");
        entity.setTargetUrl("https://example.com/expired");
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt (Instant.now());
        entity.setExpiresAt(Instant.now().minusSeconds(60));

        when(repository.findByAlias("exp")).thenReturn(Optional.of(entity)); 

        assertThrows(ExpiredUrlException.class, () -> urlService.redirectToTarget("exp"));
        verify(clickEventPublisher, never()).publish(anyString(), anyString());
    }
}
