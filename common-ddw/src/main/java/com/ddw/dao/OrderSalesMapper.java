package com.ddw.dao;

import com.ddw.beans.SalesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销量统计
 *
 */
@Mapper
public interface OrderSalesMapper {


    List<SalesVO> sales(@Param("orderType") Integer orderType, @Param("busIds") String busIds, @Param("date") String date);
}
