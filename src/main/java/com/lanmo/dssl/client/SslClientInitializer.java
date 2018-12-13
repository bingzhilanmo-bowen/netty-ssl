package com.lanmo.dssl.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;
import java.util.ArrayList;
import java.util.List;

public class SslClientInitializer extends ChannelInitializer<SocketChannel> {


    private final SslContext sslCtx;
    private final String host;
    private final int port;


    public SslClientInitializer(SslContext sslCtx, String host, int port) {
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        SslHandler sslHandler = sslCtx.newHandler(ch.alloc(),host,port);
        // Ssl 参数，这里主要是做了SNI，通过添加SNI参数可以通过这个参数做SNI的访问控制，server端验证这个参数就能做控制
        List<SNIServerName> sni = new ArrayList<SNIServerName>();
        sni.add(new SNIHostName("bowen"));
        SSLParameters sslParameters = sslHandler.engine().getSSLParameters();
        sslParameters.setServerNames(sni);
        sslHandler.engine().setSSLParameters(sslParameters);

        pipeline.addLast(sslHandler);

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new SslClientHandler());
    }
}
