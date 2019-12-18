package me.chanjar.codesnippets.smoothratelimit;

public class SynchronizedSmoothRateLimiterTest extends SmoothRateLimiterTestBase {
  @Override
  protected SmoothRateLimiter rateLimiter(long windowLength, int maxRequests) {
    return new SynchronizedSmoothRateLimiter(windowLength, maxRequests);
  }
}
