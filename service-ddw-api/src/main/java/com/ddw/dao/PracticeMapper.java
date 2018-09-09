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
    List<AppIndexPracticeVO> getPracticeList( @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment);
    Integer getPracticeListCount(@Param("storeId") Integer storeId,@Param("appointment") Integer appointment);

    List<AppIndexPracticeVO> getPracticeHaveOrderListByNoInIds(@Param("userIdList") List<Integer> userIdList, @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment);

    List<AppIndexPracticeVO> getPracticeListByNotInIds(@Param("userIdList") List<Integer> userIdList, @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment);

    List getPracticeDynamic(@Param("practiceId") Integer practiceId);
}
