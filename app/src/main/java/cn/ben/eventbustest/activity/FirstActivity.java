package cn.ben.eventbustest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.ben.eventbustest.R;
import cn.ben.eventbustest.event.MessageEvent;
import cn.ben.eventbustest.event.SomeOtherEvent;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = FirstActivity.class.getSimpleName();
    private TextView textField;
    private boolean destroyed = true;
    private TextView textFieldSticky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // 在注册之前发送消息
        // StickyEvent=true的订阅者能否接收post的事件？ 可以
        // StickyEvent=false的订阅者能否接收postSticky的事件？ 不可以
        EventBus.getDefault().postSticky(new SomeOtherEvent("This is an event before register!"));
        EventBus.getDefault().postSticky(new MessageEvent("This is an event before register!"));

        View btn_start_activity = findViewById(R.id.btn_start_activity);
        if (btn_start_activity != null) {
            btn_start_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                    startActivity(intent);
                }
            });
        }

        View btn_start_thread = findViewById(R.id.btn_start_thread);
        if (btn_start_thread != null) {
            btn_start_thread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new MessageEvent("Event From Thread id: " + Thread.currentThread().getId() + "!"));
                        }
                    });
                    thread.start();
                }
            });
        }

        textField = (TextView) findViewById(R.id.tv_1);
        textFieldSticky = (TextView) findViewById(R.id.tv_sticky);
    }

    @Override
    public void onStart() {
        super.onStart();

//        //EventBus提供了一个默认的实现，但不是单例
//        EventBus eventBus = new EventBus();
//        //下面这一条的效果是完全一样的
//        EventBus eventBus = EventBus.builder().build();

        SomeOtherEvent stickyEvent = EventBus.getDefault().getStickyEvent(SomeOtherEvent.class);
        // Better check that an event was actually posted before
        if (stickyEvent != null) {
            // "Consume" the sticky event
            Log.d(TAG, "A stickyEvent of SomeOtherEvent exists");
            EventBus.getDefault().removeStickyEvent(stickyEvent);
            // or
            // EventBus.getDefault().removeAllStickyEvents();
            // Now do something with it
        }

        stickyEvent = EventBus.getDefault().getStickyEvent(SomeOtherEvent.class);
        if (stickyEvent == null) {
            Log.d(TAG, "StickyEvent of SomeOtherEvent does not exist");
        }

        if (destroyed) EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyed = false;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 准备订阅者
     *
     * @param event 接收到的事件
     */
    // This method will be called when a MessageEvent is posted
    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        Log.e(TAG, "onMessageEvent " + event.message);
        // if not caught, it will crash because we wet it in application.onCreate()
        try {
            Toast.makeText(FirstActivity.this, event.message, Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Log.e(TAG, "You are not in main thread!");
        }
    }

    //默认调用方式，在调用post方法的线程执行，避免了线程切换，性能开销最少
    // Called in the same thread (default)
    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING) // ThreadMode is optional here
    public void onMessagePosting(MessageEvent event) {
        log("onMessagePosting " + event.message);
    }

    // Called in Android UI's main thread
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageMain(MessageEvent event) {
        textField.setText(event.message);
    }

    // 如果调用post方法的线程不是主线程，则直接在该线程执行
    // 如果是主线程，则切换到后台单例线程，多个方法公用同个后台线程，按顺序执行，避免耗时操作
    // Called in the background thread
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageBackground1(MessageEvent event) {
        log("onMessageBackground1" + event.message);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageBackground2(MessageEvent event) {
        log("onMessageBackground2" + event.message);
    }

    //开辟新独立线程，用来执行耗时操作，例如网络访问
    //EventBus内部使用了线程池，但是要尽量避免大量长时间运行的异步线程，限制并发线程数量
    //可以通过EventBusBuilder修改，默认使用Executors.newCachedThreadPool()
    // Called in a separate thread
    // TODO: 2016/7/6 可能是ThreadMode.Background的后台单例线程
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageAsync(MessageEvent event) {
        log("onMessageAsync " + event.message);
    }

    //在onStart调用register后，执行消息
    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageSticky(MessageEvent event) {
        // UI updates must run on MainThread
        textFieldSticky.setText(event.message);
    }

    @SuppressWarnings("unused")
    @Subscribe(priority = 123)
    public void onMessagePriority(MessageEvent event) {
        log("onMessagePriority " + event.message);
    }

    @SuppressWarnings("unused")
    @Subscribe(priority = -1)
    public void onMessageMinusOnePriority(MessageEvent event) {
        log("onMessageMinusOnePriority " + event.message);
        // 中止事件传递，后续事件不在调用,注意，只能在传递事件的时候调用
        // 注释这个来看效果
        EventBus.getDefault().cancelEventDelivery(event);
    }

    @SuppressWarnings("unused")
    @Subscribe(priority = -2)
    public void onMessageMinusTwoPriority(MessageEvent event) {
        log("onMessageMinusTwoPriority " + event.message);
    }

    // This method will be called when a SomeOtherEvent is posted
    @SuppressWarnings("unused")
    @Subscribe
    public void handleSomethingElse(SomeOtherEvent event) {
        doSomethingWith(event);
    }

    private void log(String message) {
        Log.e(TAG, "Thread id: " + Thread.currentThread().getId() + " " + message);
    }

    private void doSomethingWith(@SuppressWarnings("UnusedParameters") SomeOtherEvent event) {
        Log.d(TAG, "doSomethingWith SomeOtherEvent");
    }

}
