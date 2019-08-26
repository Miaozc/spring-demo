package com.mzc.wiki.demo.simulation.framework.beans.factory.config;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcBeanPostProcessor {
    //为在 Bean 的初始化前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
    //为在 Bean 的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
