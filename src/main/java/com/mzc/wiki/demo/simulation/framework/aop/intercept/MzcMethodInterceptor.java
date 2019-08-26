package com.mzc.wiki.demo.simulation.framework.aop.intercept;

/**
 * Created by Tom on 2019/4/14.
 */
public interface MzcMethodInterceptor {
    Object invoke(MzcMethodInvocation invocation) throws Throwable;
}
