package com.example.product_service.ratelimiter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private AtomicInteger requestCount = new AtomicInteger(0);
    private long windowStartTime;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.windowStartTime = Instant.now().toEpochMilli();
    }

    public synchronized boolean tryConsume() {
        long now = Instant.now().toEpochMilli();
        if (now - windowStartTime > windowSizeInMillis) {
            windowStartTime = now;
            requestCount.set(0);
        }
        if (requestCount.incrementAndGet() <= maxRequests) {
            return true;
        }
        return false;
    }
}
