package com.mzc.wiki.demo;

import com.mzc.wiki.demo.simulation.demo.controller.MyAction;
import com.mzc.wiki.demo.simulation.framework.context.MzcApplicationContext;

/**
 * Created by miaozc on 2019-8-26.
 */
public class TestBeanFactory {
    public static void main(String[] args) {

        MzcApplicationContext context = new MzcApplicationContext("classpath:application.properties");
        try {
            Object object = context.getBean(MyAction.class);
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
