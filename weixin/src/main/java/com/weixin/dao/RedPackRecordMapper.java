package com.weixin.dao;


import com.weixin.entity.RedPackRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 红包发送记录
 * @author Jacky
 *
 */
@Mapper
public interface RedPackRecordMapper {
	int insert(RedPackRecord redPackRecord);
}
