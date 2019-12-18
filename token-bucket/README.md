# 令牌桶限流

假象有一个桶，它有容量上限（capacity），有一个人A按照一定速率（rate）往桶里扔令牌（issue），另一个人B则从这个桶里取令牌（acquire）。如果取的速度比扔的快，那么最终桶就会干涸，此时B的请求就被拒绝。如果取的速度比扔的慢，那么桶里的令牌也不会无限多，到其上限为止。

令牌桶算法能够使得在高峰时动用低谷时积攒的令牌，使其在高峰能够抗一下，而不是粗暴的规定请求速度不能超过xxx/秒，具备一定的弹性。

在实现上，并不需要有一个线程负责扔令牌，只需在拿令牌时取当前时间和上一次扔令牌的时间差 * 速率即可。示意代码如下：

```java
public class SynchronizedTokenBucket implements TokenBucket {

  private final int issueRatePerSecond;

  private final int capacity;

  private int tokens;

  private long lastIssueTime;

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
```

本例子实现了三种令牌桶，它们在功能上没有区别，在性能上存在区别：

* [SynchronizedTokenBucket](src/main/java/me/chanjar/codesnippets/tokenbucket/SynchronizedTokenBucket.java)，使用`synchronized`保护`tryAcquire`方法。
* [AtomicTokenBucket](src/main/java/me/chanjar/codesnippets/tokenbucket/AtomicTokenBucket.java)，使用`AtomicInteger`。
* [AtomicFieldUpdaterTokenBucket](src/main/java/me/chanjar/codesnippets/tokenbucket/AtomicFieldUpdaterTokenBucket.java)，使用`AtomicIntegerFieldUpdater`。

可以使用[token-bucket-jmh](../token-bucket-jmh)来测试性能：

```bash
mvn clean package
java -jar target/benchmarks.jar
```

