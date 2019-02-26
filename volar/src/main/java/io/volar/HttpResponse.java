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

import io.volar.configuration.CustomErrorMessages;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by LiShen on 2017/11/27.
 * Http Response
 */

public final class HttpResponse<T> {

    public static final int SUCCESS = HttpConstant.Code.SUCCESS;
    public static final int NETWORK_ERROR = HttpConstant.Code.DATA_PARSE_FAILURE;
    public static final int DATA_PARSE_FAILURE = HttpConstant.Code.DATA_PARSE_FAILURE;
    public static final int SERVER_NO_RESPONSE = HttpConstant.Code.SERVER_NO_RESPONSE;

    public String url;
    public Call call;
    public Headers headers;
    public Response response;
    public String message;
    public Exception exception;
    public boolean canceled;
    public int code;
    public boolean success;
    public String responseString;
    public T responseData;
    public Class responseDataClass;
    public long requestCostTime;
    public long parseDataCostTime;
    public int callbackType;
    public boolean noNeedCallback;

    private Object extra;

    /**
     * Only use once
     *
     * @return
     */
    public Object getExtra() {
        Object extra = this.extra;
        this.extra = null;
        return extra;
    }

    void setExtra(Object extra) {
        this.extra = extra;
    }

    public void setError(int errorCode) {
        success = false;
        code = errorCode;
        CustomErrorMessages customErrorMessages = Volar.getDefault().getConfiguration().getCustomErrorMessages();
        switch (errorCode) {
            case HttpConstant.Code.NETWORK_ERROR:
                if (customErrorMessages != null) {
                    message = customErrorMessages.networkError();
                } else {
                    message = HttpConstant.ErrorMessages.NETWORK_ERROR;
                }
                break;
            case HttpConstant.Code.SERVER_NO_RESPONSE:
                if (customErrorMessages != null) {
                    message = customErrorMessages.serverNoResponse();
                } else {
                    message = HttpConstant.ErrorMessages.SERVER_NO_RESPONSE;
                }
                break;
            case HttpConstant.Code.DATA_PARSE_FAILURE:
                if (customErrorMessages != null) {
                    message = customErrorMessages.dataParseFailed();
                } else {
                    message = HttpConstant.ErrorMessages.DATA_PARSE_FAILURE;
                }
                break;
            default:
                if (customErrorMessages != null) {
                    message = customErrorMessages.otherError(errorCode);
                } else {
                    message = HttpConstant.ErrorMessages.OTHER_ERROR;
                }
                break;
        }
    }
}