package com.mzc.wiki.demo.simulation.framework.aop.framework;

import com.mzc.wiki.demo.simulation.framework.aop.support.MzcAdvisedSupport;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcCglibAopProxy implements MzcAopProxy {
    private MzcAdvisedSupport config;
    public MzcCglibAopProxy(MzcAdvisedSupport config){
        this.config = config;
    }
    public Object getProxy() {
        return null;
    }

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
