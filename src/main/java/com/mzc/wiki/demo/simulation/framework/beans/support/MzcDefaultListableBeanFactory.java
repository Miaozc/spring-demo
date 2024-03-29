package com.mzc.wiki.demo.simulation.framework.beans.support;

import com.mzc.wiki.demo.simulation.framework.beans.factory.config.MzcBeanDefinition;
import com.mzc.wiki.demo.simulation.framework.context.support.MzcAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcDefaultListableBeanFactory extends MzcAbstractApplicationContext  {

    //存储注册信息的BeanDefinition
    public final Map<String, MzcBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MzcBeanDefinition>(256);
}
