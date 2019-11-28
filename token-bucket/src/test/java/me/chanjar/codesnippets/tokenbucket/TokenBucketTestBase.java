package me.chanjar.codesnippets.tokenbucket;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public abstract class TokenBucketTestBase {

  protected abstract TokenBucket createTokenBucket(int issueRatePerSecond, int capacity);

  @Test
  public void tryAcquire() throws InterruptedException {
    // 容量10，签发速率10/s的桶
    TokenBucket tokenBucket = createTokenBucket(10, 10);

    tryAcquireRound(1, tokenBucket);
    // 睡眠等待桶恢复
    TimeUnit.MILLISECONDS.sleep(1500L);

    tryAcquireRound(2, tokenBucket);
  }

  private void tryAcquireRound(int round, TokenBucket tokenBucket) {
    // 连续获取10个
    for (int i = 0; i < 10; i++) {
      assertEquals("Round " + round + ", tryAcquire: #" + i, true, tokenBucket.tryAcquire());
    }
    // 桶应该干涸了
    assertEquals("Round " + round + ", tryAcquire: #" + 11, false, tokenBucket.tryAcquire());
  }

  @Test
  public void tryAcquireConcurrently() throws InterruptedException {
    TokenBucket tokenBucket = createTokenBucket(10, 10);

    tryAcquireConcurrentlyRound(1, tokenBucket);
    // 睡眠等待桶恢复
    TimeUnit.MILLISECONDS.sleep(1500L);
    tryAcquireConcurrentlyRound(2, tokenBucket);
  }

  private void tryAcquireConcurrentlyRound(int round, TokenBucket tokenBucket) throws InterruptedException {

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

    // 桶应该干涸了
    assertEquals(false, tokenBucket.tryAcquire());
  }

  private class TokenConsumer implements Runnable {

    private final String name;

    private final CountDownLatch startLatch;

    private final CountDownLatch finishLatch;

    private final TokenBucket tokenBucket;

    public TokenConsumer(String name,
        TokenBucket tokenBucket,
        CountDownLatch startLatch,
        CountDownLatch finishLatch) {
      this.name = name;
      this.startLatch = startLatch;
      this.finishLatch = finishLatch;
      this.tokenBucket = tokenBucket;
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
      assertEquals(name, true, tokenBucket.tryAcquire());
      finishLatch.countDown();
    }
  }

}
