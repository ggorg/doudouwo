package com.ddw.dao;

import com.ddw.beans.vo.AppIndexPracticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
@Mapper
public interface PracticeMapper {
    List<AppIndexPracticeVO> getPracticeList(@Param("practiceId") Integer practiceId, @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end);
    Integer getPracticeListCount(@Param("practiceId") Integer practiceId, @Param("storeId") Integer storeId);
}
