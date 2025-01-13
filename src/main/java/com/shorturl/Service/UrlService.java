package com.shorturl.Service;

import com.shorturl.Configs.AppConstants;
import com.shorturl.Models.UrlPO;
import com.shorturl.Repositories.UrlRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlPO shortenUrl(String url) {
        if(isNotValidUrl(url)) {
            throw new InvalidUrlException("Url is not valid");
        }

        if(urlRepository.existsUrlByOriginalUrl(url)) {
            return urlRepository.findByOriginalUrl(url);
        }

        String shortUrl = generateShortUrl(url);

        if(urlRepository.existsByShortUrlNot(shortUrl)) {
            UrlPO urlPO = new UrlPO();
            urlPO.setOriginalUrl(url);
            urlPO.setShortUrl(shortUrl);
            return urlRepository.save(urlPO);
        }

        do {
            shortUrl = generateShortUrl(url + AppConstants.APPEND_STRING);
        } while(urlRepository.existsByShortUrlNot(shortUrl));
        UrlPO urlPO = new UrlPO();
        urlPO.setOriginalUrl(url);
        urlPO.setShortUrl(shortUrl);

        return urlRepository.save(urlPO);
    }

    private String generateShortUrl(String url) {
        StringBuilder sb = new StringBuilder();
        DigestUtils.md5Hex(url).chars().limit(AppConstants.SHORT_URL_SIZE).forEach(i -> sb.append((char) i));
        return sb.toString();
    }

    public UrlPO findOriginalUrl(String shortUrl) {
        if(isNotValidUrl(shortUrl)) {
            return null;
        }

        return urlRepository.findByShortUrl(shortUrl);
    }

    private boolean isNotValidUrl(String url) {
        if(url == null || url.isEmpty()) {
            return true;
        }
        final String regex = "\"(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)\"";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
