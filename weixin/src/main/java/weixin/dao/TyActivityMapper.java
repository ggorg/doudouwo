package weixin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TyActivityMapper {
    List getActivity(@Param("tUid") Integer tUid, @Param("actId") Integer actId);
}
