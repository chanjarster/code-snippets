package me.chanjar.codesnippets;

import java.util.concurrent.ConcurrentHashMap;

public class Cache<K, V> {

  private ConcurrentHashMap<K, Node<K, V>> cacheMap = new ConcurrentHashMap<>();

  private ExpiryPolicy expiryPolicy;

  public void put(K key, V value) {
    Node<K, V> node = new Node<>(key, value);
    cacheMap.put(key, node);
    expiryPolicy.writeNode(node);
  }

  public V remove(K key) {
    Node<K, V> node = cacheMap.remove(key);
    return node == null ? null : node.getValue();
  }

  public V get(K key) {
    Node<K, V> node = cacheMap.get(key);
    return node == null ? null : node.getValue();
  }

  public void setExpiryPolicy(ExpiryPolicy expiryPolicy) {
    this.expiryPolicy = expiryPolicy;
  }

}
