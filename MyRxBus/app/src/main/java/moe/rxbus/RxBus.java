package moe.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @author H-Mo
 * time 16/12/14  9:25
 * desc 用 RxJava 实现的事件总线
 */
public class RxBus {

    // 私有化的静态自身变量，volatile 用来保证原子性操作
    private static volatile RxBus mInstance;

    // final 定义的总线
    private final Subject<Object,Object> mBus;

    // 私有化的构造器中初始化总线
    private RxBus(){
        // 把线程不安全的 PublishSubject 包装成线程安全的 SerializedSubject
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 单例的懒汉式，获取自身对象
     * @return 自身的实例
     */
    public static RxBus get(){
        if(mInstance == null){
            synchronized (RxBus.class){
                if(mInstance == null){
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 投递一个事件
     * @param event 事件
     */
    public void post(Object event){
        mBus.onNext(event);
    }

    /**
     *  传入需要订阅的事件类型，得到可用于订阅的被观察者对象
     * @param eventType 事件类型
     * @return 可用于订阅的被观察者对象
     */
    public <T> Observable<T> toObservable(Class<T> eventType){
        return mBus.ofType(eventType);
    }
}
