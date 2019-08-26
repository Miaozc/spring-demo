package com.mzc.wiki.demo.simulation.demo.service;

import com.mzc.wiki.demo.simulation.formework.annotation.MzcService;

/**
 * Created by miaozc on 2019-8-26.
 */
@MzcService
public class ModifyService implements IModifyService {
    /**
     * 增加
     */
    public String add(String name,String addr) {
        return "modifyService add,name=" + name + ",addr=" + addr;
    }
    /**
     * 修改
     */
    public String edit(Integer id,String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }
    /**
     * 删除
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }
}
