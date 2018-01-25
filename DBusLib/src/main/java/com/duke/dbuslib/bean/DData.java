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
        this.thread = THREAD_ALL;
    }

    public DData(int port, int thread) {
        this.port = port;
        this.thread = thread;
        if (this.thread != THREAD_UI && this.thread != THREAD_CHILD) {
            this.thread = THREAD_ALL;
        }
    }

    /**
     * 如果port = -1，表示只有方法名限定的方法才能收到消息，注解的方法收不到。
     */
    public static final int PORT_RECEIVE_METHOD_NAME = -1;
    /**
     * 如果port = 0，表示所有方法名限定的和注解的方法都能收到消息。
     */
    public static final int PORT_RECEIVE_ALL = 0;
    /**
     * 除以上值（PORT_RECEIVE_METHOD_NAME，PORT_RECEIVE_ALL）之外，其他值自己定义<br/>
     * 剩下的都是只针对注解方法的，且端口值相等的方法才会收到消息<br/>
     */
    public int port;//可以在发送消息时自定义赋其他值，除去0和-1之外任意值，建议为大于0的值


    /**
     * 只有UI线程的方法才能收到消息
     */
    public static final int THREAD_UI = 1;
    /**
     * 所有的方法都能收到消息
     */
    public static final int THREAD_ALL = 0;
    /**
     * 只有child线程的方法才能收到消息
     */
    public static final int THREAD_CHILD = -1;
    /**
     * 主要目的是控制哪些方法能够收到消息：Ui线程方法，子线程方法，所有方法 <br/>
     * 默认值：METHOD_ALL <br/>
     */
    public int thread = THREAD_ALL;//在发送消息时，只识别上面三种值，默认值为0


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
