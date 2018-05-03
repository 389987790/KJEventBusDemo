package com.example.kongjian.kjeventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().regist(this);
        textView = (TextView) findViewById(R.id.tv_1);
    }

    @Subscrible(ThreadMode.PostThread)
    public void receive(String content) {
//        textView.setText("content="+content+"---thread = "+ Thread.currentThread().getName());
        Log.d("eventbus", "content=" + content + "---thread = " + Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

    public void jump(View btn) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}
