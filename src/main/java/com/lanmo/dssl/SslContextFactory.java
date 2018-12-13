package com.lanmo.dssl;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import java.io.*;

public class SslContextFactory {


    public static SslContext createClientSslContext(String certPath, String keyPath) throws IOException {
        InputStream keyCertChainInputStream = getByPath(certPath);
        InputStream keyInputStream = getByPath(keyPath);

        SslContext client = SslContextBuilder
                .forClient()
                .sslProvider(SslProvider.OPENSSL)
                .keyManager(keyCertChainInputStream,keyInputStream)
                .trustManager(MyTrustManagerFactory.MY_TMF)
                .build();

        close(keyCertChainInputStream);
        close(keyInputStream);

        return client;
    }

    public static SslContext createServerSslContext(String certPath, String keyPath) throws IOException {
        InputStream keyCertChainInputStream = getByPath(certPath);
        InputStream keyInputStream = getByPath(keyPath);


        SslContext client = SslContextBuilder
                .forServer(keyCertChainInputStream,keyInputStream)
                .sslProvider(SslProvider.OPENSSL)
                .trustManager(MyTrustManagerFactory.MY_TMF)
                .clientAuth(ClientAuth.REQUIRE)
                .build();

        close(keyCertChainInputStream);
        close(keyInputStream);

        return client;
    }


    public static InputStream getByPath(String filePath) throws FileNotFoundException {
        File certFile = new File(filePath);
        if(certFile.exists()){
            return new FileInputStream(certFile);
        }
        throw new IllegalStateException("Error !!!!");
    }

    public static void close(InputStream stream) throws IOException {
        if(stream!=null){
            stream.close();
        }
    }

}
