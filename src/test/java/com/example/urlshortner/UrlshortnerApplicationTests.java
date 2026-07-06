package com.example.urlshortner;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UpdateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.exception.AliasAlreadyExistsException;
import com.example.urlshortner.exception.ExpiredUrlException;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.service.UrlService;

@SpringBootTest
@ActiveProfiles("test")
class UrlshortnerApplicationTests {

	@Autowired
	private UrlService urlService;

	@Test
	void shouldCreateReadUpdateAndDeleteUrlAndRedirect() {
		CreateUrlRequest createRequest = new CreateUrlRequest("https://example.com/docs", "docs", Instant.parse("2030-12-31T23:59:59Z"));
		UrlResponse created = urlService.createUrl(createRequest);

		assertThat(created.alias()).isEqualTo("docs");
		assertThat(created.targetUrl()).isEqualTo("https://example.com/docs");

		UrlResponse fetched = urlService.getUrl(created.id());
		assertThat(fetched.id()).isEqualTo(created.id());

		UrlResponse updated = urlService.updateUrl(created.id(), new UpdateUrlRequest("https://example.com/updated", "docs-updated", Instant.parse("2030-12-31T23:59:59Z")));
		assertThat(updated.alias()).isEqualTo("docs-updated");

		String redirectTarget = urlService.redirectToTarget("docs-updated");
		assertThat(redirectTarget).isEqualTo("https://example.com/updated");

		urlService.deleteUrl(created.id());
		assertThrows(ResourceNotFoundException.class, () -> urlService.getUrl(created.id()));
	}

	@Test
	void shouldRejectInvalidAlias() {
		CreateUrlRequest invalidRequest = new CreateUrlRequest("https://example.com/docs", "bad alias", null);
		assertThrows(IllegalArgumentException.class, () -> urlService.createUrl(invalidRequest));
	}

	@Test
	void shouldPreventDuplicateAlias() {
		CreateUrlRequest first = new CreateUrlRequest("https://example.com/one", "unique-alias", null);
		CreateUrlRequest second = new CreateUrlRequest("https://example.com/two", "unique-alias", null);
		urlService.createUrl(first);
		assertThrows(AliasAlreadyExistsException.class, () -> urlService.createUrl(second));
	}

	@Test
	void shouldRejectExpiredUrls() {
		CreateUrlRequest request = new CreateUrlRequest("https://example.com/expired", "expired-link", Instant.now().minusSeconds(60));
		UrlResponse created = urlService.createUrl(request);
		assertThrows(ExpiredUrlException.class, () -> urlService.redirectToTarget(created.alias()));
	}

	@Test
	void shouldThrowWhenUrlDoesNotExist() {
		assertThrows(ResourceNotFoundException.class, () -> urlService.getUrl(UUID.randomUUID()));
	}
}
