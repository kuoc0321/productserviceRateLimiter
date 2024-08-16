package com.example.product_service.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final Map<Long, AtomicInteger> windows = new ConcurrentHashMap<>();
    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public synchronized boolean tryConsume() {
        long currentTimeMillis = System.currentTimeMillis();
        long currentWindow = currentTimeMillis / windowSizeInMillis;

        windows.putIfAbsent(currentWindow, new AtomicInteger(0));


        int countInCurrentWindow = windows.get(currentWindow).get();
        int countInPreviousWindow = windows.getOrDefault(currentWindow - 1, new AtomicInteger(0)).get();

        long overlapTime = currentTimeMillis % windowSizeInMillis;
        double overlapRatio = (double) overlapTime / windowSizeInMillis;

        int totalRequests = countInCurrentWindow + (int)(countInPreviousWindow * overlapRatio);

        if (totalRequests >= maxRequests) {
            return false;
        }

        windows.get(currentWindow).incrementAndGet();
        return true;
    }
}
