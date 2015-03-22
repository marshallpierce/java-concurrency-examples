package org.mpierce.concurrency.examples.publication.field;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class FieldPublicationMain {

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

    static class VolatileHolder {
        volatile int x;
        volatile int y;
    }
}
