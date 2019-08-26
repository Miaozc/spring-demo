package com.mzc.wiki.demo.simulation.demo.controller;

import com.mzc.wiki.demo.simulation.demo.service.IModifyService;
import com.mzc.wiki.demo.simulation.demo.service.IQueryService;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcAutowired;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcController;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcRequestMapping;
import com.mzc.wiki.demo.simulation.framework.annotation.MzcRequestParam;
import com.mzc.wiki.demo.simulation.framework.web.servlet.MzcModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by miaozc on 2019-8-26.
 */
@MzcController
@MzcRequestMapping("/web")
public class MyAction {
    @MzcAutowired
    IQueryService queryService;
    @MzcAutowired
    IModifyService modifyService;
    @MzcRequestMapping("/query.json")
    public MzcModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                 @MzcRequestParam("name") String name){
        String result = queryService.query(name);
        return out(response,result);
    }
    @MzcRequestMapping("/add*.json")
    public MzcModelAndView add(HttpServletRequest request,HttpServletResponse response,
                              @MzcRequestParam("name") String name,@MzcRequestParam("addr") String addr) throws Exception {
        String result = modifyService.add(name,addr);
        return out(response,result);
    }
    @MzcRequestMapping("/remove.json")
    public MzcModelAndView remove(HttpServletRequest request,HttpServletResponse response,
                                 @MzcRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(response,result);
    }
    @MzcRequestMapping("/edit.json")
    public MzcModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                               @MzcRequestParam("id") Integer id,
                               @MzcRequestParam("name") String name){
        String result = modifyService.edit(id,name);
        return out(response,result);
    }
    private MzcModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
