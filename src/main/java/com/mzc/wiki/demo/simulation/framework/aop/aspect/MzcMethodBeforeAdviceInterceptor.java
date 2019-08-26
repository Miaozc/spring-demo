package com.mzc.wiki.demo.simulation.framework.aop.aspect;

import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInterceptor;
import com.mzc.wiki.demo.simulation.framework.aop.intercept.MzcMethodInvocation;

import java.lang.reflect.Method;

/**
 *
 * Created by miaozc on 2019-8-27.
 */
public class MzcMethodBeforeAdviceInterceptor extends MzcAbstractAspectAdvice implements MzcAdvice, MzcMethodInterceptor {


    private MzcJoinPoint joinPoint;
    public MzcMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }
    @Override
    public Object invoke(MzcMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
