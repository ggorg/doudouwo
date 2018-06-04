package com.ddw.dao;

import com.ddw.beans.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
@Mapper
public interface UserInfoMapper {
    public List<UserInfoVO> getUserInfoList(@Param("userId") List<String> userId);
}
