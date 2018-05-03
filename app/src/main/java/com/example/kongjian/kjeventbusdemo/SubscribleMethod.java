package com.example.kongjian.kjeventbusdemo;

import java.lang.reflect.Method;

/**
 * Created by user on 2018/5/3.
 */

public class SubscribleMethod {
    private ThreadMode threadMode;
    private Method method;
    private Class<?> eventType;

    public SubscribleMethod(ThreadMode threadMode, Method method, Class<?> eventType) {
        this.threadMode = threadMode;
        this.method = method;
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
