package com.mzc.wiki.demo.simulation.demo.service;

import com.mzc.wiki.demo.simulation.framework.annotation.MzcService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by miaozc on 2019-8-26.
 */
@MzcService
@Slf4j
public class QueryService implements IQueryService {
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        log.info("这是在业务方法中打印的：" + json);
        return json;
    }
}
