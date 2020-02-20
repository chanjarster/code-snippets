package me.chanjar.codesnippets;

import java.util.Objects;

public class Node<K, V> {
  private K key;
  private V value;
  private long writeTimestamp;

  public Node(K key, V value) {
    this.key = key;
    this.value = value;
    this.writeTimestamp = System.currentTimeMillis();
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public long getWriteTimestamp() {
    return writeTimestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Node<?, ?> node = (Node<?, ?>) o;
    return writeTimestamp == node.writeTimestamp &&
        Objects.equals(key, node.key) &&
        Objects.equals(value, node.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, writeTimestamp);
  }

}
