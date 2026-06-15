package com.app.noobshop.aop.annotation.common;

import com.app.noobshop.aop.annotation.emums.CheckEnum;

import java.lang.annotation.*;

/**
 * 方法参数非空校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamCheckAnnotation {

    /**
     * 校验模式，默认校验字符串非空（含空格）
     */
    CheckEnum mode() default CheckEnum.NOT_BLANK;
}