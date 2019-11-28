package me.chanjar.codesnippets.tokenbucket;

/**
 * 令牌桶
 */
public interface TokenBucket {

  /**
   * 获取Token, 非阻塞
   * @return true 获取成功，token数-1；false 获取失败，说明token已经为0了。
   */
  boolean tryAcquire();

}
