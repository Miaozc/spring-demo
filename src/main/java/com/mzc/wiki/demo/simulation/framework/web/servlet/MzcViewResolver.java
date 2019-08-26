package com.mzc.wiki.demo.simulation.framework.web.servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

/**
 * 这个类的主要目的是:
 * 1. 将一个静态文件变为一个动态文件
 * 2. 根据用户传递的参数不同, 产生不同的结果,最终输出字符串, 交给Response输出
 * Created by miaozc on 2019-8-26.
 */
public class MzcViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;
    private String viewName;
    public MzcViewResolver(String templateRoot){
        String templateRootPath =
                this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }
    public MzcView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if(null == viewName || "".equals(viewName.trim())){ return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        String filePath = null;
        try {
             filePath = URLDecoder.decode((templateRootDir.getPath() + "/" + viewName),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File templateFile = new File(filePath.replaceAll("/+", "/"));
        return new MzcView(templateFile);
    }
    public String getViewName() {
        return viewName;
    }
}
