package com.mzc.wiki.demo.simulation.framework.aop.framework;

import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInvocation;
import com.mzc.wiki.demo.simulation.framework.aop.support.MzcAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcJdkDynamicAopProxy implements MzcAopProxy , InvocationHandler {
    private MzcAdvisedSupport advised;

    public MzcJdkDynamicAopProxy(MzcAdvisedSupport config){
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        MzcMethodInvocation invocation = new MzcMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
