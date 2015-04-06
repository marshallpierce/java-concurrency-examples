package org.mpierce.concurrency.examples.forkjoin;

import java.util.concurrent.ForkJoinPool;

import java.util.Random;

public class ForkJoinMain {
    private static final int WARMUPS = 100;

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();

        int numItems = 100000000;
        int[] data = new int[numItems];
        Random random = new Random();

        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt();
        }

        doSum(pool, data);

        doSumSquares(pool, data);

        doMax(pool, data);

        doNumPositive(pool, data);
    }

    private static void doSumSquares(ForkJoinPool pool, int[] data) {
        // warmup
        for (int i = 0; i < WARMUPS; i++) {
            pool.invoke(new SumSquaresTask(data, 0, data.length - 1));
        }

        long fjStart = System.currentTimeMillis();
        long fjSum = pool.invoke(new SumSquaresTask(data, 0, data.length - 1));
        long fjEnd = System.currentTimeMillis();

        long simpleStart = System.currentTimeMillis();
        long simpleSum = 0L;
        // compute it the simple way
        for (int i = 0; i < data.length; i++) {
            simpleSum = simpleSum + ((long) data[i]) * ((long) data[i]);
        }
        long simpleEnd = System.currentTimeMillis();

        System.out.println("Got " + fjSum + " in " + (fjEnd - fjStart));
        System.out.println("Simple sum of squares " + simpleSum + " in " + (simpleEnd - simpleStart));
    }

    private static void doNumPositive(ForkJoinPool pool, int[] data) {
        // warmup
        for (int i = 0; i < WARMUPS; i++) {
            pool.invoke(new NumPositiveTask(data, 0, data.length - 1));
        }

        long fjStart = System.currentTimeMillis();
        long fjNumPos = pool.invoke(new NumPositiveTask(data, 0, data.length - 1));
        long fjEnd = System.currentTimeMillis();

        long simpleStart = System.currentTimeMillis();
        long simpleNumPos = 0L;

        // compute it the simple way
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                simpleNumPos++;
            }
        }
        long simpleEnd = System.currentTimeMillis();

        System.out.println("Got " + fjNumPos + " in " + (fjEnd - fjStart));
        System.out.println("Simple num pos " + simpleNumPos + " in " + (simpleEnd - simpleStart));

    }

    private static void doMax(ForkJoinPool pool, int[] data) {
        // warmup
        for (int i = 0; i < WARMUPS; i++) {
            pool.invoke(new MaxTask(data, 0, data.length - 1));
        }

        long fjStart = System.currentTimeMillis();
        long fjMax = pool.invoke(new MaxTask(data, 0, data.length - 1));
        long fjEnd = System.currentTimeMillis();

        long simpleStart = System.currentTimeMillis();
        long simpleMax = Long.MIN_VALUE;

        // compute it the simple way
        for (int i = 0; i < data.length; i++) {
            simpleMax = data[i] > simpleMax ? data[i] : simpleMax;
        }
        long simpleEnd = System.currentTimeMillis();

        System.out.println("Got " + fjMax + " in " + (fjEnd - fjStart));
        System.out.println("Simple max " + simpleMax + " in " + (simpleEnd - simpleStart));

    }

    private static void doSum(ForkJoinPool pool, int[] data) {
        // warmup
        for (int i = 0; i < WARMUPS; i++) {
            pool.invoke(new SumTask(data, 0, data.length - 1));
        }

        long fjStart = System.currentTimeMillis();
        long fjSum = pool.invoke(new SumTask(data, 0, data.length - 1));
        long fjEnd = System.currentTimeMillis();

        long simpleStart = System.currentTimeMillis();
        long simpleSum = 0L;
        // compute it the simple way
        for (int i = 0; i < data.length; i++) {
            simpleSum = simpleSum + (long) data[i];
        }
        long simpleEnd = System.currentTimeMillis();

        System.out.println("Got " + fjSum + " in " + (fjEnd - fjStart));
        System.out.println("Simple sum " + simpleSum + " in " + (simpleEnd - simpleStart));
    }
}
