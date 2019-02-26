package io.volar.https;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by LiShen on 2017/11/28.
 * SSLSocketFactory and X509TrustManager
 */

public class SslSocketFactoryParams {
    private SSLSocketFactory sslSocketFactory;
    private X509TrustManager x509TrustManager;

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public void setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
    }
}