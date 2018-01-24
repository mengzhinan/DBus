package com.duke.dbuslib.constant;

/**
 * @author duke
 * @dateTime 2018-01-20 12:49
 * @description 执行线程类型常量
 */
public interface DThreadType {
    /**
     * UI线程操作
     */
    int UI_THREAD = 0;

    /**
     * 当前子线程
     */
    int CURRENT_CHILD_THREAD = 1;

    /**
     * new一个新的子线程
     */
    int NEW_CHILD_THREAD = 2;
}
