package me.chanjar.codesnippets.smoothratelimit;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public abstract class SmoothRateLimiterTestBase {

  /**
   *
   * @param windowLength  时间窗口长度
   * @param maxRequests   最多请求数
   * @return
   */
  protected abstract SmoothRateLimiter rateLimiter(long windowLength, int maxRequests);

  @Test
  public void tryAcquire() throws InterruptedException {
    // 时间窗口1秒，最多10个请求
    SmoothRateLimiter tokenBucket = rateLimiter(TimeUnit.SECONDS.toMillis(1L), 10);

    tryAcquireRound(1, tokenBucket);
    // 睡眠等待桶恢复
    TimeUnit.MILLISECONDS.sleep(1500L);

    tryAcquireRound(2, tokenBucket);
  }

  private void tryAcquireRound(int round, SmoothRateLimiter tokenBucket) {
    // 连续获取10个
    for (int i = 0; i < 10; i++) {
      assertEquals("Round " + round + ", tryAcquire: #" + i, true, tokenBucket.tryAcquire());
    }
    // 应该被禁止了
    assertEquals("Round " + round + ", tryAcquire: #" + 11, false, tokenBucket.tryAcquire());
  }

  @Test
  public void tryAcquireConcurrently() throws InterruptedException {
    SmoothRateLimiter tokenBucket = rateLimiter(10, 10);

    tryAcquireConcurrentlyRound(1, tokenBucket);
    // 睡眠等待桶恢复
    TimeUnit.MILLISECONDS.sleep(1500L);
    tryAcquireConcurrentlyRound(2, tokenBucket);
  }

  private void tryAcquireConcurrentlyRound(int round, SmoothRateLimiter tokenBucket) throws InterruptedException {

    CountDownLatch startLatch1 = new CountDownLatch(11);
    CountDownLatch finishLatch1 = new CountDownLatch(10);

    // 10个线程并发获取token
    for (int i = 0; i < 10; i++) {
      String name = "Round " + round + " , tryAcquire: #" + i;
      Thread thread = new Thread(new TokenConsumer(name, tokenBucket, startLatch1, finishLatch1));
      thread.start();
    }
    startLatch1.countDown();
    finishLatch1.await();

    // 应该被禁止了
    assertEquals(false, tokenBucket.tryAcquire());
  }

  private class TokenConsumer implements Runnable {

    private final String name;

    private final CountDownLatch startLatch;

    private final CountDownLatch finishLatch;

    private final SmoothRateLimiter rateLimiter;

    public TokenConsumer(String name,
        SmoothRateLimiter rateLimiter,
        CountDownLatch startLatch,
        CountDownLatch finishLatch) {
      this.name = name;
      this.startLatch = startLatch;
      this.finishLatch = finishLatch;
      this.rateLimiter = rateLimiter;
    }

    @Override
    public void run() {
      startLatch.countDown();
      try {
        startLatch.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
      assertEquals(name, true, rateLimiter.tryAcquire());
      finishLatch.countDown();
    }
  }
}
