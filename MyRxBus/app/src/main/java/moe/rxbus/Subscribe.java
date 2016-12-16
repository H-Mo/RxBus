package moe.rxbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author H-Mo
 * @time 16/12/16  9:17
 * @desc 封装注解，方便对事件进行监听的注解，默认执行在当前线程
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    ThreadMode threadMode() default ThreadMode.CURRENT_THREAD;
}
