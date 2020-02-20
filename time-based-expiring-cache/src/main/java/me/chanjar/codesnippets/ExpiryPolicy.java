package me.chanjar.codesnippets;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiryPolicy {

  private long ttlMillis;

  private Cache cache;

  private ConcurrentLinkedQueue<Node> writeQueues = new ConcurrentLinkedQueue<>();

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public ExpiryPolicy(long ttlMillis, Cache cache) {
    this.ttlMillis = ttlMillis;
    this.cache = cache;
  }

  public void writeNode(Node node) {
    writeQueues.add(node);
  }

  public void start() {
    executor.scheduleWithFixedDelay(new ExpiringTask(), ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
  }

  class ExpiringTask implements Runnable {
    @Override
    public void run() {
      System.out.println("Start purge expired keys");
      while (true) {
        long now = System.currentTimeMillis();
        Node node = ExpiryPolicy.this.writeQueues.peek();
        if (node == null || now - node.getWriteTimestamp() < ttlMillis) {
          break;
        }
        System.out.println("Remove expired key: " + node.getKey());
        ExpiryPolicy.this.writeQueues.remove(node);
        ExpiryPolicy.this.cache.remove(node.getKey());
      }
    }
  }
}
