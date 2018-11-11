package com.weixin.dao;

import com.weixin.entity.RedPack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 微信红包配置
 *
 * @author Jacky
 */
public interface RedPackMapper {
    int insert(@Param("userInfo") RedPack redPack);
    
    int update(RedPack redPack);
    
    RedPack selectByappid(@Param("appid") String appid);
    
}
