package com.mzc.wiki.demo.simulation.formework.beans.factory.config;

import lombok.Data;

/**
 * Created by miaozc on 2019-8-26.
 */
@Data
public class MzcBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
}
