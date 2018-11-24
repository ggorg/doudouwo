package com.ddw.dao;

import com.ddw.beans.AllTypeSalesVO;
import com.ddw.beans.SalesVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销量统计
 *
 */
public interface BiddingCommonMapper {


    List getBiddingNoIncome(@Param("bidId") Integer bidId);
}
