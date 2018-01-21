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
    public static final String TAG = "DBus_log";
    public static boolean isDebug = false;

    private static HashMap<Object, ArrayList<DMethod>> uiThreadMethodMap;
    private static HashMap<Object, ArrayList<DMethod>> childThreadMethodMap;

    /**
     * 如果是使用方法名注册事件，则提供下面两种特定的方法名寻找事件回调 <br/>
     * onUIEvent*(DData data):UI线程中执行的方法 <br/>
     * onThreadEvent*(DData data):非UI线程中执行的方法
     */
    private static final String UI_THREAD_METHOD_NAME = "onUIEvent";
    private static final String CHILD_THREAD_METHOD_NAME = "onThreadEvent";

    /**
     * 是否使用方法名，还是使用注解来注册事件方法 <br/>
     * 使用注解：灵活方便，但是效率低<br/>
     * 使用方法名：不灵活，但是效率高
     */
    private static boolean isUseMethodName = false;

    /**
     * 是否使用方法名称为注册事件函数，而不使用注解 <br/>
     * 二者只能选其一，默认是使用注解
     *
     * @param isJustUseMethodName
     */
    public static void isUseMethodNameFind(boolean isJustUseMethodName) {
        isUseMethodName = isJustUseMethodName;
    }

    //统计注册了多少个对象
    private static int totalObject;

    public static int getTotalObjectSize() {
        return totalObject;
    }

    private static class Inner {
        private static DBus i = new DBus();
    }

    private DBus() {
        uiThreadMethodMap = new HashMap<>();
        childThreadMethodMap = new HashMap<>();
    }

    public static DBus getBus() {
        return Inner.i;
    }

    /**
     * 注册消息监听
     *
     * @param object 消息对象
     * @return 是否注册成功
     */
    @SuppressWarnings("unchecked")
    public boolean register(Object object) {
        if (object == null) {
            return false;
        }
        Class clazz = object.getClass();
        if (clazz == null) {
            return false;
        }
        ArrayList<DMethod> uiList = new ArrayList<>();
        ArrayList<DMethod> childList = new ArrayList<>();
        boolean isOK = false;
        if (isUseMethodName) {
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
            DMethod dMethod = null;
            for (Method method : methods) {
                if (!isMethodParamOK(method)) {
                    continue;
                }
                //startsWith
                if (method.getName().startsWith(CHILD_THREAD_METHOD_NAME)) {
                    dMethod = new DMethod(object, method, 0);
                    childList.add(dMethod);
                } else if (method.getName().startsWith(UI_THREAD_METHOD_NAME)) {
                    dMethod = new DMethod(object, method, 0);
                    uiList.add(dMethod);
                }
            }
            /*try {
                //查找特定的函数
                Method method = clazz.getDeclaredMethod(CHILD_THREAD_METHOD_NAME, DData.class);
                if (isMethodParamOK(method)) {
                    DMethod dMethod = new DMethod(object, method, 0);
                    childList.add(dMethod);
                    log(dMethod, DThreadType.CHILD_THREAD);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                //查找特定的函数
                Method method = clazz.getDeclaredMethod(UI_THREAD_METHOD_NAME, DData.class);
                if (isMethodParamOK(method)) {
                    DMethod dMethod = new DMethod(object, method, 0);
                    uiList.add(dMethod);
                    log(dMethod, DThreadType.UI_THREAD);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }*/
        } else {
            //如果是查找所有方法，再根据注解确定方法
            Method[] methods = clazz.getDeclaredMethods();
            if (methods == null || methods.length <= 0) {
                return false;
            }
            DBusInject dBusInject;
            for (Method method : methods) {
                if (!isMethodParamOK(method)) {
                    continue;
                }
                //循环，获取每个函数的注解对象
                dBusInject = method.getAnnotation(DBusInject.class);
                if (dBusInject == null) {
                    continue;
                }
                //获取注解thread参数值
                int thread = dBusInject.thread();
                //获取注解port参数值
                int port = dBusInject.port();
                //包装相关信息
                DMethod dMethod = new DMethod(object, method, port);
                if (thread == DThreadType.CHILD_THREAD) {
                    childList.add(dMethod);
                } else {
                    //threadType其他所有情况都认为是UI线程
                    uiList.add(dMethod);
                }
            }
        }
        if (uiThreadMethodMap != null && uiList.size() > 0) {
            //保存到UI函数集合中
            uiThreadMethodMap.put(object, uiList);
            isOK = true;
        }
        if (childThreadMethodMap != null && childList.size() > 0) {
            //保存到子线程函数集合中
            childThreadMethodMap.put(object, childList);
            isOK = true;
        }
        if (isOK) {
            totalObject++;
        }
        return isOK;
    }

    /**
     * 判断参数类型
     *
     * @param method 待处理的方法
     * @return 是否是特定参数，特定修饰符
     */
    private boolean isMethodParamOK(Method method) {
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
    }

    /**
     * 解除消息监听
     *
     * @param object 方法对象
     * @return 是否解除注册成功
     */
    public boolean unRegister(Object object) {
        if (object == null) {
            return false;
        }
        boolean isOK = false;
        if (uiThreadMethodMap != null && uiThreadMethodMap.containsKey(object)) {
            uiThreadMethodMap.remove(object);
            isOK = true;
        }
        if (childThreadMethodMap != null && childThreadMethodMap.containsKey(object)) {
            childThreadMethodMap.remove(object);
            isOK = true;
        }
        if (isOK) {
            totalObject--;
        }
        return isOK;
    }

    /**
     * 重置消息库
     */
    public void reset() {
        if (uiThreadMethodMap == null) {
            uiThreadMethodMap = new HashMap<>();
        }
        if (uiThreadMethodMap.size() > 0) {
            uiThreadMethodMap.clear();
        }
        if (childThreadMethodMap == null) {
            childThreadMethodMap = new HashMap<>();
        }
        if (childThreadMethodMap.size() > 0) {
            childThreadMethodMap.clear();
        }
        Distributor.reset();
    }

    /**
     * 发送消息
     *
     * @param dData
     */
    public void post(DData dData) {
        if (dData == null) {
            return;
        }
        doPost(uiThreadMethodMap, dData, DThreadType.UI_THREAD);
        doPost(childThreadMethodMap, dData, DThreadType.CHILD_THREAD);
    }

    private static void doPost(HashMap<Object, ArrayList<DMethod>> map, DData dData, int threadType) {
        if (map == null || map.size() == 0 || dData == null) {
            return;
        }
        ArrayList<DMethod> dMethodList = null;
        DMethod dMethod = null;
        Set<Object> keySet = map.keySet();
        for (Object key : keySet) {
            dMethodList = map.get(key);
            if (dMethodList == null || dMethodList.size() <= 0) {
                //key是不能重复的，即每个key只有一个list类型的value值
                //找不到就直接退出循环
                break;
            }
            int methodSize = dMethodList.size();
            for (int i = 0; i < methodSize; i++) {
                dMethod = dMethodList.get(i);
                if (dMethod == null
                        || dMethod.method == null
                        || dMethod.subscriber == null) {
                    continue;
                }
                //如果不是注解方式，则不考虑端口限制
                if (!isUseMethodName && dData.port != dMethod.port) {
                    continue;
                }
                Distributor.post(dMethod, dData, threadType);
            }
        }
    }

    static boolean isObjectValid(Object object) {
        if (object == null) {
            return false;
        }
        boolean isValid = false;
        if (uiThreadMethodMap != null
                && uiThreadMethodMap.size() > 0
                && uiThreadMethodMap.containsKey(object)) {
            isValid = true;
        }
        if (childThreadMethodMap != null
                && childThreadMethodMap.size() > 0
                && childThreadMethodMap.containsKey(object)) {
            isValid = true;
        }
        return isValid;
    }
}
