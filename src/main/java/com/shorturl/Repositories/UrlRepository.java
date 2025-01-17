package com.shorturl.Repositories;

import com.shorturl.Models.UrlPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlPO, Long> {
    boolean existsUrlByOriginalUrl(String originalUrl);

    boolean existsByShortUrlNot(String shortUrl);

    UrlPO findByShortUrl(String shortUrl);

    UrlPO findByOriginalUrl(String originalUrl);

    @Query(value="SELECT urls.url_id, urls.short_url, urls.original_url, urls.created_at, urls.expires_at, urls.access_count, urls.user_id FROM url_schema.urls as urls JOIN url_schema.users as users ON urls.user_id = users.id WHERE users.username=?1", nativeQuery=true)
    List<UrlPO> findAllByUsername(String username);
}
