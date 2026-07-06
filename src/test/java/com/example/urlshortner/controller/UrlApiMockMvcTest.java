package com.example.urlshortner.controller;

import java. time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders. post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.exception.ResourceNotFoundException;
import com.example.urlshortner.service.UrlService;


@WebMvcTest(ApiUrlController.class)
@Import(ApiExceptionHandler.class)
public class UrlApiMockMvcTest {
    
    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    // private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService uriService;

    @Test
    void createShouldReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();
        UrlResponse response = new UrlResponse(id, "https://example.com", "docs", true, Instant. now(), Instant.now(), null, 0);
        when(uriService.createUrl(any(CreateUrlRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"targetUrl": "https://example.com", "alias": "docs"}
                """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/urls/" + id))
        .andExpect(jsonPath("$.alias").value("docs"));
    }


    @Test
    void createShouldReturnBadRequestOnValidationError() throws Exception{
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getShouldReturnNotFound() throws Exception {
        UUID missing = UUID.randomUUID();
        when (uriService.getUrl(missing)).thenThrow(new ResourceNotFoundException( "url not found")); 
        mockMvc.perform(get("/api/urls/{id}", missing))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("url not found"));
    }

    @Test
    void redirectShouldReturnFound() throws Exception{
        when(uriService.redirectToTarget("docs")).thenReturn("https://example.com/docs");

        mockMvc.perform(get("/redirect/{alias}", "docs"))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "https://example.com/docs"));
    }

}
