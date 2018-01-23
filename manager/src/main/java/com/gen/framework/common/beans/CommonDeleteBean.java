package com.gen.framework.common.beans;

import com.gen.framework.common.util.BeanToMapUtil;

import java.util.Map;

public class CommonDeleteBean {
    private String tablename;
    private Object condition;

    public CommonDeleteBean(String tablename, Object condition) {
        this.tablename = tablename;
        this.setCondition(condition);
    }

    public Object getCondition() {
        return condition;
    }

    public void setCondition(Object condition) {
        if(condition!=null){
            if(condition instanceof Map){
                this.condition=(Map)condition;
            }else{
                this.condition= BeanToMapUtil.beanToMap(condition);
            }
        }
    }
}
