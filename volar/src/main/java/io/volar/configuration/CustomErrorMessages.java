package io.volar.configuration;

/**
 * Created by LiShen on 2017/11/27.
 * Custom error messages
 */

public interface CustomErrorMessages {
    String networkError();

    String serverNoResponse();

    String dataParseFailed();

    String otherError(int errorCode);
}