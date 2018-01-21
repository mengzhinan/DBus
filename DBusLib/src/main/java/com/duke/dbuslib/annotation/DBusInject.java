package com.duke.dbuslib.annotation;

import com.duke.dbuslib.constant.DThreadType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author duke
 * @dateTime 2018-01-20 12:24
 * @description 注册事件的方法的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBusInject {

    /**
     * 订阅函数的接收端口，同一端口的多个函数都可以接收到消息<br/>
     * 此端口并非网络通讯连接的端口，在此处只是一个形象的比喻
     *
     * @return 订阅函数接收消息的端口
     */
    int port();

    /**
     * 当前注解的方法在哪个线程中执行
     *
     * @return 参考DThreadType接口定义  0：UI线程，1：子线程
     */
    int thread() default DThreadType.UI_THREAD;

}
