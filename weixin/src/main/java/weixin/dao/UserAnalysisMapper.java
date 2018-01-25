package weixin.dao;


import com.ty.entity.UserAnalysis;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户分析
 * @author Jacky
 *
 */
@Mapper
public interface UserAnalysisMapper {
	
	int insert(UserAnalysis entity);
	
	List<UserAnalysis> findList(UserAnalysis entity);
}
