package com.manymore13.multypicture;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author yzw
 * @version V1.0
 * @company 跨越速运
 * @Description
 * @date 2016/4/1
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }
}
