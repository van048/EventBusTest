package cn.ben.eventbustest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import cn.ben.eventbustest.R;
import cn.ben.eventbustest.event.MessageEvent;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        View btn_post_event = findViewById(R.id.btn_post_event);
        if (btn_post_event != null) {
            btn_post_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new MessageEvent("Event From Thread id: " + Thread.currentThread().getId() + "!"));
                }
            });
        }
    }
}
