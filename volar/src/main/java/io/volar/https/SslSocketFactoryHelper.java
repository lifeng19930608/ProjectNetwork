package io.volar.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.volar.configuration.VolarConfiguration;

/**
 * Created by LiShen on 2017/11/28.
 * Https helper
 */

public class SslSocketFactoryHelper {
    /**
     * Get a https ssl params
     * use in {@link VolarConfiguration.Builder#sslSocketFactoryParams(SslSocketFactoryParams)}
     *
     * @param certificates server certificates
     * @param bks          client certificate .bks file
     * @param password     client user password
     * @return SSLSocketFactory and X509TrustManager
     */
    public static SslSocketFactoryParams getSslSocketFactoryParams(InputStream[] certificates, InputStream bks, String password) {
        SslSocketFactoryParams sslSocketFactoryParams = new SslSocketFactoryParams();
        try {
            TrustManager[] trustManagers = generateTrustManager(certificates);
            KeyManager[] keyManagers = generateKeyManagers(bks, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new CombinedX509TrustManager(chooseX509TrustManager(trustManagers));
            } else {
                trustManager = new TrustAllX509TrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslSocketFactoryParams.setSslSocketFactory(sslContext.getSocketFactory());
            sslSocketFactoryParams.setX509TrustManager(trustManager);
        } catch (Exception e) {
            e.printStackTrace();
            sslSocketFactoryParams = new SslSocketFactoryParams();
        }
        return sslSocketFactoryParams;
    }

    private static TrustManager[] generateTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length == 0) {
            return null;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException ignore) {
                }
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static KeyManager[] generateKeyManagers(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) {
                return null;
            }
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static X509TrustManager chooseX509TrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    public static SslSocketFactoryParams getTrustAllSslSocketFactory() {
        SslSocketFactoryParams sslSocketFactoryParams = new SslSocketFactoryParams();
        sslSocketFactoryParams.setX509TrustManager(new TrustAllX509TrustManager());
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{sslSocketFactoryParams.getX509TrustManager()}, new SecureRandom());
            sslSocketFactoryParams.setSslSocketFactory(sc.getSocketFactory());
        } catch (Exception ignore) {
        }
        return sslSocketFactoryParams;
    }
}