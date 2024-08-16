package com.example.product_service.ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class TokenBucketRateLimiter {
    private final Bucket bucket;

    public TokenBucketRateLimiter(long capacity, long tokensPerInterval, Duration interval) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(tokensPerInterval, interval));
        this.bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
    public boolean tryConsume() {
        return bucket.tryConsume(1);
    }
}
