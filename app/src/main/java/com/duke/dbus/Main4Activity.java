package com.duke.dbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.constant.DThreadType;
import com.duke.dbuslib.core.DBus;


public class Main4Activity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        textView = findViewById(R.id.text4);
        DBus.getBus().register(this);
        Log.v("dkdkdkdkdk", "size = " + DBus.getTotalObjectSize());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubData data = new SubData(4, DData.THREAD_ALL);
                        data.name = "dbus发送---";
                        data.str1 = "默认值de";
                        DBus.getBus().post(data);
                    }
                }).start();

                /*SubData data = new SubData(4);
                data.name = "dbus发送---";
                data.str1 = "默认值de";
                DBus.getBus().post(data);*/
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus.getBus().unRegister(this);
    }

    private void onUIEvent(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.onUIEvent(), Thread = " + Thread.currentThread().getName());
    }

    private void onUIEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.onUIEventXXX(), Thread = " + Thread.currentThread().getName());
    }

    public String onThreadEventXXX(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.onThreadEventXXX(), Thread = " + Thread.currentThread().getName());
        return "op";
    }

    @DBusInject(port = 4)
    public int aaaaasd(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.aaaaasd(port = 4), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 4, thread = DThreadType.CURRENT_CHILD_THREAD)
    public int dddddddeds(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.dddddddeds(port = 4,thread = DThreadType.CURRENT_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }

    @DBusInject(port = 4, thread = DThreadType.NEW_CHILD_THREAD)
    private static int aazxs(DData dData) {
        Log.v("dkdkdkdkdk", "Main4Activity.aazxs(port = 4,thread = DThreadType.NEW_CHILD_THREAD), Thread = " + Thread.currentThread().getName());
        return 0;
    }
}
