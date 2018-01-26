package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 微信用户资料
 *
 * @author Jacky
 */
@Mapper
public interface UserInfoMapper {
    int insert(@Param("userInfo") UserInfo userInfo);
    
    int update(UserInfo userInfo);

    /**
     * 批量修改用户标签
     * @param userInfo 筛选条件
     * @param tagid_list
     * @return
     */
    int batchUpdateTags(@Param("userInfo") UserInfo userInfo, @Param("tagid_list") String tagid_list);

    UserInfo selectByopenid(@Param("openid") String openid);

    /**
     * 查询数据列表，如果需要分页，请设置分页对象
     *
     * @param userInfo 筛选条件
     * @return
     */
    List<UserInfo> findList(@Param("page") Page page, @Param("userInfo") UserInfo userInfo);

    List<UserInfo> findListAll(@Param("userInfo") UserInfo userInfo);

    /**
     * 查询总数
     * @param userInfo 筛选条件
     * @return
     */
    int findListCount(@Param("userInfo") UserInfo userInfo);
    
    /**
     * 获取用户openid列表
     * @param appid 应用ID
     * @return 用户openid
     */
    List<Map<String,String>> findOpenidList(@Param("appid") String appid);
}
