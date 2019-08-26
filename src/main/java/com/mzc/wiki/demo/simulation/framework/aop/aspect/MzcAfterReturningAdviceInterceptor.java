package com.mzc.wiki.demo.simulation.framework.aop.aspect;

import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInterceptor;
import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInvocation;

import java.lang.reflect.Method;

/**
 *
 * Created by miaozc on 2019-8-27.
 */
public class MzcAfterReturningAdviceInterceptor extends MzcAbstractAspectAdvice implements MzcAdvice, MzcMethodInterceptor {

    private MzcJoinPoint joinPoint;

    public MzcAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MzcMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
