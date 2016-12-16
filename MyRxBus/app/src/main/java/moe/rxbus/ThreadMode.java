package moe.rxbus;

/**
 * @author H-Mo
 * @time 16/12/16  9:19
 * @desc 线程模式，对应RxJava的四种线程模式
 */
public enum ThreadMode {
    CURRENT_THREAD,     // 当前线程
    MAIN,               // 主线程
    NEW_THREAD,         // 新的线程
    IO                  // 由IO调度的线程
}
