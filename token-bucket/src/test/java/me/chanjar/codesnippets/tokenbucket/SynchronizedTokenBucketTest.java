package me.chanjar.codesnippets.tokenbucket;

public class SynchronizedTokenBucketTest extends TokenBucketTestBase {

  @Override
  protected TokenBucket createTokenBucket(int issueRatePerSecond, int capacity) {
    return new SynchronizedTokenBucket(issueRatePerSecond, capacity);
  }
}
