package com.example.urlshortner.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UpdateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.service.UrlService;

import jakarta.validation.Valid;

@RestController
public class ApiUrlController {

        private final UrlService urlService;

        ApiUrlController(UrlService urlService) {
            this.urlService = urlService;
        }

        @PostMapping("/api/urls")
        public ResponseEntity<UrlResponse> createUrl(@Valid @RequestBody CreateUrlRequest request) {
            UrlResponse response = urlService.createUrl(request);
            return ResponseEntity.created(URI.create("/api/urls/" + response.id())).body(response);
        }

        @GetMapping("/api/urls/{id}")
        public ResponseEntity<UrlResponse> getUrl(@PathVariable UUID id) {
            return ResponseEntity.ok(urlService.getUrl(id));
        }

        @PutMapping("/api/urls/{id}")
        public ResponseEntity<UrlResponse> updateUrl(@PathVariable UUID id, @Valid @RequestBody UpdateUrlRequest request) {
            return ResponseEntity.ok(urlService.updateUrl(id, request));
        }

        @DeleteMapping("/api/urls/{id}")
        public ResponseEntity<Void> deleteUrl(@PathVariable UUID id) {
            urlService.deleteUrl(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/redirect/{alias}")
        public ResponseEntity<Void> redirect(@PathVariable String alias) {
            String targetUrl = urlService.redirectToTarget(alias);
            return ResponseEntity.status(302).location(URI.create(targetUrl)).build();
        }
    
}
