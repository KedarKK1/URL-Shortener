package com.example.urlshortner.repository;

import com.example.urlshortner.domain.UrlEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<UrlEntity, UUID> {

    Optional<UrlEntity> findByAlias(String alias);

    boolean existsByAlias(String alias);

    boolean existsByAliasAndIdNot(String alias, UUID id);
}
