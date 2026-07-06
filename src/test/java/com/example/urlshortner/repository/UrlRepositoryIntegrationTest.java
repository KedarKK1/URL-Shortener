package com.example.urlshortner.repository;

import static org.assertj.core.api.Assertions. assertThat;
import com.example.urlshortner.domain.UrlEntity;
import java.time.Instant; 
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
// import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

@DataJpaTest
public class UrlRepositoryIntegrationTest {

    @Autowired
    private UrlRepository repository;

    @Test
    void shouldFindByAliasAndCheckExistence(){
        UrlEntity entity = new UrlEntity();
        entity.setId(UUID.randomUUID());
        entity.setAlias("repo-test");
        entity.setTargetUrl("https://example.com/repo");
        entity.setActive(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt (Instant.now());

        UrlEntity saved = repository.saveAndFlush(entity);

        assertThat(repository.findByAlias("repo-test")).isPresent(); 
        assertThat(repository.existsByAlias("repo-test")).isTrue();
        assertThat(repository.existsByAliasAndIdNot("repo-test", saved.getId())). isFalse();

    }
}
