package org.hackerdojo.examples.dns;

import org.jboss.netty.buffer.ChannelBuffer;

public interface Encodable {

    /**
     * @return buffer size necessary to encode
     */
    int getEncodedLength();

    void write(ChannelBuffer buffer);

}
