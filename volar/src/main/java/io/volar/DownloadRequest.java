package io.volar;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.volar.callback.DownloadCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Simple download request
 * Author: LiShen
 * Time: 2018/8/1 14:23
 */
class DownloadRequest {
    private String url;
    private File saveFolder;
    private String fileName;
    private boolean deleteFileAlreadyExist;
    private boolean useIndependentOkHttpClient;
    private OkHttpClient okHttpClient;
    private DownloadCallback callback;
    private long startTime;

    private void execute() {
        startTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(url)) {
            if (callback != null) {
                callback.onFinish(false, null, costTime(), "can't download from a EMPTY url!");
            }
            return;
        }
        try {
            if (saveFolder.exists()) {
                if (saveFolder.isDirectory() && saveFolder.canWrite()) {
                    File destination = new File(saveFolder.getPath() + File.separator + fileName);
                    if (destination.exists()) {
                        if (deleteFileAlreadyExist) {
                            boolean result = destination.delete();
                            if (result) {
                                executeDownload(destination);
                            }
                        } else {
                            fileName = System.currentTimeMillis() + "_" + fileName;
                            destination = new File(saveFolder.getPath() + File.separator + fileName);
                            executeDownload(destination);
                        }
                    } else {
                        executeDownload(destination);
                    }
                } else {
                    if (callback != null) {
                        callback.onFinish(false, null, costTime(), "can't download into a wrong folder!");
                    }
                }
            } else {
                boolean result = saveFolder.mkdirs();
                if (result) {
                    executeDownload(new File(saveFolder.getPath() + File.separator + fileName));
                } else {
                    if (callback != null) {
                        callback.onFinish(false, null, costTime(), "can't download into a wrong folder!");
                    }
                }
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onFinish(false, null, costTime(), "can't download into a wrong folder!");
            }
        }
    }

    /**
     * Execute the download
     */
    private void executeDownload(final File destination) {
        Request request = new Request.Builder().url(url).build();
        Call downloadCall = okHttpClient.newCall(request);
        refreshProgress(0);
        downloadCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    if (callback != null)
                        callback.onFinish(false, null, costTime(), "download failed, error: " + e.getMessage());
                } else {
                    if (callback != null)
                        callback.onFinish(false, null, costTime(), "download failed");
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    double total = response.body().contentLength();
                    fos = new FileOutputStream(destination);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0d / total * 100d);
                        refreshProgress(progress);
                    }
                    fos.flush();
                    refreshProgress(100);
                    if (callback != null)
                        callback.onFinish(true, destination, costTime(), "download success");
                } catch (Exception e) {
                    if (callback != null)
                        callback.onFinish(false, null, costTime(), "download failed, error: " + e.getMessage());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception ignore) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        });
    }

    private void refreshProgress(int progress) {
        int newProgress = progress;
        if (newProgress > 100) {
            newProgress = 100;
        } else if (newProgress < 0) {
            newProgress = 0;
        }
        if (callback != null)
            callback.onProgress(newProgress);
    }

    private long costTime() {
        return System.currentTimeMillis() - startTime;
    }

    private DownloadRequest(DownloadRequestBuilder builder) {
        url = builder.url;
        callback = builder.callback;
        deleteFileAlreadyExist = builder.deleteFileAlreadyExist;

        fileName = builder.fileName;
        if (TextUtils.isEmpty(fileName)) {
            if (!TextUtils.isEmpty(url)) {
                String[] temp = this.url.split("/");
                fileName = temp[temp.length - 1];
            }
        }

        saveFolder = builder.saveFolder;
        if (saveFolder == null) {
            saveFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }

        useIndependentOkHttpClient = builder.useIndependentOkHttpClient;
        if (useIndependentOkHttpClient) {
            if (builder.okHttpClient != null) {
                okHttpClient = builder.okHttpClient;
            } else {
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(45, TimeUnit.SECONDS)
                        .writeTimeout(45, TimeUnit.SECONDS)
                        .build();
            }
        } else {
            okHttpClient = Volar.getDefault().getOkHttpClient();
        }
    }


    public static final class DownloadRequestBuilder {
        private String url;
        private File saveFolder;
        private String fileName;
        private boolean useIndependentOkHttpClient;
        private boolean deleteFileAlreadyExist;
        private OkHttpClient okHttpClient;
        private DownloadCallback callback;

        DownloadRequestBuilder(String url) {
            this.url = url;
        }

        public DownloadRequestBuilder saveFolder(File val) {
            saveFolder = val;
            return this;
        }

        public DownloadRequestBuilder fileName(String val) {
            fileName = val;
            return this;
        }

        public DownloadRequestBuilder useIndependentOkHttpClient(boolean val) {
            useIndependentOkHttpClient = val;
            return this;
        }

        public DownloadRequestBuilder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public DownloadRequestBuilder callback(DownloadCallback val) {
            callback = val;
            return this;
        }

        public DownloadRequestBuilder deleteFileAlreadyExist(boolean val) {
            deleteFileAlreadyExist = val;
            return this;
        }

        public void execute() {
            new DownloadRequest(this).execute();
        }
    }
}