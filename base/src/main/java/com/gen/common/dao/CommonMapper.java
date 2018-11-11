package com.gen.common.dao;

import com.gen.common.beans.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface CommonMapper {
	long selectCount(CommonCountBean commonCountBean);
	List selectObjects(CommonSearchBean commonSearchBean);
	int insertObject(CommonInsertBean commonInsertBean);
	int updateObject(CommonUpdateBean commonUpdateBean);
	int deleteObject(CommonDeleteBean dommonDeleteBean);
	int deleteObjectCombination(CommonDeleteBean dommonDeleteBean);
}
