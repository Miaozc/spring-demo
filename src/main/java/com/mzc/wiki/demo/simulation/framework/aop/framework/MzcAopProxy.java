package com.mzc.wiki.demo.simulation.framework.aop.framework;

/**
 * 默认就用JDK动态代理
 * Created by miaozc on 2019-8-26.
 */
public interface MzcAopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
