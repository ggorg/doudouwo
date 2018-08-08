package com.ddw.dao;

import com.ddw.beans.PhotographPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Jacky on 2018/5/10.
 */
@Mapper
public interface PhotographMapper {
    List<PhotographPO> findListByNames(@Param("condition") HashSet<String> condition);
    List<PhotographPO> findListByIds(@Param("condition") HashSet<Integer> condition);
}
