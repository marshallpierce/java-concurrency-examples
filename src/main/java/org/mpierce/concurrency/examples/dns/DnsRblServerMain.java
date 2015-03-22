package org.mpierce.concurrency.examples.dns;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class DnsRblServerMain {

    public static void main(String[] args) {

        ChannelFactory factory =
                new NioDatagramChannelFactory(Executors.newCachedThreadPool());

        ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new DnsRequestHandler());
            }
        });


        bootstrap.bind(new InetSocketAddress(53535));
    }
}
