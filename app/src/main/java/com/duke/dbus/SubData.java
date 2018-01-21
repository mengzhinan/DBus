package com.duke.dbus;


import com.duke.dbuslib.bean.DData;

/**
 * @author duke
 * @dateTime 2018-01-21 11:26
 * @description 自定义的数据类
 */
public class SubData extends DData {
    public SubData(int port) {
        super(port);
    }
    public String name;
    public int myInt;
}
