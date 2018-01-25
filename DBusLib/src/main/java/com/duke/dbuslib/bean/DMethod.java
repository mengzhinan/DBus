package com.duke.dbuslib.bean;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author duke
 * @dateTime 2018-01-20 16:24
 * @description 订阅函数封装类
 */
public class DMethod implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 关联的订阅者
     */
    public Object subscriber;
    /**
     * 当前订阅函数
     */
    public Method method;
    /**
     * 在哪个线程执行的方法，参考DThreadType接口
     */
    public int thread;
    /**
     * 订阅函数的接收端口，同一端口的多个函数都可以接收到消息<br/>
     * 此端口并非网络通讯连接的端口，在此处只是一个形象的比喻
     */
    public int port;

    /**
     * 是否使用方法名的方法，还是注解的方法
     */
    public boolean isUseMethodName;

    public DMethod(Object subscriber, Method method, int thread, int port, boolean isUseMethodName) {
        if (subscriber == null || method == null) {
            throw new IllegalArgumentException("subscriber or method is null");
        }
        this.subscriber = subscriber;
        this.method = method;
        this.thread = thread;
        this.port = port;
        this.isUseMethodName = isUseMethodName;
    }
}
