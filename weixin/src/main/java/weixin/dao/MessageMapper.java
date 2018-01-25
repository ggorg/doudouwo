package weixin.dao;

import com.gen.framework.common.util.Page;
import com.ty.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图文消息
 * Created by Jacky on 2017/12/6.
 */
@Mapper
public interface MessageMapper {
    List<Message> findList(@Param("page") Page page, @Param("appid") String appid);
    /** 不分页*/
    List<Message> findListAll(@Param("appid") String appid);
    /** 查询总数*/
    public int findListCount(@Param("appid") String appid);
    /*根据父id查询子图文数据*/
    List<Message> findListById(@Param("id") Integer id);
    int insert(Message message);
    int delete(@Param("id") Integer id);
    int update(Message message);
    Message selectById(@Param("id") int id);
}
