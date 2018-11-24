package com.weixin.dao;

import com.weixin.entity.OldBringingNew;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Jacky on 2017/12/15.
 */
public interface OldBringingNewMapper {
    int findCount(@Param("newOpenid") String newOpenid);
    String getOpenid(@Param("openid") String openid);
    int insert(OldBringingNew entity);
}
