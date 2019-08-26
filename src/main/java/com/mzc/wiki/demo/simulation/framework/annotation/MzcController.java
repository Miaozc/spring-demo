package com.mzc.wiki.demo.simulation.framework.annotation;

import java.lang.annotation.*;

/**
 * Spring MVC contorller demo
 * Created by miaozc on 2019-8-17.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MzcController {
    String value() default "";
}
