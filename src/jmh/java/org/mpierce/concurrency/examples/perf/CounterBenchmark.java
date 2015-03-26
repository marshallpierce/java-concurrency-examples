package org.mpierce.concurrency.examples.perf;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class CounterBenchmark {

    @State(Scope.Group)
    public static class GroupState {
        final AtomicLong atomicLong = new AtomicLong();
        final LongAdder longAdder = new LongAdder();
    }

    @Benchmark
    @Group("oneThreadAtomic")
    @GroupThreads(1)
    public void singleThreadAtomic(GroupState groupState) {
        groupState.atomicLong.incrementAndGet();
    }

    @Benchmark
    @Group("oneThreadAdder")
    @GroupThreads(1)
    public void singleThreadAdder(GroupState groupState) {
        groupState.longAdder.increment();
    }

    @Benchmark
    @Group("fourThreadAtomic")
    @GroupThreads(4)
    public void fourThreadAtomic(GroupState groupState) {
        groupState.atomicLong.incrementAndGet();
    }

    @Benchmark
    @Group("fourThreadAdder")
    @GroupThreads(4)
    public void fourThreadAdder(GroupState groupState) {
        groupState.longAdder.increment();
    }
}
