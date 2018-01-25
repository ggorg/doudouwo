package weixin.entity;

import java.util.Date;

public class TyRedPacket {
    private Integer id;
    private Integer tUid;
    private Integer trAmount;
    private Date trSendDate;
    private String trFromId;
    private String trActivityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer gettUid() {
        return tUid;
    }

    public void settUid(Integer tUid) {
        this.tUid = tUid;
    }

    public Integer getTrAmount() {
        return trAmount;
    }

    public void setTrAmount(Integer trAmount) {
        this.trAmount = trAmount;
    }

    public Date getTrSendDate() {
        return trSendDate;
    }

    public void setTrSendDate(Date trSendDate) {
        this.trSendDate = trSendDate;
    }

    public String getTrFromId() {
        return trFromId;
    }

    public void setTrFromId(String trFromId) {
        this.trFromId = trFromId;
    }

    public String getTrActivityId() {
        return trActivityId;
    }

    public void setTrActivityId(String trActivityId) {
        this.trActivityId = trActivityId;
    }
}
