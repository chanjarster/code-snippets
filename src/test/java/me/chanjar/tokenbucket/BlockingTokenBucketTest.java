package me.chanjar.tokenbucket;

public class BlockingTokenBucketTest extends TokenBucketTestBase {

  @Override
  protected TokenBucket createTokenBucket(int issueRatePerSecond, int capacity) {
    return new BlockingTokenBucket(issueRatePerSecond, capacity);
  }
}
