package com.shorturl.Controllers;

import com.shorturl.Models.UrlPO;
import com.shorturl.Service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("api/")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("shorten")
    public UrlPO shortenURL(@RequestParam String url) {
        return urlService.shortenUrl(url);
    }

    @PostMapping("shorten/custom")
    public UrlPO customUrl(@RequestParam String url, @RequestParam String customUrl) {
        return urlService.makeCustomUrl(url, customUrl);
    }

    @GetMapping("")
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
