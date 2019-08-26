package com.mzc.wiki.demo.simulation.framework.context;

import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcService;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcAutowired;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcController;
import com.mzc.wiki.demo.simulation.framework.beans.MzcBeanWrapper;
import com.mzc.wiki.demo.simulation.framework.beans.factory.MzcBeanFactory;
import com.mzc.wiki.demo.simulation.framework.beans.factory.config.MzcBeanDefinition;
import com.mzc.wiki.demo.simulation.framework.beans.factory.config.MzcBeanPostProcessor;
import com.mzc.wiki.demo.simulation.framework.beans.factory.support.MzcBeanDefinitionReader;
import com.mzc.wiki.demo.simulation.framework.beans.support.MzcDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by miaozc on 2019-8-26.
 */
public class MzcApplicationContext extends MzcDefaultListableBeanFactory implements MzcBeanFactory {
    private String[] configLoactions;
    private MzcBeanDefinitionReader reader;
    //单例的 IOC 容器缓存
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的 IOC 容器
    private Map<String, MzcBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MzcBeanWrapper>();

    //用来保证注册式单例的容器
    private Map<String, Object> singletonBeanCacheMap = new HashMap<String, Object>();
    //用来存储所有的被代理过的对象
    private Map<String, MzcBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, MzcBeanWrapper>();

    public MzcApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new MzcBeanDefinitionReader(this.configLoactions);
        //2、加载配置文件，扫描相关的类，把它们封装成 BeanDefinition
        List<MzcBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3、注册，把配置信息放到容器里面(伪 IOC 容器)
        doRegisterBeanDefinition(beanDefinitions);
        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, MzcBeanDefinition> beanDefinitionEntry :
                super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<MzcBeanDefinition> beanDefinitions) throws Exception {
        for (MzcBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，通过读取 BeanDefinition 中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
    //装饰器模式：
    //1、保留原来的 OOP 关系
    //2、我需要对它进行扩展，增强（为了以后 AOP 打基础）
    public Object getBean(String beanName) throws Exception {
        MzcBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            MzcBeanPostProcessor beanPostProcessor = new MzcBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            MzcBeanWrapper beanWrapper = new MzcBeanWrapper(instance);
            this.beanWrapperMap.put(beanName, beanWrapper);
            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            populateBean(beanName, instance);
            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        //不是所有牛奶都叫特仑苏
        if (!(clazz.isAnnotationPresent(MzcController.class) ||
                clazz.isAnnotationPresent(MzcService.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(MzcAutowired.class)) {
                continue;
            }
            MzcAutowired autowired = field.getAnnotation(MzcAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
            }
        }
    }

    //传一个 BeanDefinition，就返回一个实例 Bean
    private Object instantiateBean(MzcBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            //因为根据 Class 才能确定一个类是否有实例
            if (this.singletonBeanCacheMap.containsKey(className)) {
                instance = this.singletonBeanCacheMap.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonBeanCacheMap.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new
                String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
