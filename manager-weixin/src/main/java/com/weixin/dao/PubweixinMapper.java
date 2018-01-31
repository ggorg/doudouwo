package com.weixin.dao;

import com.gen.common.util.Page;
import com.weixin.entity.Pubweixin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信公众号
 * @author Jacky
 *
 */
@Mapper
public interface PubweixinMapper {
	
	Pubweixin selectByAppid(@Param("appid") String appid);
	Pubweixin selectByOpenid(@Param("openid") String openid);
	/**查询出所有公众号信息  获取accesstoken用 */
	List<Pubweixin> select();
	/**查询出所有删除公众号信息  移除accesstoken用 */
	List<Pubweixin> selectdel();
	
	int insert(Pubweixin pubweixin);
	
	int update(Pubweixin pubweixin);

	int delete(Pubweixin pubweixin);

	/**
     * 查询数据列表，如果需要分页
     *
     * @param entity
     * @return
     */
    public List<Pubweixin> findList(@Param("page") Page page, Pubweixin entity);
    /** 查询总数*/
    public int findListCount(Pubweixin entity);

    public List<Pubweixin> findListAll();
}
