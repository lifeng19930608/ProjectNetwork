package io.volar.callback;


import org.json.JSONObject;

import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Json callback
 */

public interface JsonCallback extends BaseCallback {
    void onSuccess(HttpResponse response, JSONObject jsonObject);
}