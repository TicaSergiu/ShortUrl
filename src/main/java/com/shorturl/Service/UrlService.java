package com.shorturl.Service;

import com.shorturl.Constants.AppConstants;
import com.shorturl.Exceptions.CustomUrlAlreadyExistsException;
import com.shorturl.Models.UrlPO;
import com.shorturl.Repositories.UrlRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

import java.util.List;
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
            throw new InvalidUrlException("");
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
        if(url == null || url.length() < 4) {
            return true;
        }
        final String regex = "\"(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)\"";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public UrlPO makeCustomUrl(String url, String customUrl) {
        if(isNotValidUrl(url)) {
            throw new InvalidUrlException(
                    "URL is not valid. The valid URL should not be empty and be a valid web address.");
        }

        if(!isValidCustomUrl(customUrl)) {
            throw new InvalidUrlException(
                    "Custom URL should be exactly 8 characters long and contain only numbers and letters.");
        }

        UrlPO urlPO = urlRepository.findByShortUrl(customUrl);
        if(urlPO != null) {
            throw new CustomUrlAlreadyExistsException("Selected custom URL is already in use");
        }

        urlPO = new UrlPO();
        urlPO.setOriginalUrl(url);
        urlPO.setShortUrl(customUrl);
        return urlRepository.save(urlPO);
    }

    private boolean isValidCustomUrl(String customUrl) {
        if(customUrl.length() != AppConstants.SHORT_URL_SIZE) {
            return false;
        }
        final String regex = "\\w{8}";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(customUrl).matches();
    }

    public List<UrlPO> getUrlsCreatedByUser(String username) {
        return urlRepository.findAllByUsername(username);
    }
}
