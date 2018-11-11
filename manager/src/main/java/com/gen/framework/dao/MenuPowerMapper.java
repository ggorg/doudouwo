package com.gen.framework.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MenuPowerMapper {
    List queryById(@Param("uid") Integer uid);
    List queryUidByMid(@Param("mid") Integer mid);
}
