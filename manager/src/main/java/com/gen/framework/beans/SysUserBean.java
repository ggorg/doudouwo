package com.gen.framework.beans;

public class SysUserBean {
    private Integer id;
    private String uName;
    private String uPassword;
    private Boolean disabled;

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "SysUserBean{" +
                "uName='" + uName + '\'' +
                ", uPassword='" + uPassword + '\'' +
                '}';
    }
}
