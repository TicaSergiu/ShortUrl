package com.shorturl.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name="urls", schema="url_schema")
public class UrlPO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="url_id", nullable=false)
    private Integer id;

    @Column(name="short_url", nullable=false, length=8)
    private String shortUrl;

    @Column(name="original_url", nullable=false, length=Integer.MAX_VALUE)
    private String originalUrl;

    @Column(name="created_at", nullable=false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name="expires_at")
    private Instant expiresAt;

    @Column(name="access_count")
    private Integer accessCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

}