package com.gen.framework.common.dao;

import java.util.List;

import com.gen.framework.common.beans.*;
import org.apache.ibatis.annotations.Mapper;

@SuppressWarnings("rawtypes")
@Mapper
public interface CommonMapper {
	long selectCount(CommonCountBean commonCountBean);
	List selectObjects(CommonSearchBean commonSearchBean);
	int insertObject(CommonInsertBean commonInsertBean);
	int updateObject(CommonUpdateBean commonUpdateBean);
	int deleteObject(CommonDeleteBean dommonDeleteBean);
}
