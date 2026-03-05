package com.telemetry.urlshortener.controller;

import com.telemetry.urlshortener.model.ShortenRequest;
import com.telemetry.urlshortener.model.UrlMapping;
import com.telemetry.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class UrlController {

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlShortenerService urlShortenerService;
    private final Random random = new Random();

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    private boolean shouldFail() {
        return random.nextInt(100) < 10;
    }

    private void maybeFail(String endpointName) {
        if (shouldFail()) {
            RuntimeException ex = new RuntimeException("Unexpected failure in " + endpointName);
            logger.error("Failure in {}: {}", endpointName, ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        logger.info("Home endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "message", "URL Shortener Service",
                "version", "1.0.0",
                "status", "running"
        ));
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody ShortenRequest request) {
        logger.info("Received shorten request for URL: {}", request.getUrl());

        try {
            maybeFail("POST /shorten");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal service error"));
        }

        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            logger.warn("Empty URL provided");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "URL is required"));
        }

        try {
            UrlMapping mapping = urlShortenerService.shortenUrl(
                    request.getUrl(),
                    request.getCustomCode()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "shortCode", mapping.getShortCode(),
                            "originalUrl", mapping.getOriginalUrl(),
                            "shortUrl", "/api/" + mapping.getShortCode(),
                            "createdAt", mapping.getCreatedAt().toString()
                    ));

        } catch (IllegalArgumentException e) {
            logger.error("Error shortening URL: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        logger.info("Redirect request for short code: {}", shortCode);
        maybeFail("GET /{shortCode}");
        return urlShortenerService.getOriginalUrl(shortCode)
                .map(mapping -> {
                    RedirectView redirectView = new RedirectView(mapping.getOriginalUrl());
                    redirectView.setStatusCode(HttpStatus.FOUND);
                    return redirectView;
                })
                .orElseGet(() -> {
                    RedirectView redirectView = new RedirectView("/api/");
                    redirectView.setStatusCode(HttpStatus.NOT_FOUND);
                    return redirectView;
                });
    }

    @GetMapping("/urls")
    public ResponseEntity<Map<String, UrlMapping>> getAllUrls() {
        logger.info("Request to get all URLs");

        try {
            maybeFail("GET /urls");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        return ResponseEntity.ok(urlShortenerService.getAllUrls());
    }

}
