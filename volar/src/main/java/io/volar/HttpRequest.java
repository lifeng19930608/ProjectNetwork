/*
 * Copyright 2015 Glow Geniuses Studio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.volar;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import io.volar.callback.BaseCallback;
import io.volar.callback.JsonArrayCallback;
import io.volar.callback.JsonCallback;
import io.volar.callback.ObjectCallback;
import io.volar.callback.ObjectListCallback;
import io.volar.callback.StringCallback;
import io.volar.configuration.VolarConfiguration;
import io.volar.util.JSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by LiShen on 2017/11/27.
 * Http request
 */
class HttpRequest<T> {
    private String url;
    private HttpConstant.Method method;
    private HttpParams httpParams;
    private BaseCallback callback;
    private Class dataClass;
    private WeakReference<Object> tag;
    private VolarConfiguration networkConfiguration;
    private boolean useSeparateOkHttpClient = false;

    private HttpResponse<T> httpResponse;

    private int parseType;

    private long timeMilestone;
    private boolean executed = false;

    private HttpRequest(HttpRequestBuilder builder) {
        url = builder.url;
        method = builder.method;
        httpParams = builder.httpParams;
        callback = builder.callback;
        parseType = builder.parseType;
        dataClass = builder.dataClass;
        tag = new WeakReference<>(builder.tag);
        networkConfiguration = Volar.getDefault().getConfiguration();
        if (builder.separateConfiguration != null) {
            // use separate configuration
            networkConfiguration = builder.separateConfiguration;
            useSeparateOkHttpClient = true;
        }

        httpResponse = new HttpResponse<>();
    }

    private synchronized void execute() {
        if (executed) {
            return;
        }
        executed = true;
        Volar.getDefault().getWorkHandler().post(new Runnable() {
            @Override
            public void run() {
                executeRequest();
            }
        });
    }

    private void executeRequest() {
        String originalJsonStringBody = httpParams.getParamsString();

        // custom filter
        if (networkConfiguration.getCustomFilter() != null) {
            httpParams = networkConfiguration.getCustomFilter().filter(httpParams);
        }

        Request.Builder requestBuilder = new Request.Builder();

        // tag
        if (tag.get() != null) {
            requestBuilder.tag(tag.get());
        }

        // method
        switch (method) {
            case GET:
                url = httpParams.generateUrlWithParams(url);
                requestBuilder.get();
                Volar.getDefault().log("GET URL: " + url);
                break;
            case DELETE:
                requestBuilder.delete(httpParams.getRequestBody());
                Volar.getDefault().log("DELETE URL: " + url);
                break;
            case HEAD:
                url = httpParams.generateUrlWithParams(url);
                Volar.getDefault().log("HEAD URL: " + url);
                break;
            case POST:
                requestBuilder.post(httpParams.getRequestBody());
                Volar.getDefault().log("POST URL: " + url);
                break;
            case PUT:
                requestBuilder.put(httpParams.getRequestBody());
                Volar.getDefault().log("PUT URL: " + url);
                break;
            case PATCH:
                requestBuilder.patch(httpParams.getRequestBody());
                Volar.getDefault().log("PATCH URL: " + url);
                break;
        }

        if (method != HttpConstant.Method.GET && method != HttpConstant.Method.HEAD) {
            if (networkConfiguration.isLogParamsBeforeFilter()) {
                // log original json params
                if (!TextUtils.isEmpty(originalJsonStringBody))
                    Volar.getDefault().log("REQUEST PARAMS: " + originalJsonStringBody);
            } else {
                // log json params after custom filter
                String paramsStr = httpParams.getParamsString();
                if (!TextUtils.isEmpty(paramsStr))
                    Volar.getDefault().log("REQUEST PARAMS: " + paramsStr);
            }
        }

        // url
        requestBuilder.url(url);

        // headers
        if (httpParams.getHeadersBuilder() != null) {
            Headers headers = httpParams.getHeadersBuilder().build();
            requestBuilder.headers(headers);
            if (networkConfiguration.isLogHeader()) {
                String headersStr = headers.toString();
                if (!TextUtils.isEmpty(headersStr))
                    Volar.getDefault().log("REQUEST HEADERS: \n" + headersStr);
            }
        }

        // custom okHttpClient
        OkHttpClient okHttpClient = Volar.getDefault().getOkHttpClient();
        if (useSeparateOkHttpClient) {
            okHttpClient = Volar.getDefault().generateOkHttpClient(networkConfiguration);
        }

        // execute request async
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(null, e, call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(response, null, call);
            }
        });

        timeMilestone = System.currentTimeMillis();
    }

    /**
     * Handle the response, working in non main thread
     */
    private void handleResponse(Response response, Exception exception, Call call) {
        httpResponse.url = url;
        httpResponse.callbackType = parseType;
        httpResponse.response = response;
        httpResponse.exception = exception;
        httpResponse.call = call;
        httpResponse.canceled = call.isCanceled();
        httpResponse.requestCostTime = System.currentTimeMillis() - timeMilestone;
        httpResponse.setExtra(httpParams.getExtra());
        httpResponse.responseDataClass = dataClass;

        timeMilestone = System.currentTimeMillis();

        String originalResponseString = null;

        if (response != null) {
            httpResponse.code = response.code();
            httpResponse.message = response.message();
            httpResponse.success = response.isSuccessful();
            httpResponse.headers = response.headers();

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                try {
                    originalResponseString = responseBody.string();
                } catch (Exception ignore) {
                }
            }

            if (TextUtils.isEmpty(originalResponseString)) {
                httpResponse.setError(HttpConstant.Code.SERVER_NO_RESPONSE);
            } else {
                httpResponse.responseString = originalResponseString;
            }
        } else {
            httpResponse.setError(HttpConstant.Code.NETWORK_ERROR);
        }

        // custom filter
        if (networkConfiguration.getCustomFilter() != null && !TextUtils.isEmpty(httpResponse.responseString)) {
            httpResponse = networkConfiguration.getCustomFilter().filter(httpResponse);
        }

        // data parse
        if (httpResponse.success) {
            if (!parseToData(httpResponse.responseString)) {
                httpResponse.setError(HttpConstant.Code.DATA_PARSE_FAILURE);
            }
        }
        httpResponse.parseDataCostTime = System.currentTimeMillis() - timeMilestone;

        // show original response or not
        if (networkConfiguration.isLogResponseBeforeFilter()) {
            Volar.getDefault().log("RESPONSE: " + originalResponseString, !httpResponse.success);
        } else {
            Volar.getDefault().log("RESPONSE: " + httpResponse.responseString, !httpResponse.success);
        }

        String responseLog = "RESPONSE CODE: " + httpResponse.code;
        if (!TextUtils.isEmpty(httpResponse.message)) {
            responseLog += "\nRESPONSE MESSAGE: " + httpResponse.message;
        }
        responseLog += "\nREQUEST COST TIME: " + httpResponse.requestCostTime + " ms";
        if (httpResponse.parseDataCostTime > 0) {
            responseLog += "\nPARSE DATA COST TIME: " + httpResponse.parseDataCostTime + "ms";
        }
        responseLog += "\nURL: " + httpResponse.url;
        Volar.getDefault().log(responseLog, !httpResponse.success);

        // post to main thread to callback
        if (!httpResponse.noNeedCallback) {
            Volar.getDefault().getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback();
                }
            });
        }
    }

    /**
     * Parse response string to data
     *
     * @param responseString response string
     * @return data
     */
    private boolean parseToData(String responseString) {
        switch (parseType) {
            case HttpConstant.ParseType.PARSE_TYPE_STRING:
                try {
                    httpResponse.responseData = (T) responseString;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpResponse.responseData != null;
            case HttpConstant.ParseType.PARSE_TYPE_JSON:
                try {
                    httpResponse.responseData = (T) new JSONObject(responseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpResponse.responseData != null;
            case HttpConstant.ParseType.PARSE_TYPE_JSON_ARRAY:
                try {
                    httpResponse.responseData = (T) new JSONArray(responseString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpResponse.responseData != null;
            case HttpConstant.ParseType.PARSE_TYPE_OBJECT:
                try {
                    httpResponse.responseData = (T) JSON.parseObject(responseString, dataClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpResponse.responseData != null;
            case HttpConstant.ParseType.PARSE_TYPE_OBJECT_LIST:
                try {
                    httpResponse.responseData = (T) JSON.parseArray(responseString, dataClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpResponse.responseData != null;
        }
        return false;
    }

    /**
     * Callback in main thread
     */
    void callback() {
        if (callback != null) {
            if (httpResponse.success && httpResponse.responseData != null) {
                switch (parseType) {
                    case HttpConstant.ParseType.PARSE_TYPE_STRING:
                        ((StringCallback) callback).onSuccess(httpResponse, (String) httpResponse.responseData);
                        break;
                    case HttpConstant.ParseType.PARSE_TYPE_JSON:
                        ((JsonCallback) callback).onSuccess(httpResponse, (JSONObject) httpResponse.responseData);
                        break;
                    case HttpConstant.ParseType.PARSE_TYPE_JSON_ARRAY:
                        ((JsonArrayCallback) callback).onSuccess(httpResponse, (JSONArray) httpResponse.responseData);
                        break;
                    case HttpConstant.ParseType.PARSE_TYPE_OBJECT:
                        ((ObjectCallback<T>) callback).onSuccess(httpResponse, httpResponse.responseData);
                        break;
                    case HttpConstant.ParseType.PARSE_TYPE_OBJECT_LIST:
                        ((ObjectListCallback<T>) callback).onSuccess(httpResponse, (T[]) httpResponse.responseData);
                        break;
                }
            } else {
                callback.onFailure(httpResponse, httpResponse.code, httpResponse.message);
            }
        }
    }

    public static final class HttpRequestBuilder {
        private String url = "";
        private HttpConstant.Method method;
        private HttpParams httpParams = new HttpParams();
        private BaseCallback callback = null;
        private Class dataClass = null;
        private int parseType = HttpConstant.ParseType.PARSE_TYPE_STRING;
        private Object tag = null;
        private VolarConfiguration separateConfiguration = null;

        HttpRequestBuilder(String url, HttpConstant.Method method) {
            if (url != null)
                this.url = url;
            this.method = method;
        }

        public HttpRequestBuilder params(HttpParams val) {
            if (val != null) {
                httpParams = val;
            }
            return this;
        }

        public <V> HttpRequestBuilder callback(ObjectCallback<V> val1, Class val2) {
            if (val1 != null && val2 != null) {
                callback = val1;
                parseType = HttpConstant.ParseType.PARSE_TYPE_OBJECT;
                dataClass = val2;
            }
            return this;
        }

        public <V> HttpRequestBuilder callback(ObjectListCallback<V> val1, Class val2) {
            if (val1 != null && val2 != null) {
                callback = val1;
                parseType = HttpConstant.ParseType.PARSE_TYPE_OBJECT_LIST;
                dataClass = val2;
            }
            return this;
        }

        public HttpRequestBuilder callback(JsonCallback val1) {
            if (val1 != null) {
                callback = val1;
                parseType = HttpConstant.ParseType.PARSE_TYPE_JSON;
            }
            return this;
        }

        public HttpRequestBuilder callback(JsonArrayCallback val1) {
            if (val1 != null) {
                callback = val1;
                parseType = HttpConstant.ParseType.PARSE_TYPE_JSON_ARRAY;
            }
            return this;
        }

        public HttpRequestBuilder callback(StringCallback val1) {
            if (val1 != null) {
                callback = val1;
                parseType = HttpConstant.ParseType.PARSE_TYPE_STRING;
            }
            return this;
        }

        public HttpRequestBuilder tag(Object val) {
            tag = val;
            return this;
        }

        public HttpRequestBuilder separateConfig(VolarConfiguration configuration) {
            separateConfiguration = configuration;
            return this;
        }

        public <V> void execute() {
            switch (parseType) {
                case HttpConstant.ParseType.PARSE_TYPE_STRING:
                    new HttpRequest<String>(this).execute();
                    break;
                case HttpConstant.ParseType.PARSE_TYPE_JSON:
                    new HttpRequest<JSONObject>(this).execute();
                    break;
                case HttpConstant.ParseType.PARSE_TYPE_JSON_ARRAY:
                    new HttpRequest<JSONArray>(this).execute();
                    break;
                case HttpConstant.ParseType.PARSE_TYPE_OBJECT:
                    new HttpRequest<V>(this).execute();
                    break;
                case HttpConstant.ParseType.PARSE_TYPE_OBJECT_LIST:
                    new HttpRequest<List<V>>(this).execute();
                    break;
            }
        }
    }
}