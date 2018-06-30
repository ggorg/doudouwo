package com.ddw.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Jacky on 2018/6/30.
 */
@Mapper
public interface WalletErrorLogMapper {
    public int errorTodayCount(Integer userId);
}
