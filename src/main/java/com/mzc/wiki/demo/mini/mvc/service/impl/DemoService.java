package com.mzc.wiki.demo.mini.mvc.service.impl;

import com.mzc.wiki.demo.mini.mvc.framework.annotation.MzcService;
import com.mzc.wiki.demo.mini.mvc.service.IDemoService;

/**
 * 核心业务逻辑
 */
@MzcService
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
