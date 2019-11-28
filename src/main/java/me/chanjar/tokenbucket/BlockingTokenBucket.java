package me.chanjar.tokenbucket;

public class BlockingTokenBucket implements TokenBucket {

  private final int issueRatePerSecond;

  private final int capacity;

  private int tokens;

  private long lastIssueTime;

  public BlockingTokenBucket(int issueRatePerSecond, int capacity) {
    this.issueRatePerSecond = issueRatePerSecond;
    this.lastIssueTime = System.currentTimeMillis();
    this.tokens = capacity;
    this.capacity = capacity;
  }


  @Override
  public synchronized boolean tryAcquire() {
    issueTokensIfNecessary();
    if (tokens > 0) {
      tokens--;
      return true;
    }
    return false;
  }

  private void issueTokensIfNecessary() {
    long acquireTime = System.currentTimeMillis();
    int issueTokens = (int) ((acquireTime - lastIssueTime) / 1000L * issueRatePerSecond);
    issueTokens = Math.min(capacity - tokens, issueTokens);
    if (issueTokens <= 0) {
      // < 0 是因为时间回拨问题
      return;
    }
    lastIssueTime = acquireTime;
    tokens += issueTokens;
  }

}
