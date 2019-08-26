package com.mzc.wiki.demo.simulation.framework.aop.aspect;

import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInterceptor;
import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInvocation;

import java.lang.reflect.Method;

/**
 *
 * Created by miaozc on 2019-8-27.
 */
public class MzcAfterThrowingAdviceInterceptor extends MzcAbstractAspectAdvice implements MzcAdvice, MzcMethodInterceptor {


    private String throwingName;

    public MzcAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MzcMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
