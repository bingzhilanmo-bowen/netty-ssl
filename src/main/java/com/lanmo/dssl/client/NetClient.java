package com.lanmo.dssl.client;

import com.lanmo.dssl.SslContextFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetClient {

    private static final String HOST = System.getProperty("host","127.0.0.1");
    private static final int PORT = Integer.valueOf(System.getProperty("port","9999"));
    private static final String certPath= System.getProperty("certPath","/home/myCa/nclient/client.cer");
    private static final String keyPath= System.getProperty("keyPath","/home/myCa/nclient/client.pem");


    public static void main(String[] args) {
        try {
            client(HOST,PORT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void client(String host, int port) throws IOException, InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap client = new Bootstrap();
        client.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SslClientInitializer(SslContextFactory.createClientSslContext(certPath,
                        keyPath), host, port));

        Channel ch = client.connect(host,port).sync().channel();
        ChannelFuture lastWriteFuture = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            lastWriteFuture = ch.writeAndFlush(line + "\r\n");
            if ("bye".equals(line.toLowerCase())) {
                ch.closeFuture().sync();
                break;
            }
        }

        // Wait until all messages are flushed before closing the channel.
        if (lastWriteFuture != null) {
            lastWriteFuture.sync();
        }

    }


}
