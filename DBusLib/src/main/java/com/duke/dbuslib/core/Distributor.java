package com.duke.dbuslib.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.bean.DMethod;
import com.duke.dbuslib.constant.DThreadType;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * @author duke
 * @dateTime 2018-01-20 17:01
 * @description 消息分发执行器
 */
public class Distributor {
    private static final String DMETHOD_NAME = "dMethod_Name";
    private static final String DDATA_NAME = "dData_Name";

    static void reset() {
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            DExecutor.get().shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void post(final DMethod dMethod, final DData dData, int threadType) {
        if (dMethod == null || dData == null) {
            throw new IllegalArgumentException("dMethod or dData is null");
        }
        if (!DBus.isObjectValid(dMethod.subscriber)) {
            return;
        }
        try {
            if (threadType == DThreadType.UI_THREAD) {
                if (isUIThread()) {
                    invoke(dMethod, dData);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DMETHOD_NAME, dMethod);
                    bundle.putSerializable(DDATA_NAME, dData);
                    Message message = handler.obtainMessage();
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } else {
                if (isUIThread()) {
                    DExecutor.get().execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(dMethod, dData);
                        }
                    });
                } else {
                    invoke(dMethod, dData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isUIThread() {
        try {
            return Thread.currentThread() == Looper.getMainLooper().getThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null || msg.getData() == null) {
                return;
            }
            try {
                Bundle bundle = msg.getData();
                DMethod dMethod = null;
                Serializable serializable = bundle.getSerializable(DMETHOD_NAME);
                if (serializable instanceof DMethod) {
                    dMethod = (DMethod) serializable;
                    if (!DBus.isObjectValid(dMethod.subscriber)) {
                        return;
                    }
                }
                DData dData = null;
                serializable = bundle.getSerializable(DDATA_NAME);
                if (serializable instanceof DData) {
                    dData = (DData) serializable;
                }
                invoke(dMethod, dData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static void invoke(DMethod dMethod, DData dData) {
        if (dMethod == null || dData == null) {
            return;
        }
        try {
            if (!DBus.isObjectValid(dMethod.subscriber)) {
                return;
            }
            dMethod.method.setAccessible(true);
            dMethod.method.invoke(dMethod.subscriber, dData);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
