package com.mzc.wiki.demo.simulation.framework.beans.factory;

/**
 *
 * Created by miaozc on 2019-8-26.
 */
public interface MzcBeanFactory {
    /**
     * 根据beanName从IOC容器中获得一个实例的Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
