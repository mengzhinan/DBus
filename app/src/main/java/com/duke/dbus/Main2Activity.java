package com.duke.dbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        if (dData == null) {
            return;
        }
        String thread = Thread.currentThread().getName();
        int c = DBus.getTotalObjectSize();
        if (dData instanceof SubData) {
            SubData subData = (SubData) dData;
            textView.setText(subData.name + "页面2");
        } else {
            textView.setText(dData.str1 + "页面2");
        }
    }

    private void onThreadEvent(DData dData) {
        if (dData == null) {
            return;
        }
        String thread = Thread.currentThread().getName();
        int c = DBus.getTotalObjectSize();
        if (dData instanceof SubData) {
            SubData subData = (SubData) dData;
            textView.setText(subData.name + "页面2");
        } else {
            textView.setText(dData.str1 + "页面2");
        }
    }

    @DBusInject(port = 4, thread = DThreadType.CHILD_THREAD)
    public void asdflk(DData dData) {
        int c = DBus.getTotalObjectSize();
        String thread = Thread.currentThread().getName();
        textView.setText(dData.str1 + "页面2注解");
    }
}
