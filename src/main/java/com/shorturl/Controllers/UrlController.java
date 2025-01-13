package com.shorturl.Controllers;

import com.shorturl.Models.UrlPO;
import com.shorturl.Service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("api/v1/shorten/")
    public UrlPO shortenURL(@RequestParam String url) {
        return urlService.shortenUrl(url);
    }

    @GetMapping("/api/v1/")
    public ResponseEntity<Void> redirect(@RequestParam String shortUrl) {
        UrlPO urlPO = urlService.findOriginalUrl(shortUrl);
        if(urlPO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified url does not exist");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlPO.getOriginalUrl()));
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }
}