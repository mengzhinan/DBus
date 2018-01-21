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


public class Main3Activity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        DBus.getBus().register(this);
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
        if (dData == null) {
            return;
        }
        int c = DBus.getTotalObjectSize();
        textView.setText(dData.str1 + "页面3");
    }

    @DBusInject(port = 4, thread = DThreadType.CHILD_THREAD)
    private void asdflk(DData dData) {
        int c = DBus.getTotalObjectSize();
        textView.setText(dData.str1 + "页面3注解");
    }
}
