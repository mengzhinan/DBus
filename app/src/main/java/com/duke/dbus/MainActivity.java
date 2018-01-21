package com.duke.dbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.core.DBus;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "dbustest";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBus.isUseMethodNameFind(true);
        DBus.getBus().register(this);

        textView = findViewById(R.id.test);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubData data = new SubData(654);
                        data.name = "asdf";
                        DBus.getBus().post(data);
                    }
                }).start();*/
                startActivity(new Intent(MainActivity.this, Main2Activity.class));

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBus.getBus().unRegister(this);
    }

    private void onUIEvent(DData dData) {
        textView.setText(dData.str1 + "页面1");
    }

    @DBusInject(port = 4)
    public int aaaaasd(DData dData) {
        if (dData == null) {
            return 0;
        }
        int c = DBus.getTotalObjectSize();
        textView.setText(dData.str1 + "页面1注解");
        return 0;
    }

    void onThreadEventdsf(DData dData) {
        String thread = Thread.currentThread().getName();
        int c = DBus.getTotalObjectSize();
        Log.v(TAG, "a16 static");
    }
}
