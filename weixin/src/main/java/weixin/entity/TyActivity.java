package weixin.entity;

import java.util.Date;

public class TyActivity {
    private Integer id;
    private String taName;
    private Integer taAmount;
    private Integer taUsed;
    private Date taBeginDate;
    private Date taEndDate;
    private String taRemark;
    private Integer taMaxCost;
    private Integer taMinCost;
    private String taRule;
    private Integer taNumber;
    private Integer taDisabled;

    public Boolean getTaDisabled() {
        return taDisabled==1?true:false;
    }

    public void setTaDisabled(Integer taDisabled) {
        this.taDisabled = taDisabled;
    }

    public Integer getTaNumber() {
        return taNumber;
    }

    public void setTaNumber(Integer taNumber) {
        this.taNumber = taNumber;
    }

    public Integer getTaUsed() {
        return taUsed;
    }

    public void setTaUsed(Integer taUsed) {
        this.taUsed = taUsed;
    }

    public String getTaRule() {
        return taRule;
    }

    public void setTaRule(String taRule) {
        this.taRule = taRule;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public Integer getTaAmount() {
        return taAmount;
    }

    public void setTaAmount(Integer taAmount) {
        this.taAmount = taAmount;
    }

    public Date getTaBeginDate() {
        return taBeginDate;
    }

    public void setTaBeginDate(Date taBeginDate) {
        this.taBeginDate = taBeginDate;
    }

    public Date getTaEndDate() {
        return taEndDate;
    }

    public void setTaEndDate(Date taEndDate) {
        this.taEndDate = taEndDate;
    }

    public String getTaRemark() {
        return taRemark;
    }

    public void setTaRemark(String taRemark) {
        this.taRemark = taRemark;
    }

    public Integer getTaMaxCost() {
        return taMaxCost;
    }

    public void setTaMaxCost(Integer taMaxCost) {
        this.taMaxCost = taMaxCost;
    }

    public Integer getTaMinCost() {
        return taMinCost;
    }

    public void setTaMinCost(Integer taMinCost) {
        this.taMinCost = taMinCost;
    }
}
