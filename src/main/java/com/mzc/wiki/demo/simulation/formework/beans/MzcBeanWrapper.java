package com.mzc.wiki.demo.simulation.formework.beans;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;
    public MzcBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }
    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }
    // 返回代理以后的 Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
