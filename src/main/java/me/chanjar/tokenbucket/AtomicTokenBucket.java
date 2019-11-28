package me.chanjar.tokenbucket;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicTokenBucket implements TokenBucket {

  private final int issueRatePerSecond;

  private final int capacity;

  private final AtomicInteger tokens;

  private volatile long lastIssueTime;

  public AtomicTokenBucket(int issueRatePerSecond, int capacity) {
    this.issueRatePerSecond = issueRatePerSecond;
    this.lastIssueTime = System.currentTimeMillis();
    this.tokens = new AtomicInteger(capacity);
    this.capacity = capacity;
  }


  @Override
  public boolean tryAcquire() {
    issueTokensIfNecessary();
    int oldValue;
    int newValue;
    do {
      oldValue = tokens.get();
      if (oldValue <= 0) {
        return false;
      }
      newValue = oldValue - 1;
    } while (!tokens.compareAndSet(oldValue, newValue));
    return true;
  }

  private void issueTokensIfNecessary() {
    int oldValue;
    int newValue;
    long acquireTime;
    do {
      acquireTime = System.currentTimeMillis();
      int issueTokens = (int) ((acquireTime - lastIssueTime) / 1000L * issueRatePerSecond);
      // 签发的token上限不得超过capacity
      oldValue = tokens.get();
      issueTokens = Math.min(capacity - oldValue, issueTokens);
      if (issueTokens <= 0) {
        return;
      }
      newValue = oldValue + issueTokens;
    } while (!tokens.compareAndSet(oldValue, newValue));

    lastIssueTime = acquireTime;
  }
}
