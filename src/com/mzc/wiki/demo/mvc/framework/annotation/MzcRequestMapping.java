package com.mzc.wiki.demo.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * Spring MVC RequestMapping demo
 * Created by miaozc on 2019-8-17.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MzcRequestMapping {
    String value() default "";
}
