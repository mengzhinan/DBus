package com.duke.dbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.core.DBus;


public class Main4Activity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        textView = findViewById(R.id.text4);
        DBus.getBus().register(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubData data = new SubData(4);
                        data.name = "dbus发送---";
                        data.str1 = "默认值de";
                        DBus.getBus().post(data);
                    }
                }).start();*/

                SubData data = new SubData(4);
                data.name = "dbus发送---";
                data.str1 = "默认值de";
                DBus.getBus().post(data);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus.getBus().unRegister(this);
    }

    private void onUIEvent(DData dData) {
        if (dData == null) {
            return;
        }
        int c = DBus.getTotalObjectSize();
        textView.setText(dData.str1 + "页面4");
    }

    @DBusInject(port = 4)
    private void onCreate(DData dData) {
        int c = DBus.getTotalObjectSize();
        textView.setText(dData.str1 + "页面4");
    }
}
