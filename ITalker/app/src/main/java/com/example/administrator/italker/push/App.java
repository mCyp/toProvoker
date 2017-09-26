package com.example.administrator.italker.push;

import com.example.common.app.Application;
import com.example.factory.Factory;
import com.igexin.sdk.PushManager;

/**
 * Created by Administrator on 2017/8/6.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 调用Factory进行初始化
        Factory.setup();
        // 推送进行初始化
        PushManager.getInstance().initialize(this);
    }
}
