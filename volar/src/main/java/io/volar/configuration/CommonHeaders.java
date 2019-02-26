package io.volar.configuration;


import okhttp3.Headers;

/**
 * Created by LiShen on 2017/11/27.
 * Common headers
 */

public interface CommonHeaders {
    Headers.Builder getCommonHeaders();
}