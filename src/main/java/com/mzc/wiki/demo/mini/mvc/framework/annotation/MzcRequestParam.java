package com.mzc.wiki.demo.mini.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * Spring MVC requestParam demo
 * Created by miaozc on 2019-8-17.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MzcRequestParam {
    String value() default "";
}
