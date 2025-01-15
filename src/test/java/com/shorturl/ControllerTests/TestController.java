package com.shorturl.ControllerTests;

import com.shorturl.Controllers.UrlController;
import com.shorturl.Models.UrlPO;
import com.shorturl.Service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages="com.shorturl")
public class TestController {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShortenUrlSuccedes() throws Exception {
        final String url = "https://ro.wikipedia.org/wiki/Calculator";
        final String hash = "3c6becc4";
        final UrlPO expected = new UrlPO();
        expected.setOriginalUrl(url);
        final UrlPO actual = new UrlPO();
        actual.setShortUrl(hash);

        when(urlController.shortenURL(url)).thenReturn(actual);

        mockMvc.perform(post("/api/shorten").param("url", url)).andExpect(jsonPath("$.shortUrl").value(hash));
    }

    @Test()
    public void testRedirectThrowsResponseStatusException() throws Exception {
        final String hash = "Inexistent Hash";
        final String message = "Specified url does not exist";

        when(urlService.findOriginalUrl(hash)).thenReturn(null);

        mockMvc.perform(get("/api/").param("shortUrl", hash)).andExpect(status().isNotFound());
    }

    @Test
    public void testRedirectSuccessful() throws Exception {
        final String hash = "3c6becc4";
        final String url = "https://ro.wikipedia.org/wiki/Calculator";
        final UrlPO expected = new UrlPO();
        expected.setOriginalUrl(url);

        when(urlService.findOriginalUrl(hash)).thenReturn(expected);

        mockMvc.perform(get("/api/").param("shortUrl", hash)).andExpect(header().string("Location", url))
               .andExpect(status().isPermanentRedirect());
    }
}
