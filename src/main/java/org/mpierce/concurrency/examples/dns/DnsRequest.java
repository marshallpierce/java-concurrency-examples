package org.mpierce.concurrency.examples.dns;

import net.jcip.annotations.Immutable;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Collections;
import java.util.List;

@Immutable
public class DnsRequest implements Encodable {

    private final short transactionId;

    private final boolean query;

    private final Opcode opcode;

    private final boolean authoritativeAnswer;

    private final boolean truncated;

    private final boolean recursionDesired;

    private final boolean recursionAvailable;

    private final List<DnsQuery> queries;

    public DnsRequest(short transactionId, boolean query, Opcode opcode, boolean authoritativeAnswer, boolean truncated,
                      boolean recursionDesired, boolean recursionAvailable, List<DnsQuery> queries) {
        this.transactionId = transactionId;
        this.query = query;
        this.opcode = opcode;
        this.authoritativeAnswer = authoritativeAnswer;
        this.truncated = truncated;
        this.recursionDesired = recursionDesired;
        this.recursionAvailable = recursionAvailable;
        this.queries = Collections.unmodifiableList(queries);
    }

    public short getTransactionId() {
        return transactionId;
    }

    public boolean isQuery() {
        return query;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public boolean isAuthoritativeAnswer() {
        return authoritativeAnswer;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public boolean isRecursionDesired() {
        return recursionDesired;
    }

    public boolean isRecursionAvailable() {
        return recursionAvailable;
    }

    public List<DnsQuery> getQueries() {
        return queries;
    }

    @Override
    public int getEncodedLength() {
        int total = 12;

        for (DnsQuery dnsQuery : this.queries) {
            total += dnsQuery.getEncodedLength();
        }

        return total;
    }

    @Override
    public void write(ChannelBuffer buffer) {
        buffer.writeShort(this.transactionId);

        // all zeros is a valid query
        buffer.writeShort((short) 0);

        buffer.writeShort(this.queries.size());

        buffer.writeShort(0);
        buffer.writeShort(0);
        buffer.writeShort(0);

        for (DnsQuery dnsQuery : this.queries) {
            dnsQuery.write(buffer);
        }

    }
}
