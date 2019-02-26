package io.volar.callback;


import org.json.JSONArray;

import io.volar.HttpResponse;

/**
 * Created by LiShen on 2017/11/27.
 * Json array callback
 */

public interface JsonArrayCallback extends BaseCallback {
    void onSuccess(HttpResponse response, JSONArray jsonArray);
}