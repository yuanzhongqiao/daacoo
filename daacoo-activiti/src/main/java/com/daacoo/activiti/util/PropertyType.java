package com.daacoo.activiti.util;

import java.util.Date;

/**
 * 属性数据类型
 * @author daacoo
 * @version 2019/10/11
 */
public enum PropertyType {

    S(String.class),
    I(Integer.class),
    L(Long.class),
    F(Float.class),
    N(Double.class),
    D(Date.class),
    SD(java.sql.Date.class),
    B(Boolean.class);

    private Class<?> clazz;

    private PropertyType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getValue() {
        return clazz;
    }
}