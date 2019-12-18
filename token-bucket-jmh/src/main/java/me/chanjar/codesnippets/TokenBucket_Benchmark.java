/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package me.chanjar.codesnippets;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import me.chanjar.codesnippets.tokenbucket.AtomicFieldUpdaterTokenBucket;
import me.chanjar.codesnippets.tokenbucket.AtomicTokenBucket;
import me.chanjar.codesnippets.tokenbucket.SynchronizedTokenBucket;
import me.chanjar.codesnippets.tokenbucket.TokenBucket;

public class TokenBucket_Benchmark {


  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(TokenBucket_Benchmark.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }

  @Benchmark
  @Threads(100)
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void test_synchronizedTokenBucket(TokenBucketHolder tokenBucketHolder) {
    tokenBucketHolder.synchronizedTokenBucket.tryAcquire();
  }

  @Benchmark
  @Threads(100)
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void test_atomicBucket(TokenBucketHolder tokenBucketHolder) {
    tokenBucketHolder.atomicBucket.tryAcquire();
  }

  @Benchmark
  @Threads(100)
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void test_atomicFieldUpdaterBucket(TokenBucketHolder tokenBucketHolder) {
    tokenBucketHolder.atomicFieldUpdaterBucket.tryAcquire();
  }

  @State(Scope.Benchmark)
  public static class TokenBucketHolder {
    private final TokenBucket synchronizedTokenBucket = new SynchronizedTokenBucket(100, 10000);

    private final TokenBucket atomicBucket = new AtomicTokenBucket(100, 10000);

    private final TokenBucket atomicFieldUpdaterBucket = new AtomicFieldUpdaterTokenBucket(100, 10000);
  }
}
