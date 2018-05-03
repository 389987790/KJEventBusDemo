package com.example.kongjian.kjeventbusdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by user on 2018/5/3.
 */

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void send(View btn) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post("second");
            }
        }).start();
    }
}
