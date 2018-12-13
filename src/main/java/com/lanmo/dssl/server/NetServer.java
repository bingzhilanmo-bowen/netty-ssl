package com.lanmo.dssl.server;

import com.lanmo.dssl.SslContextFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

public class NetServer {
    private static final String certPath= System.getProperty("certPath","/home/myCa/server/server.cer");
    private static final String keyPath= System.getProperty("keyPath","/home/myCa/server/server.pem");
    public static void main(String[] args) throws Exception {
        SslContext sslCtx = SslContextFactory.createServerSslContext(certPath,keyPath);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SslServerInitializer(sslCtx));

            b.bind(9999).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
