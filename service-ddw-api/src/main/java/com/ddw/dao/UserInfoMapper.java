package com.ddw.dao;

import com.ddw.beans.MyAttentionInfoVO;
import com.ddw.beans.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
@Mapper
public interface UserInfoMapper {
    List<UserInfoVO> getUserInfoList(@Param("userId") List<Integer> userId);
    List<MyAttentionInfoVO> getMyAttentionPracticeInfoList(@Param("userId") List<Integer> userId);
    List<MyAttentionInfoVO> getMyAttentionGoddessInfoList(@Param("userId") List<Integer> userId);
    UserInfoVO getUserInfo(@Param("openid") String openid);
    UserInfoVO loginByOpenid(@Param("openid") String openid);
    UserInfoVO getUserInfoById(@Param("id") Integer id);
}
