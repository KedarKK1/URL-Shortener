package com.example.urlshortner.dto;

public record UrlAnalyticsResponse(long totalUrls, long activeUrls, long expiredUrls, long totalClicks) {
}
