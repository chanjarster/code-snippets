package me.chanjar.codesnippets.smoothratelimit;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 比如任意5分钟内，最多只能有1000个请求
 */
public class SynchronizedSmoothRateLimiter implements SmoothRateLimiter {

  /**
   * 时间窗口长度（单位ms）
   */
  private final long windowLength;

  /**
   * 时间窗口内能够有多少个请求
   */
  private final int maxRequests;

  /**
   * 时间窗口，内记录的是时间戳
   */
  private final Queue<Long> window = new LinkedList<>();

  public SynchronizedSmoothRateLimiter(long windowLength, int maxRequests) {
    this.windowLength = windowLength;
    this.maxRequests = maxRequests;
  }

  @Override
  public synchronized boolean tryAcquire() {
    long now = System.currentTimeMillis();
    int windowSize = window.size();
    if (windowSize < maxRequests) {
      window.add(now);
      return true;
    }

    long head = window.peek().longValue();
    long distant = now - head;
    if (distant <= windowLength) {
      return false;
    }
    window.poll();
    window.add(now);
    return true;
  }

}
