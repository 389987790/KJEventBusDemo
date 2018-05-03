package com.example.kongjian.kjeventbusdemo;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by user on 2018/5/3.
 */

public class EventBus {
    private Map<Object, List<SubscribleMethod>> cacheMap;
    private static EventBus instance;
    private Handler handler;
    private ExecutorService executorService;
    private EventBus() {
        cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void regist(Object activity) {
        List<SubscribleMethod> subscribleMethods = cacheMap.get(activity);
        if (subscribleMethods == null) {
            List<SubscribleMethod> methods = findSubscribleMethod(activity);
            cacheMap.put(activity, methods);
        }
    }

    private List<SubscribleMethod> findSubscribleMethod(Object activity) {
        List<SubscribleMethod> list = new CopyOnWriteArrayList<>();
        Class<?> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            for (Method method : methods) {
                Subscrible subscrible = method.getAnnotation(Subscrible.class);
                if (subscrible == null) {
                    continue;
                }
                //拿到方法的参数数组
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("传递参数个数必须为1");
                }
                Class<?> paramsClass = parameterTypes[0];
                ThreadMode threadMode = subscrible.value();
                SubscribleMethod subscribleMethod = new SubscribleMethod(threadMode,method,paramsClass);
                list.add(subscribleMethod);

            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    public void post(final Object o) {
        for (Map.Entry<Object, List<SubscribleMethod>> entry : cacheMap.entrySet()) {
            List<SubscribleMethod> list = entry.getValue();
            final Object activity = entry.getKey();
            for (final SubscribleMethod subscribleMethod : list) {
                if (subscribleMethod.getEventType().isAssignableFrom(o.getClass())) {
                    //线程切换
                    switch (subscribleMethod.getThreadMode()) {
                        case PostThread:
                            invoke(activity,subscribleMethod,o);
                            break;
                        case MainThread:
                            //判断发送线程处于那个线程
                            if (Looper.getMainLooper() == Looper.myLooper()) {
                                invoke(activity,subscribleMethod,o);
                            }else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,subscribleMethod,o);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            if (Looper.getMainLooper() != Looper.myLooper()) {
                                //发生在子线程
                                invoke(activity,subscribleMethod,o);
                            }else{
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,subscribleMethod,o);
                                    }
                                });
                            }
                            break;
                    }
                }
            }
        }

    }

    private void invoke(Object activity, SubscribleMethod subscribleMethod, Object o) {
        try {
            subscribleMethod.getMethod().invoke(activity, o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void unregist(Object object) {
        cacheMap.remove(object);
    }
}
