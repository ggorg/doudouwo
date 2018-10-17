package com.ddw.beans;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
public class MyAttentionUserVO {
    private int userId;
    private int goddessCount;
    private int practiceCount;
    private List<UserInfoVO> userInfoList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGoddessCount() {
        return goddessCount;
    }

    public void setGoddessCount(int goddessCount) {
        this.goddessCount = goddessCount;
    }

    public int getPracticeCount() {
        return practiceCount;
    }

    public void setPracticeCount(int practiceCount) {
        this.practiceCount = practiceCount;
    }

    public List<UserInfoVO> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfoVO> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
