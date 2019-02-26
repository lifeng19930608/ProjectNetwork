package io.volar;

/**
 * Created by LiShen on 2017/11/27.
 * Constants
 */

public final class HttpConstant {
    public static final String DEFAULT_LOG_TAG = "Volar";
    public static final long DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
    public static final long DEFAULT_READ_TIMEOUT = 45 * 1000;
    public static final long DEFAULT_WRITE_TIMEOUT = 45 * 1000;

    enum Method {
        GET, POST, PUT, DELETE, HEAD, PATCH
    }

    public enum LogLevel {
        V, I, D, W, E
    }

    static final class ParseType {
        static final int PARSE_TYPE_STRING = 0;
        static final int PARSE_TYPE_JSON = 1;
        static final int PARSE_TYPE_JSON_ARRAY = 2;
        static final int PARSE_TYPE_OBJECT = 3;
        static final int PARSE_TYPE_OBJECT_LIST = 4;
    }

    public static final class ContentType {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String IMAGE_PNG = "image/png; charset=utf-8";
        public static final String TEXT_PLAIN = "text/plain; charset=utf-8";
        public static final String TEXT_XML = "text/xml; charset=utf-8";
        public static final String JSON = "application/json; charset=utf-8";
        public static final String OCTET_STREAM = "application/octet-stream";
        public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String MULTI_PART = "multipart/form-data";
    }

    public static final class Code {
        public static final int SUCCESS = 200;
        public static final int NETWORK_ERROR = -1;
        public static final int DATA_PARSE_FAILURE = -2;
        public static final int SERVER_NO_RESPONSE = 503;
    }

    static final class ErrorMessages {
        static final String NETWORK_ERROR = "Something goes wrong with the network";
        static final String DATA_PARSE_FAILURE = "Data parsing failure";
        static final String SERVER_NO_RESPONSE = "Service unavailable";
        static final String OTHER_ERROR = "Unknown error";
    }
}