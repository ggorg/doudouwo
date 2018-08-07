package com.ddw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@SuppressWarnings("rawtypes")
@Mapper
public interface StoreProductFormulaMaterialMapper {
    List getFormulaMaterialWeight(@Param("orderNo") String orderNo);
}
