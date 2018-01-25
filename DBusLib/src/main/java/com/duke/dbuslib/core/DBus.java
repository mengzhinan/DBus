package com.duke.dbuslib.core;

import com.duke.dbuslib.annotation.DBusInject;
import com.duke.dbuslib.bean.DData;
import com.duke.dbuslib.bean.DMethod;
import com.duke.dbuslib.constant.DThreadType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author duke
 * @dateTime 2018-01-20 12:18
 * @description 消息客车工具类，对外
 */
public class DBus {

    /**
     * 保存抓取的方法集合
     */
    private static HashMap<Object, ArrayList<DMethod>> methodMap;

    public static int getTotalObjectSize() {
        int totalSize = 0;
        if (methodMap != null) {
            totalSize = methodMap.size();
        }
        return totalSize;
    }

    /**
     * 如果是使用方法名注册事件，则提供下面两种特定的方法名寻找事件回调 <br/>
     * onUIEvent*(DData data):UI线程中执行的方法 <br/>
     * onThreadEvent*(DData data):非UI线程中执行的方法
     */
    private static final String UI_THREAD_METHOD_NAME = "onUIEvent";
    private static final String CHILD_THREAD_METHOD_NAME = "onThreadEvent";

    private static class Inner {
        private static DBus i = new DBus();
    }

    private DBus() {
        methodMap = new HashMap<>();
    }

    public static DBus getBus() {
        return Inner.i;
    }

    /**
     * 注册消息监听
     *
     * @param subscriber 消息订阅对象
     * @return 是否注册成功
     */
    @SuppressWarnings("unchecked")
    public boolean register(Object subscriber) {
        try {
            if (subscriber == null) {
                return false;
            }
            Class clazz = subscriber.getClass();
            if (clazz == null) {
                return false;
            }
            //如果是直接定位方法名
            //clazz.getMethods() 获取公共的可访问的方法，但是缺点是包含父类的方法
            //clazz.getDeclaredMethods() 获取自己类定义的方法，缺点是还包含私有的和不可访问的方法
            //查找特定的函数
            //Method method = clazz.getDeclaredMethod(CHILD_THREAD_METHOD_NAME, DData.class);
            //获取当前类声明的所有方法
            Method[] methods = clazz.getDeclaredMethods();
            if (methods == null || methods.length <= 0) {
                return false;
            }
            ArrayList<DMethod> dMethodList = null;
            if (methodMap.containsKey(subscriber)) {
                dMethodList = methodMap.get(subscriber);
                //清空之前注册的方法
                dMethodList.clear();
            }
            if (dMethodList == null) {
                dMethodList = new ArrayList<>();
            }
            DBusInject dBusInject;
            DMethod dMethod = null;
            for (Method method : methods) {
                if (!isMethodParamOK(method)) {
                    continue;
                }
                //循环，获取每个函数的注解对象
                dBusInject = method.getAnnotation(DBusInject.class);
                if (dBusInject != null) {//使用注解模式
                    //获取注解thread参数值
                    int thread = dBusInject.thread();
                    //获取注解port参数值
                    int port = dBusInject.port();
                    //包装相关信息
                    dMethod = new DMethod(subscriber, method, thread, port, false);
                    dMethodList.add(dMethod);
                } else {//使用方法名模式
                    //startsWith
                    if (!method.getName().startsWith(UI_THREAD_METHOD_NAME)
                            && !method.getName().startsWith(CHILD_THREAD_METHOD_NAME)) {
                        continue;
                    }
                    //默认在UI线程
                    int threadValue = DThreadType.UI_THREAD;
                    if (method.getName().startsWith(CHILD_THREAD_METHOD_NAME)) {
                        threadValue = DThreadType.CURRENT_CHILD_THREAD;
                    }
                    //默认端口为0，此时端口不重要
                    dMethod = new DMethod(subscriber, method, threadValue, 0, true);
                    dMethodList.add(dMethod);
                }
            }
            if (dMethodList.size() > 0) {
                methodMap.put(subscriber, dMethodList);
                return true;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断参数类型
     *
     * @param method 待处理的方法
     * @return 是否是特定参数，特定修饰符
     */
    private boolean isMethodParamOK(Method method) {
        try {
            if (method == null
                    || method.getName() == null
                    || "".equals(method.getName().trim())) {
                return false;
            }
            // 获取参数类型
            Class[] paramTypes = method.getParameterTypes();
            //判断参数个数
            if (paramTypes.length != 1) {
                return false;
            }
            //判断参数类型
            return DData.class.getName().equals(paramTypes[0].getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解除消息监听
     *
     * @param subscriber 消息订阅对象
     * @return 是否解除注册成功
     */
    public boolean unRegister(Object subscriber) {
        try {
            if (subscriber == null) {
                return false;
            }
            if (methodMap.containsKey(subscriber)) {
                ArrayList<DMethod> list = methodMap.get(subscriber);
                list.clear();
                methodMap.remove(subscriber);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重置消息库
     */
    public void reset() {
        methodMap.clear();
        Distributor.reset();
    }

    /**
     * 发送消息
     *
     * @param dData
     */
    public void post(DData dData) {
        if (dData == null || methodMap.size() <= 0) {
            return;
        }
        ArrayList<DMethod> dMethodList = null;
        DMethod dMethod = null;
        Set<Object> keySet = methodMap.keySet();
        for (Object key : keySet) {
            dMethodList = methodMap.get(key);
            if (dMethodList == null || dMethodList.size() <= 0) {
                continue;
            }
            int methodSize = dMethodList.size();
            for (int i = 0; i < methodSize; i++) {
                dMethod = dMethodList.get(i);
                if (dMethod == null
                        || dMethod.method == null
                        || dMethod.subscriber == null) {
                    continue;
                }
                //处理每一个待调用的方法
                accessThreadPost(dMethod, dData);
            }
        }
    }

    /**
     * 对目标方法的线程类型判断处理
     *
     * @param dMethod
     * @param dData
     */
    private void accessThreadPost(DMethod dMethod, DData dData) {
        switch (dData.thread) {
            case DData.THREAD_UI://只有UI线程的方法
                if (dMethod.thread == DThreadType.UI_THREAD) {
                    accessPortPost(dMethod, dData);
                }
                break;
            case DData.THREAD_CHILD://只有子线程的方法
                if (dMethod.thread == DThreadType.CURRENT_CHILD_THREAD
                        || dMethod.thread == DThreadType.NEW_CHILD_THREAD) {
                    accessPortPost(dMethod, dData);
                }
                break;
            case DData.THREAD_ALL://所有线程的方法
            default:
                accessPortPost(dMethod, dData);
                break;
        }
    }

    /**
     * 对目标方法的端口判断，已决定是否执行方法名的还是注解的方法
     *
     * @param dMethod
     * @param dData
     */
    private void accessPortPost(DMethod dMethod, DData dData) {
        switch (dData.port) {
            case DData.PORT_RECEIVE_ALL://所有方法
                Distributor.post(dMethod, dData);
                break;
            case DData.PORT_RECEIVE_METHOD_NAME://仅仅方法名限定的方法
                if (dMethod.isUseMethodName) {
                    Distributor.post(dMethod, dData);
                }
                break;
            default://仅仅注解方法，且端口号相等
                if (!dMethod.isUseMethodName && dData.port == dMethod.port) {
                    Distributor.post(dMethod, dData);
                }
                break;
        }
    }

    static boolean isObjectValid(Object subscriber) {
        return subscriber != null && methodMap.size() > 0 && methodMap.containsKey(subscriber);
    }
}
