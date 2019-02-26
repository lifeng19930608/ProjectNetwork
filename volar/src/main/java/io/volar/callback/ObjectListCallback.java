package io.volar.callback;

import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Object list callback
 */

public interface ObjectListCallback<T> extends BaseCallback {
    void onSuccess(HttpResponse response, T[] tList);
}