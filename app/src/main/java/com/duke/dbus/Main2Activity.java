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

public class Main2Activity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        DBus.getBus().register(this);
        Log.v("dkdkdkdkdk","size = " + DBus.getTotalObjectSize());
        textView = findViewById(R.id.text2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, Main3Activity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus.getBus().unRegister(this);
    }
    private void onUIEvent(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.onUIEvent(), Thread = " + Thread.currentThread().getName());
    }

    private void onUIEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.onUIEventXXX(), Thread = " + Thread.currentThread().getName());
    }

    public String onThreadEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.onThreadEventXXX(), Thread = " + Thread.currentThread().getName());
        return "op";
    }

    @DBusInject(port = 2)
    public int aaaaasd(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.aaaaasd(port = 2), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 2, thread = DThreadType.CURRENT_CHILD_THREAD)
    public int dddddddeds(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.dddddddeds(port = 2,thread = DThreadType.CURRENT_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 2, thread = DThreadType.NEW_CHILD_THREAD)
    private static int aazxs(DData dData) {
        Log.v("dkdkdkdkdk", "Main2Activity.aazxs(port = 2,thread = DThreadType.NEW_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }
}
