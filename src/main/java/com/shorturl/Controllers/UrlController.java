package com.shorturl.Controllers;

import com.shorturl.Models.UrlPO;
import com.shorturl.Service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.net.URI;
import java.util.List;

@Controller
@EnableWebMvc
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlPO> shortenURL(@RequestParam String url) {
        UrlPO urlPO = urlService.shortenUrl(url);
        return new ResponseEntity<>(urlPO, HttpStatus.CREATED);
    }

    @PostMapping("/api/shorten/custom")
    public ResponseEntity<UrlPO> customUrl(@RequestParam String url, @RequestParam String customUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UrlPO urlPO = urlService.makeCustomUrl(url, customUrl);
        }
        UrlPO urlPO = urlService.makeCustomUrl(url, customUrl);
        return new ResponseEntity<>(urlPO, HttpStatus.CREATED);
    }

    @GetMapping("my-urls")
    public String getMyUrls(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<UrlPO> urls = urlService.getUrlsCreatedByUser(userDetails.getUsername());
            model.addAttribute("urls", urls);

            return "my-urls";
        }
        return "login";
    }

    @GetMapping("{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        UrlPO urlPO = urlService.findOriginalUrl(shortUrl);
        if(urlPO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified url does not exist");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(urlPO.getOriginalUrl()));
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
