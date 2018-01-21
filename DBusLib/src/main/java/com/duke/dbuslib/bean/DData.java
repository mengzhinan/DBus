package com.duke.dbuslib.bean;

import java.io.Serializable;

/**
 * @author duke
 * @dateTime 2018-01-20 13:30
 * @description 事件携带的数据类，对外
 */
public class DData implements Serializable {
    private static final long serialVersionUID = 1L;

    public DData(int port) {
        this.port = port;
    }

    /**
     * 订阅函数的接收端口，同一端口的多个函数都可以接收到消息<br/>
     * 此端口并非网络通讯连接的端口，在此处只是一个形象的比喻
     */
    public int port;


    /**
     * 以下是数据参数
     */
    public Object obj1;
    public Object obj2;

    public String str1;
    public String str2;

    public int int1;
    public int int2;
    public int int3;

    public boolean bool1;
    public boolean bool2;
}
