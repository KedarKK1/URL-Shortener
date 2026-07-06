package com.example.urlshortner.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToInstantConverter implements Converter<String, Instant> {
    
    @Override
    public Instant convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            // First try direct Instant parsing (standard ISO-8601 with timezone/offset)
            return Instant.parse(source);
        } catch (Exception e) {
            try {
                // If direct parsing fails, treat as LocalDateTime without offset and assume system default zone
                LocalDateTime localDateTime = LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            } catch (Exception ex) {
                // Fallback for date-only formats or other patterns if needed
                return null;
            }
        }
    }
}
