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
    Long head = window.peek();

    if (head == null) {
      // 记录当前请求
      window.add(now);
      return true;
    }
    /*
     * 最早的请求距离当前请求在windowLength之内，且时间窗口记录的请求数已经超过maxRequests
     * 说明时间窗口内已经填满
     */
    long distant = now - head;
    if (distant <= windowLength && window.size() >= maxRequests) {
      return false;
    }
    /*
     * 最早的请求距离当前请求已经超出了windowLength，那么要把这个最早的请求删掉
     */
    if (distant > windowLength) {
      window.poll();
    }
    // 记录当前请求
    window.add(now);
    return true;
  }

}
