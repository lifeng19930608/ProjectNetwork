package io.volar.callback;

import java.io.File;

/**
 * Project: ProjectVolar
 * Author: LiShen
 * Time: 2018/8/1 14:24
 */
public interface DownloadCallback {
    void onProgress(int newProgress);

    void onFinish(boolean success, File downloadedFile, long costTime, String message);
}