package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.Tags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签管理
 * Created by Jacky on 2017/12/27.
 */
public interface TagsMapper {
    List<Tags> findList(@Param("page") Page page, @Param("entity") Tags entity);
    List<Tags> findListAll(@Param("entity") Tags entity);
    int findListCount(@Param("entity") Tags entity);
    int insert(@Param("entity") Tags entity);
    int delete(@Param("entity") Tags entity);
    int deleteByAppid(@Param("appid") String appid);
    int update(Tags entity);
    Tags selectById(@Param("entity") Tags entity);
}
