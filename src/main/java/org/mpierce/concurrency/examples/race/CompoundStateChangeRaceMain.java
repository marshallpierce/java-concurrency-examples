package org.mpierce.concurrency.examples.race;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

final class CompoundStateChangeRaceMain {

    static final Holder holder = new Holder();

    public static void main(String[] args) {
        final ExecutorService ex = Executors.newCachedThreadPool();

        ex.submit(() -> {
            int i = 0;
            while (true) {
                int next = i++;
                holder.x = next;
                holder.y = next;
            }
        });

        ex.submit(() -> {
            int ok = 0;
            int failed = 0;
            for (int i = 0; i < 100000000; i++) {
                int localX = holder.x;
                int localY = holder.y;

                if (localX != localY) {
                    failed++;
                } else {
                    ok++;
                }
            }

            System.out.println("Ok: " + ok + ", failed: " + failed);
        });
    }

    static class Holder {
        int x;
        int y;
    }

    // same result: no data race, but still a race condition
    static class VolatileHolder {
        volatile int x;
        volatile int y;
    }

    // same as VolatileHolder
    static class AtomicIntHolder {
        final AtomicInteger x = new AtomicInteger();
        final AtomicInteger y = new AtomicInteger();
    }
}
