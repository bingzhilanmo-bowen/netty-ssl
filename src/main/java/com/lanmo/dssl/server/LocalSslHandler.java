package com.lanmo.dssl.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import java.security.Principal;
import java.util.LinkedList;

public class LocalSslHandler extends SslHandler {

    public static final AttributeKey<LinkedList<String>> REMOTE_IDENTIFIER = AttributeKey.valueOf("remoteIdentifier");

    public LocalSslHandler(SSLEngine engine) {
        super(engine);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture().addListener(future -> {
            if (future.isSuccess()) {
                SSLSession session = engine().getSession();
                if (session != null) {
                    Principal peerPrincipal = session.getPeerPrincipal();
                    Channel channel = (Channel) future.getNow();
                    bindPrincipal(channel, peerPrincipal);
                }
            }
        });

        super.channelActive(ctx);
    }

    private void bindPrincipal(Channel channel, Principal peerPrincipal) {
        LinkedList<String> idChain = new LinkedList<>();
        String identifier = peerPrincipal.toString();
        System.out.println("====================="+identifier);
        if (identifier != null) {
            idChain.add(identifier);
        }
        channel.attr(REMOTE_IDENTIFIER).setIfAbsent(idChain);
    }

}
