package weixin.dao;

import com.ty.entity.ShakeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信摇一摇
 * @author Jacky
 *
 */
@Mapper
public interface ShakeRecordMapper {
	
	int insert(ShakeRecord shakeRecord);
}
