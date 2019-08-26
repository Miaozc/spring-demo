package com.mzc.wiki.demo.simulation.formework.web.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by miaozc on 2019-8-26.
 */
@Data
public class MzcHandlerMapping {
    public MzcHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
    private Object controller;
    private Method method;
    private Pattern pattern; //url 的封装
}
