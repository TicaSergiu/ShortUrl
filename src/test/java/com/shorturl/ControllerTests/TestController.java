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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.InvalidUrlException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

        when(urlService.shortenUrl(url)).thenReturn(actual);

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

    @Test
    public void testCustomUrlSuccessful() throws Exception {
        final String custom = "calculat";
        final String link = "https://ro.wikipedia.org/wiki/Calculator";
        final UrlPO expected = new UrlPO();
        expected.setShortUrl(custom);
        expected.setOriginalUrl(link);

        when(urlService.makeCustomUrl(link, custom)).thenReturn(expected);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("url", link);
        params.add("customUrl", custom);
        mockMvc.perform(post("/api/shorten/custom").params(params)).andExpect(status().isCreated())
               .andExpect(jsonPath("$.shortUrl").value(custom)).andExpect(jsonPath("$.originalUrl").value(link));
    }

    @Test
    public void testCustomUrlEmptyCustomUrl() throws Exception {
        final String custom = "";
        final String link = "https://ro.wikipedia.org/wiki/Calculator";
        final UrlPO expected = new UrlPO();
        expected.setShortUrl(custom);
        expected.setOriginalUrl(link);

        when(urlService.makeCustomUrl(link, custom)).thenThrow(new InvalidUrlException("Url not valid"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("url", link);
        params.add("customUrl", custom);
        mockMvc.perform(post("/api/shorten/custom").params(params)).andExpect(status().is4xxClientError()).andExpect(
                content().string(
                        "Custom URL should be exactly 8 characters long and contain only numbers and letters."));
    }

    @Test
    public void testCustomUrlEmptyOriginalUrl() throws Exception {
        final String custom = "whatever";
        final String link = "";
        final UrlPO expected = new UrlPO();
        expected.setShortUrl(custom);
        expected.setOriginalUrl(link);

        when(urlService.makeCustomUrl(link, custom)).thenThrow(new InvalidUrlException("Url not valid"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("url", link);
        params.add("customUrl", custom);
        mockMvc.perform(post("/api/shorten/custom").params(params)).andExpect(status().is4xxClientError()).andExpect(
                content().string("URL is not valid. The valid URL should not be empty and be a valid web address."));
    }
}
