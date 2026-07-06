package com.example.urlshortner.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(
        @NotBlank(message = "targetUrl is required")
        @Size(max = 2048, message = "targetUrl must be at most 2048 characters")
        @Pattern(regexp = "https?://.+", message = "targetUrl must be a valid absolute URL")
        String targetUrl,

        @Size(max = 64, message = "alias must be at most 64 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "alias must contain only letters, numbers, or hyphens")
        String alias,

        Instant expiresAt) {
}
