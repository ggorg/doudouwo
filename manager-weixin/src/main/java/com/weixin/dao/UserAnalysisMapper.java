package com.weixin.dao;


import com.weixin.entity.UserAnalysis;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户分析
 * @author Jacky
 *
 */
public interface UserAnalysisMapper {
	
	int insert(UserAnalysis entity);
	
	List<UserAnalysis> findList(UserAnalysis entity);
}
