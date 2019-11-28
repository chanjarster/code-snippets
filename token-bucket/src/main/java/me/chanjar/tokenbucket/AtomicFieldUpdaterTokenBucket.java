package me.chanjar.tokenbucket;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicFieldUpdaterTokenBucket implements TokenBucket {

  private final int issueRatePerSecond;

  private final int capacity;

  private final AtomicIntegerFieldUpdater tokensUpdater =
      AtomicIntegerFieldUpdater.newUpdater(AtomicFieldUpdaterTokenBucket.class, "tokens");

  private volatile int tokens;

  private volatile long lastIssueTime;

  public AtomicFieldUpdaterTokenBucket(int issueRatePerSecond, int capacity) {
    this.issueRatePerSecond = issueRatePerSecond;
    this.lastIssueTime = System.currentTimeMillis();
    this.tokens = capacity;
    this.capacity = capacity;
  }


  @Override
  public boolean tryAcquire() {
    issueTokensIfNecessary();
    int oldValue;
    int newValue;
    do {
      oldValue = tokens;
      if (oldValue <= 0) {
        return false;
      }
      newValue = oldValue - 1;
    } while (!tokensUpdater.compareAndSet(this, oldValue, newValue));
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
      oldValue = tokens;
      issueTokens = Math.min(capacity - oldValue, issueTokens);
      if (issueTokens <= 0) {
        return;
      }
      newValue = oldValue + issueTokens;
    } while (!tokensUpdater.compareAndSet(this, oldValue, newValue));

    lastIssueTime = acquireTime;
  }
}
