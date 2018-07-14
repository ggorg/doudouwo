package com.ddw.beans;

import java.util.Date;

/**
 * Created by Jacky on 2018/7/8.
 */
public class PracticeEvaluationPO {
    private int id;
    private int countEvaluation;
    private int practiceId;
    private int allStar;
    private int star;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountEvaluation() {
        return countEvaluation;
    }

    public void setCountEvaluation(int countEvaluation) {
        this.countEvaluation = countEvaluation;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public int getAllStar() {
        return allStar;
    }

    public void setAllStar(int allStar) {
        this.allStar = allStar;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
