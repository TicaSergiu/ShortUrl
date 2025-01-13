package com.shorturl.Repositories;

import com.shorturl.Models.UrlPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlPO, Long> {
    boolean existsUrlByOriginalUrl(String originalUrl);

    boolean existsByShortUrlNot(String shortUrl);

    UrlPO findByShortUrl(String shortUrl);

    UrlPO findByOriginalUrl(String originalUrl);
}
