package com.ddw.beans;

/**
 * Created by Jacky on 2018/5/10.
 */
public class GoddessDTO {
    private int id;
    private int userId;
    private int appointment;
    private String tableNo;
    private int bidPrice;
    private int earnest;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getEarnest() {
        return earnest;
    }

    public void setEarnest(int earnest) {
        this.earnest = earnest;
    }
}
