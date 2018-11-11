package com.ddw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface WalletDealMapper {
    public List dealRecord(@Param("userId") Integer userId,@Param("date") String date,@Param("startNum") Integer startNum,@Param("endNum") Integer endNum);
}
