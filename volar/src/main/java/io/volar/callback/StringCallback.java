package io.volar.callback;


import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * String callback
 */

public interface StringCallback extends BaseCallback {
    void onSuccess(HttpResponse response, String responseString);
}