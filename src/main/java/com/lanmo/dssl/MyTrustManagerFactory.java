package com.lanmo.dssl;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.EmptyArrays;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class MyTrustManagerFactory extends SimpleTrustManagerFactory {

    public static MyTrustManagerFactory MY_TMF = new MyTrustManagerFactory();

    static final String ROOT_PATCH = "/home/myCa/ca/ca.cer";

    private static X509Certificate ROOT_CERT;


    static {
        InputStream rootCert = null;
        try {
            rootCert = SslContextFactory.getByPath(ROOT_PATCH);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ROOT_CERT = (X509Certificate) cf.generateCertificate(rootCert);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                SslContextFactory.close(rootCert);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    private static final TrustManager tm =new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            X509Certificate client =x509Certificates[0];

            try {
                client.verify(ROOT_CERT.getPublicKey());
            } catch (Exception e) {
                e.printStackTrace();
                throw new CertificateException("failed to verify by root cert");
            }

            //这里获得client 签名时的 subj参数，这里可以利用这里信息做链接管理
            //例如 你本地存储一份client的subj信息，和这里得到的比对，比对上的才让过去
            X500Principal principal =  client.getSubjectX500Principal();

            String s1 =  principal.toString();

            System.out.print("TrustManager: "+s1);
            //do something


        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            X509Certificate serverCert = x509Certificates[0];

            try {
                serverCert.verify(ROOT_CERT.getPublicKey());
            } catch (Exception e) {
                throw new CertificateException("failed to verify by root cert");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    };







    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {

    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {

    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{tm};
    }
}
