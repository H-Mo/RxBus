package moe.rxbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.rxbus.bean.SubscriberMethod;
import moe.rxbus.core.RxBusCore;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * @author H-Mo
 * @time 16/12/16  9:46
 * @desc RxBus 的对外接口，在 RxBusCore 的基础上进一步封装
 */
public class RxBus {

    private static Map<Class<?>,List<Subscription>> subscriberMap = new HashMap<>();

    public static void register(final Object obj){
        // 获取订阅者类对象的方法集合
        final Class<?> subscriberClz = obj.getClass();
        Method[] methods = subscriberClz.getMethods();
        //
        for (final Method method : methods) {
            // 如果该方法没有使用了 Subscribe 注解，则跳过
            if( !method.isAnnotationPresent(Subscribe.class)){
                continue;
            }
            // 如果参数为空或者不是一个，也跳过
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes == null || parameterTypes.length != 1){
                continue;
            }
            // 得到订阅者订阅的事件类型
            final Class<?> eventType = parameterTypes[0];
            // 得到注解对象
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            // 订阅事件
            Subscription subscribe = RxBusCore.get()
                    .toObservable(eventType)
                    .observeOn(getThreadMode(annotation))
                    .subscribe(new Action1<Object>() {
                @Override
                public void call(Object t) {
                    try {
                        method.invoke(obj,t);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
            // 保存订阅事件到集合
            if(!subscriberMap.containsKey(subscriberClz)){
                subscriberMap.put(subscriberClz,new ArrayList<Subscription>());
            }
            List<Subscription> subscriberMethods = subscriberMap.get(subscriberClz);
            subscriberMethods.add(subscribe);
        }
    }

    /**
     * 注销监听，Activity 或者 Fragment 生命周期结束前必须注销监听
     * @param obj Activity 或者 Fragment 的对象
     */
    public static void unRegister(Object obj){
        Class<?> subscriberClz = obj.getClass();
        if(!subscriberMap.containsKey(subscriberClz)){
            return;
        }
        List<Subscription> subscriberMethods = subscriberMap.get(subscriberClz);
        if(subscriberMethods == null || subscriberMethods.size() <= 0){
            return;
        }
        for (Subscription subscriber: subscriberMethods) {
            if(subscriber != null && !subscriber.isUnsubscribed()){
                subscriber.unsubscribe();
            }
        }
    }

    /**
     * 根据注解配置返回需要的线程调度器
     * @param annotation 注解对象
     * @return 线程调度器
     */
    public static Scheduler getThreadMode(Subscribe annotation){
        ThreadMode threadMode = annotation.threadMode();
        switch (threadMode) {
            case CURRENT_THREAD:    // 当前线程
                return Schedulers.immediate();
            case NEW_THREAD:        // 新的线程
                return Schedulers.newThread();
            case IO:                // 由 IO 调度(复用的子线程)
                return Schedulers.io();
            case MAIN:              // 主线程(UI线程)
                return AndroidSchedulers.mainThread();
        }
        return null;
    }

}
