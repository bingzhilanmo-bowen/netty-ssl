package com.lanmo.dssl;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import java.io.*;

public class SslContextFactory {


    public static SslContext createClientSslContext(String certPath, String keyPath, String rootCrt) throws IOException {
        InputStream keyCertChainInputStream = getByPath(certPath);
        InputStream keyInputStream = getByPath(keyPath);
        InputStream rootInputStream = getByPath(rootCrt);


        SslContext client = SslContextBuilder
                .forClient()
                .sslProvider(SslProvider.OPENSSL)
                .keyManager(keyCertChainInputStream,keyInputStream)
                .trustManager(rootInputStream)
                .build();

        close(keyCertChainInputStream);
        close(keyInputStream);
        close(rootInputStream);

        return client;
    }

    public static SslContext createServerSslContext(String certPath, String keyPath, String rootCrt) throws IOException {
        InputStream keyCertChainInputStream = getByPath(certPath);
        InputStream keyInputStream = getByPath(keyPath);
        InputStream rootInputStream = getByPath(rootCrt);


        SslContext client = SslContextBuilder
                .forServer(keyCertChainInputStream,keyInputStream)
                .sslProvider(SslProvider.OPENSSL)
                .trustManager(rootInputStream)
                .clientAuth(ClientAuth.REQUIRE)
                .build();

        close(keyCertChainInputStream);
        close(keyInputStream);
        close(rootInputStream);

        return client;
    }


    private static InputStream getByPath(String filePath) throws FileNotFoundException {
        File certFile = new File(filePath);
        if(certFile.exists()){
            return new FileInputStream(certFile);
        }
        throw new IllegalStateException("Error !!!!");
    }

    private static
    void close(InputStream stream) throws IOException {
        if(stream!=null){
            stream.close();
        }
    }

}
