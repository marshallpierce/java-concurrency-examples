package org.mpierce.concurrency.examples.dns;

import net.jcip.annotations.Immutable;
import org.jboss.netty.buffer.ChannelBuffer;

@Immutable
public class ResourceRecord implements Encodable {

    private final RRType rrtype;

    private final RRClass rrclass;
    private final byte[] data;

    private final DnsName name;
    private final int ttl;

    public ResourceRecord(DnsName name, RRType rrtype, RRClass rrclass, int ttl, byte[] data) {
        this.name = name;
        this.ttl = ttl;
        this.rrtype = rrtype;
        this.rrclass = rrclass;
        this.data = data;
    }

    @Override
    public int getEncodedLength() {
        return 10 + data.length + name.getEncodedLength();
    }

    @Override
    public void write(ChannelBuffer buffer) {
        this.name.write(buffer);
        buffer.writeShort(rrtype.getIntCode());
        buffer.writeShort(rrclass.getIntCode());
        buffer.writeInt(ttl);
        buffer.writeShort(data.length);
        buffer.writeBytes(data);
    }
}
