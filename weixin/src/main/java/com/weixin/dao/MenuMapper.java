package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Jacky on 2017/12/6.
 */
@Mapper
public interface MenuMapper {
    List<Menu> findList(@Param("page") Page page, @Param("appid") String appid);
    /** 查询总数*/
    int findListCount(@Param("appid") String appid);
    /*根据父id查询子菜单*/
    List<Menu> findListById(@Param("id") Integer id);
    int insert(Menu menu);
    int delete(@Param("id") Integer id);
    int update(Menu menu);
    Menu selectById(@Param("id") int id);
}
