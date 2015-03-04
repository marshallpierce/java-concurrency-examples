package org.hackerdojo.examples.dns;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.ArrayList;
import java.util.List;

public class DnsName implements Encodable {

    private final List<DnsLabel> labels;

    public DnsName(List<DnsLabel> labels) {
        this.labels = labels;
    }

    /**
     * @param name dot-separated domain name
     */
    public DnsName(String name) {
        List<DnsLabel> labels = new ArrayList<DnsLabel>();
        for (String s : Splitter.on(".").split(name)) {
            labels.add(new DnsLabel(s.getBytes(Charsets.US_ASCII)));
        }

        this.labels = labels;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (DnsLabel label : labels) {
            buf.append(label.toString());
            buf.append(".");
        }

        // remove trailing dot
        buf.deleteCharAt(buf.length() - 1);

        return buf.toString();
    }

    @Override
    public int getEncodedLength() {
        // 0-length for end
        int total = 1;
        for (DnsLabel label : this.labels) {
            total += label.getEncodedLength();
        }

        return total;
    }

    @Override
    public void write(ChannelBuffer buffer) {
        for (DnsLabel label : labels) {
            label.write(buffer);
        }
        buffer.writeByte(0);
    }
}
