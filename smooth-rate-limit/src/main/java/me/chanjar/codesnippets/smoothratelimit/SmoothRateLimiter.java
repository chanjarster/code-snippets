package me.chanjar.codesnippets.smoothratelimit;

/**
 * 平滑的限流器
 */
public interface SmoothRateLimiter {

  /**
   * 获取许可, 非阻塞
   * @return true 获取成功，token数-1；false 获取失败，说明token已经为0了。
   */
  boolean tryAcquire();
}
