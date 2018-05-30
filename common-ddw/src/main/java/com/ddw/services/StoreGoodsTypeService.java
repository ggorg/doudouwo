package com.ddw.services;

import com.ddw.beans.GoodsTypeDTO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreGoodsTypeService extends CommonService {
    @Cacheable(value = "publicCache",key = "'goodsType-'+#storeId")
    public List getGoodsType(Integer storeId)throws Exception{
        Map map=new HashMap();
        map.put("storeId",storeId);
        return this.commonList("ddw_goods_type","dgtSort asc",null,null,map);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveGoodsType(GoodsTypeDTO dto, Integer storeId){
        if(StringUtils.isBlank(dto.getDgtName())){
            return new ResponseVO(-2,"类型名称是空",null);
        }
        Map map= BeanToMapUtil.beanToMap(dto);
        map.put("updateTime",new Date());

        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("storeId",storeId);
            ResponseVO vo=this.commonInsertMap("ddw_goods_type",map);
            if(vo.getReCode()==1){
                return new ResponseVO(1,"成功",null);
            }
        }else{
            Map search=new HashMap();
            search.put("id",dto.getId());
            search.put("storeId",storeId);
            ResponseVO vo=this.commonUpdateByParams("ddw_goods_type",map,search);
            if(vo.getReCode()==1){
                CacheUtil.delete("publicCache","goodsType-"+storeId);
                return new ResponseVO(1,"成功",null);
            }

        }
        return new ResponseVO(-2,"失败",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deleteGoodsType(String idStr,Integer storeId){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        Map map=new HashMap();
        map.put("id",Integer.parseInt(ids));
        map.put("storeId",storeId);
        ResponseVO vo=this.commonDeleteByParams("ddw_goods_type",map);
        if(vo.getReCode()==1){
            CacheUtil.delete("publicCache","goodsType-"+storeId);
        }
        return vo;
    }
}
