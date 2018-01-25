package weixin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TyRedPacketMapper {
    List getCount(@Param("trIsOpen") Boolean trIsOpen);
}
