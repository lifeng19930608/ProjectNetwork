package com.lifeng.network;

import android.app.Application;

import io.volar.Volar;
import io.volar.configuration.VolarConfiguration;

/**
 * author : lifeng
 * e-mail : android_lifeng@sina.com
 * date   : 2019/2/261:27 PM
 * desc   :
 * version: 1.0
 */
public class NetworkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolarConfiguration configuration = new VolarConfiguration.Builder()
                .logTag("Volar")
                .connectTimeout(5000)
                .build();
        Volar.init(configuration);
    }
}
