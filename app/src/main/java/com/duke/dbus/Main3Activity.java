package com.duke.dbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.constant.DThreadType;
import com.duke.dbuslib.core.DBus;


public class Main3Activity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        DBus.getBus().register(this);
        Log.v("dkdkdkdkdk","size = " + DBus.getTotalObjectSize());
        textView = findViewById(R.id.text3);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main3Activity.this, Main4Activity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus.getBus().unRegister(this);
    }

    private void onUIEvent(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.onUIEvent(), Thread = " + Thread.currentThread().getName());
    }

    private void onUIEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.onUIEventXXX(), Thread = " + Thread.currentThread().getName());
    }

    public String onThreadEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.onThreadEventXXX(), Thread = " + Thread.currentThread().getName());
        return "op";
    }

    @DBusInject(port = 3)
    public int aaaaasd(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.aaaaasd(port = 3), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 3, thread = DThreadType.CURRENT_CHILD_THREAD)
    public int dddddddeds(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.dddddddeds(port = 3,thread = DThreadType.CURRENT_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 3, thread = DThreadType.NEW_CHILD_THREAD)
    private static int aazxs(DData dData) {
        Log.v("dkdkdkdkdk", "Main3Activity.aazxs(port = 3,thread = DThreadType.NEW_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }
}
