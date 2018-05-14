package com.example.annotation;

import java.lang.annotation.*;

/**
 * Created by Zhangkh on 2017/12/1.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomLog {
    /**
     * 要执行的操作类型比如：add操作
     **/
    String operationType() default "";

    /**
     * 要执行的具体操作比如：添加用户
     **/
    String userName() default "";
}
