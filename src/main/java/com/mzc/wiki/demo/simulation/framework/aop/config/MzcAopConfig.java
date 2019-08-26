package com.mzc.wiki.demo.simulation.framework.aop.config;

import lombok.Data;

/**
 *
 */
@Data
public class MzcAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
