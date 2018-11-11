package com.ddw.dao;

import com.ddw.beans.UserInfoPO;
import com.gen.common.util.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员信息
 * Created by Jacky on 2018/4/12.
 */
public interface UserInfoMapper {
    public int save(@Param("userInfoPO")UserInfoPO userInfoPO);

    public int update(@Param("userInfoPO")UserInfoPO userInfoPO);

    public UserInfoPO queryByUserName(@Param("username")String username);

    List<UserInfoPO> findList(@Param("page") Page page);
}
