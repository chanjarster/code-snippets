package me.chanjar.codesnippets;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class CacheTest {

  @Test
  public void test() throws InterruptedException {
    Cache<String, Integer> cache = new Cache<>();
    ExpiryPolicy expiryPolicy = new ExpiryPolicy(TimeUnit.SECONDS.toMillis(5), cache);
    cache.setExpiryPolicy(expiryPolicy);
    expiryPolicy.start();

    cache.put("foo", 1);
    cache.put("bar", 2);

    assertEquals(cache.get("foo"), Integer.valueOf(1));
    assertEquals(cache.get("bar"), Integer.valueOf(2));

    TimeUnit.SECONDS.sleep(8L);

    assertEquals(cache.get("foo"), null);
    assertEquals(cache.get("bar"), null);
  }

}
