package com.example.urlshortner.dto;

import java.time.Instant;
import java.util.UUID;

public record UrlResponse(
        UUID id,
        String targetUrl,
        String alias,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        Instant expiresAt,
        long clickCount) {
}
