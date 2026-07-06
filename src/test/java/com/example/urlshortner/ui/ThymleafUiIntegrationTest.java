package com.example.urlshortner.ui;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.urlshortner.dto.UrlAnalyticsResponse;
import com.example.urlshortner.service.UrlService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ThymleafUiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void dashboardShouldRender() throws Exception{
        when(urlService.listUrls()).thenReturn(List.of());
        when(urlService.getAnalytics()).thenReturn(new UrlAnalyticsResponse(0, 0, 0, 0));
        
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Dashboard")));
    }

    @Test
    void createFormShouldRender() throws Exception {
        mockMvc.perform(get( "/urls/new"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Create")));
    }

}
