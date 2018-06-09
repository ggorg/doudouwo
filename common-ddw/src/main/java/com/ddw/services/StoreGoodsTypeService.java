package com.ddw.services;

import com.ddw.beans.GoodsTypeDTO;
import com.ddw.enums.DisabledEnum;
import com.ddw.enums.GoodsStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
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
@Transactional(readOnly = true)
public class StoreGoodsTypeService extends CommonService {

    public Page page(Integer pageNo, Integer storeId)throws Exception{

        Map map=new HashMap();
        map.put("storeId",storeId);
        return this.commonPage("ddw_goods_type","updateTime desc,dgtSort asc",pageNo,10,map);
    }
    public Map getGoodsType(Integer id,Integer storeId)throws Exception{
        Map map=new HashMap();
        map.put("storeId",storeId);
        map.put("id",id);
        return this.commonObjectBySearchCondition("ddw_goods_type",map);
    }
    @Cacheable(value = "publicCache",key = "'goodsType-'+#storeId")
    public List getGoodsType(Integer storeId)throws Exception{
        Map map=new HashMap();
        map.put("storeId",storeId);
        map.put("dgtDistabled",DisabledEnum.disabled0.getCode());
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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateStatus(String idStr,Integer status,Integer storeId){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);

        }
        Integer id=Integer.parseInt(ids);
        Map map= new HashMap();
        map.put("dgtDistabled",status);
        ResponseVO ret=this.commonUpdateBySingleSearchParam("ddw_goods_type",map,"id",id);
        if(ret.getReCode()==1){
            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"启用成功",null);
            }else if(GoodsStatusEnum.goodsStatus1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);
            }
            //@Cacheable(value = "publicCache",key = "'goodsType-'+#storeId")
            CacheUtil.delete("publicCache","goodsType-"+storeId);

        }
        return new ResponseVO(-2,"操作失败",null);
    }
}
