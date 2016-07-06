package cn.ben.eventbustest.application;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

import cn.ben.eventbustest.BuildConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 配置EventBusBuilder
        //修改默认实现的配置，记住，必须在第一次EventBus.getDefault()之前配置，且只能设置一次。建议在application.onCreate()调用
        // 可以通过EventBusBuilder设置是否默认发送NoSubscriberEvent，默认是打开的
        EventBus.builder().sendNoSubscriberEvent(false).throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();
    }
}
