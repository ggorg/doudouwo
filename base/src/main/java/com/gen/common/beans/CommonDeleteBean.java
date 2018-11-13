package com.gen.common.beans;


import com.gen.common.util.BeanToMapUtil;

import java.util.Map;

public class CommonDeleteBean {
    private String tablename;
    private Object condition;
    private boolean isAuto=true;

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

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
