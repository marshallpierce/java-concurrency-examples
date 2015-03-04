package org.hackerdojo.examples.dns;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DnsClientMain {

    private static final Logger logger = LoggerFactory.getLogger(DnsClientMain.class);

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        int port = 53535;

        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<Void> cs = new ExecutorCompletionService<Void>(executor);

        int numSenders = 10;
        int requestsPerSender = 1000;

        CyclicBarrier latch = new CyclicBarrier(numSenders);

        InetAddress addr = InetAddress.getLocalHost();

        // once for warmup
        runBatch(port, cs, numSenders, requestsPerSender, latch, addr);

        DateTime start = new DateTime();
        runBatch(port, cs, numSenders, requestsPerSender, latch, addr);
        Duration dur = new Duration(start, null);


        int total = numSenders * requestsPerSender;
        logger.info(total + " took " + dur.getMillis() + "ms = " + (total / (double) dur.getMillis()) * 1000 + "/sec");

        executor.shutdown();
    }

    private static void runBatch(int port, CompletionService<Void> cs, int numSenders, int requestsPerSender,
                                 CyclicBarrier latch, InetAddress addr) throws InterruptedException {
        for (int i = 0; i < numSenders; i++) {
            cs.submit(new DnsRequester(latch, requestsPerSender, addr, port));
        }

        for (int i = 0; i < numSenders; i++) {
            try {
                cs.take().get();
            } catch (ExecutionException e) {
                logger.warn("Requester failed", e);
            }
        }
    }

    private static class DnsRequester implements Callable<Void> {

        private final CyclicBarrier barrier;

        private final int numRequests;

        private final InetAddress address;

        private final int port;

        public DnsRequester(CyclicBarrier barrier, int numRequests, InetAddress address, int port) {
            this.barrier = barrier;
            this.numRequests = numRequests;
            this.address = address;
            this.port = port;
        }

        @Override
        public Void call() throws Exception {
            doIt();

            return null;
        }

        private void doIt() throws InterruptedException, BrokenBarrierException, IOException {
            barrier.await();

            DnsQuery query = new DnsQuery(new DnsName("foo.com"), QType.A, QClass.IN);

            DnsRequest request =
                    new DnsRequest((short) 42, true, Opcode.QUERY, false, false, false, false, Arrays.asList(query));

            ChannelBuffer buffer = ChannelBuffers.buffer(request.getEncodedLength());

            request.write(buffer);

            int length = buffer.readableBytes();
            byte[] byteBuf = new byte[length];
            buffer.readBytes(byteBuf);

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket sendPacket = new DatagramPacket(byteBuf, byteBuf.length, address, port);

            byte[] recvBuf = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

            for (int i = 0; i < numRequests; i++) {
                socket.send(sendPacket);
                socket.receive(recvPacket);
            }
        }
    }
}
