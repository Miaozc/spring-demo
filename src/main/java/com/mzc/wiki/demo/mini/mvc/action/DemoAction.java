package com.mzc.wiki.demo.mini.mvc.action;

import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcAutowired;
import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcRequestMapping;
import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcRequestParam;
import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcController;
import com.mzc.wiki.demo.mini.mvc.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MzcController
@MzcRequestMapping("/demo")
public class DemoAction {

  	@MzcAutowired
    private IDemoService demoService;

	@MzcRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp,
					  @MzcRequestParam("name") String name){
		String result = demoService.get(name);
		try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@MzcRequestMapping("/add")
	public void add(HttpServletRequest req, HttpServletResponse resp,
					@MzcRequestParam("a") Integer a, @MzcRequestParam("b") Integer b){
		try {
			resp.getWriter().write(a + "+" + b + "=" + (a + b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@MzcRequestMapping("/remove")
	public void remove(HttpServletRequest req,HttpServletResponse resp,
					   @MzcRequestParam("id") Integer id){
	}

}
