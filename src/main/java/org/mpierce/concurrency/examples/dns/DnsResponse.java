package org.mpierce.concurrency.examples.dns;

import javax.annotation.concurrent.Immutable;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.List;

@Immutable
public class DnsResponse implements Encodable {

    private final short transactionId;

    private final ResponseCode responseCode;
    private final List<DnsQuery> queries;
    private final List<ResourceRecord> records;

    public DnsResponse(short transactionId, ResponseCode responseCode, List<DnsQuery> queries,
                       List<ResourceRecord> records) {
        this.transactionId = transactionId;
        this.responseCode = responseCode;
        this.queries = queries;
        this.records = records;
    }

    @Override
    public int getEncodedLength() {
        // header
        int total = 12;

        for (DnsQuery query : this.queries) {
            total += query.getEncodedLength();
        }

        for (ResourceRecord record : this.records) {
            total += record.getEncodedLength();
        }

        return total;
    }

    @Override
    public void write(ChannelBuffer buffer) {
        buffer.writeShort(transactionId);

        // this is a response, so first bit = 1 (remember endianness)
        // TODO include response code
        buffer.writeShort((short) 0x8000);

        buffer.writeShort(this.queries.size());
        buffer.writeShort(this.records.size());

        // no authorities
        buffer.writeShort(0);
        // no additional
        buffer.writeShort(0);

        for (DnsQuery query : this.queries) {
            query.write(buffer);
        }

        for (ResourceRecord record : this.records) {
            record.write(buffer);
        }
    }
}
