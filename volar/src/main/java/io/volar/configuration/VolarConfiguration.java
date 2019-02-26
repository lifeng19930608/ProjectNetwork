package io.volar.configuration;

import android.text.TextUtils;

import java.net.Proxy;

import javax.net.ssl.HostnameVerifier;

import io.volar.HttpConstant;
import io.volar.https.SslSocketFactoryParams;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by LiShen on 2017/11/27.
 * NetworkConfiguration
 */

public final class VolarConfiguration {
    private boolean logEnabled;
    private String logTag;
    private boolean logHeader;
    private HttpConstant.LogLevel logLevel;
    private CommonHeaders commonHeaders;
    private CustomFilter customFilter;
    private CustomErrorMessages customErrorMessages;
    private boolean trustAllHttps;
    private boolean logParamsBeforeFilter;
    private boolean logResponseBeforeFilter;
    // okhttp
    private long connectTimeout;
    private long readTimeout;
    private long writeTimeout;
    private SslSocketFactoryParams sslSocketFactoryParams;
    private HostnameVerifier hostnameVerifier;
    private Dns dns;
    private CookieJar cookieJar;
    private boolean retryOnConnectionFailure;
    private boolean followSslRedirects;
    private boolean followRedirects;
    private Cache cache;
    private Proxy proxy;

    private VolarConfiguration(Builder builder) {
        logEnabled = builder.logEnabled;
        logTag = builder.logTag;
        logHeader = builder.logHeader;
        logLevel = builder.logLevel;
        commonHeaders = builder.commonHeaders;
        customFilter = builder.customFilter;
        customErrorMessages = builder.customErrorMessages;
        trustAllHttps = builder.trustAllHttps;
        logParamsBeforeFilter = builder.logParamsBeforeFilter;
        logResponseBeforeFilter = builder.logResponseBeforeFilter;
        connectTimeout = builder.connectTimeout;
        readTimeout = builder.readTimeout;
        writeTimeout = builder.writeTimeout;
        sslSocketFactoryParams = builder.sslSocketFactoryParams;
        hostnameVerifier = builder.hostnameVerifier;
        dns = builder.dns;
        cookieJar = builder.cookieJar;
        retryOnConnectionFailure = builder.retryOnConnectionFailure;
        followSslRedirects = builder.followSslRedirects;
        followRedirects = builder.followRedirects;
        cache = builder.cache;
        proxy = builder.proxy;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public String getLogTag() {
        return logTag;
    }

    public boolean isLogHeader() {
        return logHeader;
    }

    public HttpConstant.LogLevel getLogLevel() {
        return logLevel;
    }

    public CommonHeaders getCommonHeaders() {
        return commonHeaders;
    }

    public CustomFilter getCustomFilter() {
        return customFilter;
    }

    public CustomErrorMessages getCustomErrorMessages() {
        return customErrorMessages;
    }

    public boolean isTrustAllHttps() {
        return trustAllHttps;
    }

    public boolean isLogParamsBeforeFilter() {
        return logParamsBeforeFilter;
    }

    public boolean isLogResponseBeforeFilter() {
        return logResponseBeforeFilter;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public SslSocketFactoryParams getSslSocketFactoryParams() {
        return sslSocketFactoryParams;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public Dns getDns() {
        return dns;
    }

    public CookieJar getCookieJar() {
        return cookieJar;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public boolean isFollowSslRedirects() {
        return followSslRedirects;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public Cache getCache() {
        return cache;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private boolean logEnabled;
        private String logTag;
        private HttpConstant.LogLevel logLevel;
        private CommonHeaders commonHeaders;
        private CustomFilter customFilter;
        private CustomErrorMessages customErrorMessages;
        private boolean trustAllHttps;
        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private SslSocketFactoryParams sslSocketFactoryParams;
        private HostnameVerifier hostnameVerifier;
        private Dns dns;
        private CookieJar cookieJar;
        private boolean retryOnConnectionFailure;
        private boolean followSslRedirects;
        private boolean followRedirects;
        private Cache cache;
        private Proxy proxy;
        private boolean logParamsBeforeFilter;
        private boolean logResponseBeforeFilter;
        private boolean logHeader;

        public Builder() {
            logEnabled = true;
            logTag = HttpConstant.DEFAULT_LOG_TAG;
            logLevel = HttpConstant.LogLevel.I;
            connectTimeout = HttpConstant.DEFAULT_CONNECT_TIMEOUT;
            readTimeout = HttpConstant.DEFAULT_READ_TIMEOUT;
            writeTimeout = HttpConstant.DEFAULT_WRITE_TIMEOUT;
            cookieJar = CookieJar.NO_COOKIES;
            hostnameVerifier = OkHostnameVerifier.INSTANCE;
            dns = Dns.SYSTEM;
            followSslRedirects = true;
            followRedirects = true;
            retryOnConnectionFailure = true;
            trustAllHttps = false;
            logResponseBeforeFilter = false;
            logParamsBeforeFilter = false;
            logHeader = true;
        }

        private Builder(VolarConfiguration configuration) {
            logEnabled = configuration.logEnabled;
            logTag = configuration.logTag;
            logLevel = configuration.logLevel;
            connectTimeout = configuration.connectTimeout;
            readTimeout = configuration.readTimeout;
            writeTimeout = configuration.writeTimeout;
            cookieJar = configuration.cookieJar;
            hostnameVerifier = configuration.hostnameVerifier;
            dns = configuration.dns;
            followSslRedirects = configuration.followSslRedirects;
            followRedirects = configuration.followRedirects;
            retryOnConnectionFailure = configuration.retryOnConnectionFailure;
            trustAllHttps = configuration.trustAllHttps;
            logResponseBeforeFilter = configuration.logResponseBeforeFilter;
            logParamsBeforeFilter = configuration.logParamsBeforeFilter;
            logHeader = configuration.logHeader;
        }

        public Builder logEnabled(boolean val) {
            logEnabled = val;
            return this;
        }

        public Builder logTag(String val) {
            if (!TextUtils.isEmpty(val)) {
                logTag = val;
            }
            return this;
        }

        public Builder commonHeaders(CommonHeaders val) {
            commonHeaders = val;
            return this;
        }

        public Builder customFilter(CustomFilter val) {
            customFilter = val;
            return this;
        }

        public Builder customErrorMessages(CustomErrorMessages val) {
            customErrorMessages = val;
            return this;
        }

        public Builder connectTimeout(long val) {
            if (val > 0) {
                connectTimeout = val;
            }
            return this;
        }

        public Builder readTimeout(long val) {
            if (val > 0) {
                readTimeout = val;
            }
            return this;
        }

        public Builder writeTimeout(long val) {
            if (val > 0) {
                writeTimeout = val;
            }
            return this;
        }

        public Builder sslSocketFactoryParams(SslSocketFactoryParams val) {
            sslSocketFactoryParams = val;
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier val) {
            if (val != null) {
                hostnameVerifier = val;
            }
            return this;
        }

        public Builder dns(Dns val) {
            if (val != null) {
                dns = val;
            }
            return this;
        }

        public Builder cookieJar(CookieJar val) {
            if (val != null) {
                cookieJar = val;
            }
            return this;
        }

        public Builder retryOnConnectionFailure(boolean val) {
            retryOnConnectionFailure = val;
            return this;
        }

        public Builder followSslRedirects(boolean val) {
            followSslRedirects = val;
            return this;
        }

        public Builder followRedirects(boolean val) {
            followRedirects = val;
            return this;
        }

        public Builder cache(Cache val) {
            cache = val;
            return this;
        }

        public Builder proxy(Proxy val) {
            proxy = val;
            return this;
        }

        public Builder trustAllHttps(boolean val) {
            trustAllHttps = val;
            return this;
        }

        public Builder logParamsBeforeFilter(boolean val) {
            logParamsBeforeFilter = val;
            return this;
        }

        public Builder logResponseBeforeFilter(boolean val) {
            logResponseBeforeFilter = val;
            return this;
        }

        public Builder logLevel(HttpConstant.LogLevel val) {
            logLevel = val;
            return this;
        }

        public Builder logHeader(boolean val) {
            logHeader = val;
            return this;
        }

        public VolarConfiguration build() {
            return new VolarConfiguration(this);
        }
    }
}