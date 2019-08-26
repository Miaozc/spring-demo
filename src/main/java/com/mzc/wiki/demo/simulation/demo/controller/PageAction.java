package com.mzc.wiki.demo.simulation.demo.controller;

import com.mzc.wiki.demo.simulation.demo.service.IQueryService;
import com.mzc.wiki.demo.simulation.formework.annotation.MzcAutowired;
import com.mzc.wiki.demo.simulation.formework.annotation.MzcController;
import com.mzc.wiki.demo.simulation.formework.annotation.MzcRequestMapping;
import com.mzc.wiki.demo.simulation.formework.annotation.MzcRequestParam;
import com.mzc.wiki.demo.simulation.formework.web.servlet.MzcModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miaozc on 2019-8-26.
 */
@MzcController
@MzcRequestMapping("/")
public class PageAction {
    @MzcAutowired
    IQueryService queryService;
    @MzcRequestMapping("/first.html")
    public MzcModelAndView query(@MzcRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new MzcModelAndView("first.html",model);
    }
}