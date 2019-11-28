package me.chanjar.codesnippets.tokenbucket;

public class AtomicTokenBucketTest extends TokenBucketTestBase {

  @Override
  protected TokenBucket createTokenBucket(int issueRatePerSecond, int capacity) {
    return new AtomicTokenBucket(issueRatePerSecond, capacity);
  }
}
