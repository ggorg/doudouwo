package com.ddw.dao;

import com.ddw.beans.vo.AppIndexPracticeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2018/6/3.
 */
public interface PracticeMapper {
    List<AppIndexPracticeVO> getPracticeList( @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment);

    List<AppIndexPracticeVO> getPracticeHaveOrderListByNoInIds(@Param("userIdList") List<Integer> userIdList, @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment,@Param("weekList") Integer weekList);

    List<AppIndexPracticeVO> getPracticeListByNotInIds(@Param("userIdList") List<Integer> userIdList, @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("appointment") Integer appointment);

    List getPracticeDynamic(@Param("practiceId") Integer practiceId, @Param("start") Integer start, @Param("end") Integer end);

    List getReviewPracticeList(@Param("practiceId") Integer practiceId, @Param("storeId") Integer storeId);

    List<AppIndexPracticeVO> getListByOrder( @Param("storeId") Integer storeId, @Param("start") Integer start, @Param("end") Integer end,@Param("weekList") Integer weekList);
}
