package io.volar.callback;


import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Base callback
 */

public interface BaseCallback {
    void onFailure(HttpResponse response, int errorCode, String errorMessage);
}