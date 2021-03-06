package me.chanjar.codesnippets.tokenbucket;

public class AtomicFieldUpdaterTokenBucketTest extends TokenBucketTestBase {

  @Override
  protected TokenBucket createTokenBucket(int issueRatePerSecond, int capacity) {
    return new AtomicFieldUpdaterTokenBucket(issueRatePerSecond, capacity);
  }
}

