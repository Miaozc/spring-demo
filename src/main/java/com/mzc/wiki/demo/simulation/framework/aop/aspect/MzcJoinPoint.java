package com.mzc.wiki.demo.simulation.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 *
 * Created by miaozc on 2019-8-27.
 */
public interface MzcJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
