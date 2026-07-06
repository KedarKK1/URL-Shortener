package com.example.urlshortner.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.urlshortner.domain.UrlEntity;
import java.time.Instant; 
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory. annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry; 
import org.springframework.test.context.DynamicPropertySource;
import org. testcontainers. containers. PostgreSQLContainer; 
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("test")
public class UrlRepositoryIntegrationTestIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void registerProperties (DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl); 
        registry.add("spring.datasource.username", postgres::getUsername); 
        registry.add("spring.datasource.password", postgres::getPassword); 
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName); 
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UrlRepository repository;

    @Test
    void shouldPersistAndFetchByAliasWithPostgresContainer() {
        UrlEntity entity = new UrlEntity();
        entity.setId(UUID.randomUUID());
        entity.setAlias("container-alias");
        entity.setTargetUrl("https://example.com/container");
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        repository.saveAndFlush(entity);

        assertThat(repository.findByAlias("container-alias")).isPresent();

    }
}