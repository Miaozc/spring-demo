package com.mzc.wiki.demo.simulation.formework.annotation;

import java.lang.annotation.*;

/**
 * Spring service demo
 * Created by miaozc on 2019-8-17.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MzcService {
    String value() default "";
}
