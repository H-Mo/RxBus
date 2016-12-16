package moe.rxbus.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import moe.rxbus.ThreadMode;
import rx.Subscription;

/**
 * @author H-Mo
 * @time 16/12/16  9:25
 * @desc 封装处理过程中的相关信息、模式、接收消息对象、code、接收消息类型
 */
public class SubscriberMethod {
    private Method mMethod;             // 回调方法
    private ThreadMode mThreadMode;     // 回调方法的线程模式
    private Class<?> mEventType;        // 监听的事件类型
    private Subscription mSubscriber;         // 订阅者

    public SubscriberMethod(Method method, ThreadMode threadMode,
                            Class<?> eventType, Subscription subscriber){
        mMethod = method;
        mThreadMode = threadMode;
        mEventType = eventType;
        mSubscriber = subscriber;
    }

    /**
     * 调用方法
     * @param o 参数
     */
    public void invoke(Object o){
        try {
            mMethod.invoke(mSubscriber, o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        mThreadMode = threadMode;
    }

    public Class<?> getEventType() {
        return mEventType;
    }

    public void setEventType(Class<?> eventType) {
        mEventType = eventType;
    }

    public Subscription getSubscriber() {
        return mSubscriber;
    }

    public void setSubscriber(Subscription subscriber) {
        mSubscriber = subscriber;
    }
}
