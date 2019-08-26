package com.mzc.wiki.demo.simulation.formework.web.servlet;

import lombok.Data;

import java.util.Map;

/**
 * Created by miaozc on 2019-8-26.
 */
@Data
public class MzcModelAndView {
    private String viewName;
    private Map<String,?> model;
    public MzcModelAndView(String viewName) {
        this(viewName,null);
    }
    public MzcModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
