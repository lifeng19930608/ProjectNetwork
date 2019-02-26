package com.lifeng.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.volar.HttpParams;
import io.volar.HttpResponse;
import io.volar.Volar;
import io.volar.callback.StringCallback;
import io.volar.configuration.VolarConfiguration;
import io.volar.util.LOG;

public class MainActivity extends AppCompatActivity {

    private WebView wv_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv_main = findViewById(R.id.wv_main);
        wv_main.setWebViewClient(new WebViewClient());
        wv_main.setWebChromeClient(new WebChromeClient());
    }

    public void onClick(View v) {
        VolarConfiguration separateConfiguration = Volar.getDefault().getSeparateConfigurationBuilder()
                .connectTimeout(5 * 1000)
                .readTimeout(20 * 1000)
                .writeTimeout(20 * 1000)
                .logTag("VolarDemo")
                .logEnabled(true)
                .build();

        HttpParams httpParams = new HttpParams();
        httpParams.put("word", 123456);
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("song", "Who Says");
        mapParam.put("artist", "Selena Gomez");
        mapParam.put("time", 195);
        mapParam.put("like", true);
        httpParams.put("info", mapParam);
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("1234");
        list.add("12345");
        httpParams.put("list", list);
        httpParams.setExtra("extra");

        Volar.getDefault().GET("https://m.baidu.com/s")
                .tag(this)
                .params(httpParams)
                .separateConfig(separateConfiguration)
                .callback(new StringCallback() {
                    @Override
                    public void onSuccess(HttpResponse response, String responseString) {
                        String hehe = (String) response.getExtra();
                        LOG.w("Volar", "extra response " + response.getExtra());
                        wv_main.loadDataWithBaseURL("", responseString, "text/html; charset=UTF-8", null, "404");
                    }

                    @Override
                    public void onFailure(HttpResponse response, int errorCode, String errorMessage) {

                    }
                })
                .execute();
    }
}

