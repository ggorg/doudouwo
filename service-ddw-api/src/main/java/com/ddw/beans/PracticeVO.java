package com.ddw.beans;

/**
 * Created by Jacky on 2018/5/28.
 */
public class PracticeVO {
    private int userId;
    private int appointment;
    private int storeId;
    private String tableNo;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAppointment() {
        return appointment;
    }

    public void setAppointment(int appointment) {
        this.appointment = appointment;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

}
