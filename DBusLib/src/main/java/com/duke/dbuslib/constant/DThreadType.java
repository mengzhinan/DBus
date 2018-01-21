package com.duke.dbuslib.constant;

/**
 * @author duke
 * @dateTime 2018-01-20 12:49
 * @description 执行线程类型常量
 */
public interface DThreadType {
    /**
     * 1会认为是子线程，其他所有值都被认为是UI线程
     */
    int UI_THREAD = 0;
    int CHILD_THREAD = 1;
}
