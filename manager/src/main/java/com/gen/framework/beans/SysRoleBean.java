package com.gen.framework.beans;

public class SysRoleBean {
    private Integer id;
    private String rName;
    private String rByname;
    private String rType;

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getrByname() {
        return rByname;
    }

    public void setrByname(String rByname) {
        this.rByname = rByname;
    }

    public String getrType() {
        return rType;
    }

    public void setrType(String rType) {
        this.rType = rType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
