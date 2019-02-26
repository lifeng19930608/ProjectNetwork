package io.volar.util;

import android.text.TextUtils;
import android.util.Log;


public class LOG {
    private static final int LOG_MAX_SHOWN_LENGTH = 3000;

    public static void v(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            String[] logs = handleLogContent(msg);
            for (String log : logs) {
                if (!TextUtils.isEmpty(log)) {
                    Log.v(tag, log);
                }
            }
        }
    }

    public static void i(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            String[] logs = handleLogContent(msg);
            for (String log : logs) {
                if (!TextUtils.isEmpty(log)) {
                    Log.i(tag, log);
                }
            }
        }
    }

    public static void d(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            String[] logs = handleLogContent(msg);
            for (String log : logs) {
                if (!TextUtils.isEmpty(log)) {
                    Log.d(tag, log);
                }
            }
        }
    }

    public static void w(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            String[] logs = handleLogContent(msg);
            for (String log : logs) {
                if (!TextUtils.isEmpty(log)) {
                    Log.w(tag, log);
                }
            }
        }
    }

    public static void e(String tag, String msg) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            String[] logs = handleLogContent(msg);
            for (String log : logs) {
                if (!TextUtils.isEmpty(log)) {
                    Log.e(tag, log);
                }
            }
        }
    }

    private static String[] handleLogContent(String log) {
        String[] logs;
        if (log.length() <= LOG_MAX_SHOWN_LENGTH) {
            logs = new String[]{log};
        } else {
            int sections = (log.length() / LOG_MAX_SHOWN_LENGTH);
            if (log.length() % LOG_MAX_SHOWN_LENGTH != 0) {
                sections += 1;
            }
            logs = new String[sections];
            for (int i = 0; i < logs.length; i++) {
                int end = ((i + 1) * LOG_MAX_SHOWN_LENGTH);
                if (end > log.length()) {
                    end = log.length();
                }
                logs[i] = log.substring(i * LOG_MAX_SHOWN_LENGTH, end);
            }
        }
        return logs;
    }
}