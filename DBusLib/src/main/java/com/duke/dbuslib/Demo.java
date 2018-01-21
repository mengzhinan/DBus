package com.duke.dbuslib;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.constant.DThreadType;
import com.duke.dbuslib.core.DBus;

/**
 * @author duke
 * @dateTime 2018-01-21 15:39
 * @description 需要接收到消息的类
 */
public class Demo {

    /**
     * 发送消息处，可以是任意线程任意位置<br/>
     * DBus.getBus().post(new DData(4));//port必填，对应接收处的port值
     */

    //================================================

    /**
     * 初始化函数，注册消息事件
     */
    public void onInit() {
        //参数可以是任意Object对象
        DBus.getBus().register(this);
    }

    /**
     * 结束方法，反注册
     */
    public void onDestroy() {
        //参数可以是任意Object对象，不要忘记反注册，比喻Activity的onDestroy()函数
        DBus.getBus().unRegister(this);
    }

    //=================================================

    /**
     * 以下函数都可以为接收消息的函数
     */

    /*private void onUIEvent(DData dData) {

    }

    private void onUIEventXXX(DData dData) {

    }

    private int onUIEvent(DData dData) {
        return 2;
    }

    private String onUIEventXXX(DData dData) {
        return "";
    }

    public int onUIEvent(DData dData) {
        return 3;
    }

    public String onUIEventXXX(DData dData) {
        return "";
    }

    public void onUIEvent(DData dData) {

    }

    public void onUIEventXXX(DData dData) {

    }

    public void onThreadEvent(DData dData) {

    }

    public void onThreadEventXXX(DData dData) {

    }

    @DBusInject(port = 2)
    static int aaa(DData dData) {
        return 10;
    }

    @DBusInject(port = 103, thread = DThreadType.CHILD_THREAD)
    private String test(DData dData) {
        return "";
    }

    @DBusInject(port = 0, thread = DThreadType.UI_THREAD)
    public void haha(DData dData) {

    }*/
}
