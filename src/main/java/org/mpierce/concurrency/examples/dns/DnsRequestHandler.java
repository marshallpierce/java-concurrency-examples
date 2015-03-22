package org.mpierce.concurrency.examples.dns;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DnsRequestHandler extends SimpleChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(DnsRequestHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {

        ChannelBuffer buffer = (ChannelBuffer) event.getMessage();

        DnsRequest dnsRequest;
        try {
            dnsRequest = parseDnsRequest(buffer);
        } catch (InvalidRequestException e) {
            logger.warn("Invalid request", e);
            // TODO write error back
            return;
        }

        DnsResponse response = getResponse(dnsRequest);

        // for now just make it large
        ChannelBuffer writeBuffer = ChannelBuffers.buffer(response.getEncodedLength());

        response.write(writeBuffer);

        event.getChannel().write(writeBuffer, event.getRemoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        //noinspection ThrowableResultOfMethodCallIgnored
        logger.warn("Exception", e.getCause());
    }

    private DnsResponse getResponse(DnsRequest dnsRequest) {
        List<ResourceRecord> records = new ArrayList<ResourceRecord>();

        for (DnsQuery dnsQuery : dnsRequest.getQueries()) {
            ResourceRecord resourceRecord =
                    new ResourceRecord(dnsQuery.getName(), RRType.A, RRClass.IN, 0, new byte[]{(byte) 0x7F, (byte) 0x00,
                            (byte) 0x00, (byte) 0x02});

            records.add(resourceRecord);
        }

        return new DnsResponse(dnsRequest.getTransactionId(), ResponseCode.NO_ERROR, dnsRequest.getQueries(), records);
    }

    private DnsRequest parseDnsRequest(ChannelBuffer buffer) throws InvalidRequestException {
        // DNS header is 6 groups of 2 bytes
        if (buffer.readableBytes() < (2 * 6)) {
            return null;
        }


        // first 2 bytes are tx id
        short txId = buffer.readShort();

        // first byte in stream is most significant byte in short
        short flags = buffer.readShort();

        boolean isQuery = !BitHelper.getBit(flags, 0);
        boolean isAA = BitHelper.getBit(flags, 5);
        boolean isTruncated = BitHelper.getBit(flags, 6);
        boolean recursionDesired = BitHelper.getBit(flags, 7);
        boolean recursionAvailable = BitHelper.getBit(flags, 8);

        // this byte is always positive since only first 4 bits are used
        byte opcodeByte = BitHelper.getByteStartingAtBit(flags, 1, 4);

        byte zeroes = BitHelper.getByteStartingAtBit(flags, 9, 3);
        byte rcode = BitHelper.getByteStartingAtBit(flags, 13, 3);

        int queryCount = buffer.readUnsignedShort();

        // these 3 should always be 0
        int answerCount = buffer.readUnsignedShort();
        int nsCount = buffer.readUnsignedShort();
        int additionalRecCount = buffer.readUnsignedShort();


        List<DnsQuery> dnsQueries;
        dnsQueries = parseQueries(queryCount, buffer);

        return new DnsRequest(txId, isQuery, Opcode.getForIntCode(opcodeByte), isAA, isTruncated, recursionDesired,
                recursionAvailable,
                dnsQueries);
    }

    private static List<DnsQuery> parseQueries(int numQueries, ChannelBuffer buffer) throws InvalidRequestException {
        List<DnsQuery> queries = new ArrayList<DnsQuery>();

        List<DnsLabel> labels = new ArrayList<DnsLabel>();

        for (int i = 0; i < numQueries; i++) {
            while (true) {
                short nameLength = buffer.readUnsignedByte();

                if (nameLength == 0) {
                    // length octet == 0 means end of qname
                    break;
                }

                // need to make a new buf each time to pass ownership to the DnsLabel obj.
                byte[] nameBuf = new byte[nameLength];

                buffer.readBytes(nameBuf);

                // nameBuf now owned by labels list
                labels.add(new DnsLabel(nameBuf));
            }

            int qtype = buffer.readUnsignedShort();
            int qclass = buffer.readUnsignedShort();

            queries.add(new DnsQuery(new DnsName(labels), QType.getForIntCode(qtype), QClass.getForIntCode(qclass)));
        }

        return Collections.unmodifiableList(queries);
    }
}
