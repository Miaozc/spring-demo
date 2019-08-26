package com.mzc.wiki.demo.simulation.formework.context;

/**
 * 通过解耦方式获得 IOC 容器的顶层设计
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 * 将自动调用 setApplicationContext() 方法，从而将 IOC 容器注入到目标类中
 * Created by miaozc on 2019-8-26.
 */
public interface ApplicationContextAware {
    void setApplicationContext(MzcApplicationContext applicationContext);
}
