package moe.rxbus.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @author H-Mo
 * time 16/12/14  9:25
 * desc 用 RxJava 实现的事件总线的核心类
 */
public class RxBusCore {

    // 私有化的静态自身变量，volatile 用来保证原子性操作
    private static volatile RxBusCore mInstance;
    // final 定义的总线
    private final Subject<Object,Object> mBus;
    // 存储粘性事件的 Map
    private final Map<Class<?>,Object> mStickyEventMap;

    // 私有化的构造器中初始化总线
    private RxBusCore(){
        // 把线程不安全的 PublishSubject 包装成线程安全的 SerializedSubject
        mBus = new SerializedSubject<>(PublishSubject.create());
        // 初始化，线程安全的 HashMap ,采用 stripping lock (分离锁)，效率比 HashTable 高
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * 单例的懒汉式，获取自身对象
     * @return 自身的实例
     */
    public static RxBusCore get(){
        if(mInstance == null){
            synchronized (RxBusCore.class){
                if(mInstance == null){
                    mInstance = new RxBusCore();
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
     * 投递一个粘性事件
     * @param event 事件
     */
    public void postSticky(Object event){
        // 同步锁确保线程安全
        synchronized (mStickyEventMap){
            // key 事件类型 | value 事件
            mStickyEventMap.put(event.getClass(),event);
        }
        post(event);
    }

    /**
     *  传入需要订阅的事件类型，得到可用于订阅的被观察者对象
     * @param eventType 事件类型
     * @return 可用于订阅的被观察者对象
     */
    public <T> Observable<T> toObservable(Class<T> eventType){
        return mBus.ofType(eventType);
    }

    /**
     * 传入需要订阅的事件类型，得到可用于订阅的被观察者对象，并且可以收到粘性事件
     * @param eventType 事件类型
     * @return  可用于订阅的被观察者对象
     */
    public <T> Observable<T> toObservableSticky(final Class<T> eventType){
        // 同步锁确保线程安全
        synchronized (mStickyEventMap){
            // 得到被观察者对象
            Observable<T> observable = mBus.ofType(eventType);
            // 从 Map 中取出上一个相同类型的事件
            final Object event  = mStickyEventMap.get(eventType);
            // 如果存在上一个相同类型的事件
            if(event != null){
                // 通过 mergeWith 操作符，将从 Map 中取出来的事件合并进被观察者中，并返回
                return observable.mergeWith(Observable.create(new Observable.OnSubscribe<T>(){

                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        // cast 将一个对象强制转换成此 Class 对象所表示的类或接口。
                        subscriber.onNext(eventType.cast(event));
                    }

                }));
            }else {
                // 如果不存在上一个相同得到事件，直接返回被观察者对象
                return observable;
            }
        }
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers(){
        return mBus.hasObservers();
    }

    /**
     *  将 RxBusCore 的实例置为 null
     */
    public void reset(){
        mInstance = null;
    }

    /**
     *  获取指定类型的粘性事件
     * @param eventType 事件类型
     * @return 上一个同类型的事件
     */
    public <T> T getStickyEvent(Class<T> eventType){
        synchronized (mStickyEventMap){
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定类型的粘性事件
     * @param eventType 事件类型
     * @return 被移除的事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap){
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的粘性事件
     */
    public void removeAllStickyEvents(){
        synchronized (mStickyEventMap){
            mStickyEventMap.clear();
        }
    }
}
