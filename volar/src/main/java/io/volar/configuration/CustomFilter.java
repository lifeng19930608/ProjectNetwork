package io.volar.configuration;


import io.volar.HttpParams;
import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Custom response filter
 */

public interface CustomFilter {
    /**
     * Filter the {@link io.volar.HttpResponse#responseString}
     * custom define the {@link io.volar.HttpResponse#code}
     * custom define the {@link io.volar.HttpResponse#success}
     * custom define the error and error message by use{@link io.volar.HttpResponse#setError(int)}
     *
     * @param httpResponse original response
     * @return filtered response
     */
    HttpResponse filter(HttpResponse httpResponse);

    HttpParams filter(HttpParams httpParams);
}