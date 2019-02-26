package io.volar.callback;


import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Object callback
 */

public interface ObjectCallback<T> extends BaseCallback {
    void onSuccess(HttpResponse response, T t);
}