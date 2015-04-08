package org.mpierce.concurrency.examples.parstreams;

import java.util.SplittableRandom;

final class ParallelStreamMain {
    public static void main(String[] args) {
        System.out.println("Starting");

        long sum = new SplittableRandom().ints(100_000_000)
            .parallel()
            .mapToLong(x -> (long) x * x)
            .sum();

        System.out.println("Sum of squares = " + sum);
    }
}
