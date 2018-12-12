package com.lanmo.dssl.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SslServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SslServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        SSLEngine sslEngine = sslCtx.newEngine(ch.alloc());

        SSLParameters parameters = sslEngine.getSSLParameters();
        List<SNIMatcher> sniMatcher = new ArrayList<>(1);

        SNIMatcher sniMatcher1 =new SNIMatcher(0) {
            @Override
            public boolean matches(SNIServerName sniServerName) {
                  System.out.print(sniServerName.toString()+"---------------------");
                return Arrays.equals(sniServerName.getEncoded(), "bowen".getBytes());
            }
        };
        sniMatcher.add(sniMatcher1);
        parameters.setSNIMatchers(sniMatcher);
        sslEngine.setSSLParameters(parameters);

        LocalSslHandler localSslHandler =new LocalSslHandler(sslEngine);



        pipeline.addLast(localSslHandler);

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new SslServerHandler());
    }

}
