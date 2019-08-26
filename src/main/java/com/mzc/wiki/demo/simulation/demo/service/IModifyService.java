package com.mzc.wiki.demo.simulation.demo.service;

/**
 * 增删改业务
 * Created by miaozc on 2019-8-26.
 */
public interface IModifyService {
    /**
     * 增加
     */
    String add(String name, String addr) throws Exception;
    /**
     * 修改
     */
    String edit(Integer id, String name);
    /**
     * 删除
     */
    String remove(Integer id);
}
