package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/7/8.
 */
public class GoddessEvaluationPO {
    private Integer id;
    private Integer countEvaluation;
    private Integer goddessId;
    private Integer allStar;
    private Integer star;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCountEvaluation() {
        return countEvaluation;
    }

    public void setCountEvaluation(Integer countEvaluation) {
        this.countEvaluation = countEvaluation;
    }

    public Integer getGoddessId() {
        return goddessId;
    }

    public void setGoddessId(Integer goddessId) {
        this.goddessId = goddessId;
    }

    public Integer getAllStar() {
        return allStar;
    }

    public void setAllStar(Integer allStar) {
        this.allStar = allStar;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
