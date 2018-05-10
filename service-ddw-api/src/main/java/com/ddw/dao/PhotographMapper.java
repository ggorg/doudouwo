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
    public List<PhotographPO> findListByNames(@Param("condition") HashSet<String> condition);
}
