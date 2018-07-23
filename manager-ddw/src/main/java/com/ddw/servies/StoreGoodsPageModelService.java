package com.ddw.servies;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.GoodsPageModelDTO;
import com.ddw.beans.GoodsPageModelItemDTO;
import com.ddw.enums.GoodsPageModelStatusEnum;
import com.ddw.enums.GoodsPlatePosEnum;
import com.ddw.enums.GoodsStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class StoreGoodsPageModelService extends CommonService {

    public Page findPage(Integer pageNo, Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("storeId",storeId);

        return this.commonPage("ddw_goods_page_model","updateTime desc",pageNo,10,condtion);

    }
    public Map get(String idStr)throws Exception{
        if(StringUtils.isNotBlank(idStr)){
            Integer id=Integer.parseInt(MyEncryptUtil.getRealValue(idStr));
            Map map= this.commonObjectBySingleParam("ddw_goods_page_model","id",id);
            map.put("goodsObjs",JSON.parseArray(map.get("jsonStr").toString()));
            map.remove("jsonStr");
            return map;
        }

        return new HashMap();
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateStatus(String idStr,Integer status, Integer storeId){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(GoodsStatusEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);

        }
        Integer id=Integer.parseInt(ids);
        Map map= new HashMap();
        map.put("status",status);
        Map searchMap=new HashMap();
        searchMap.put("id",id);
        searchMap.put("storeId",storeId);
        ResponseVO ret=this.commonUpdateByParams("ddw_goods_page_model",map,searchMap);
        if(ret.getReCode()==1){
            if(GoodsPageModelStatusEnum.goodsStatus1.getCode().equals(status)){
                return new ResponseVO(1,"发布成功",null);
            }else if(GoodsPageModelStatusEnum.goodsStatus0.getCode().equals(status)){
                return new ResponseVO(1,"撤回发布成功",null);
            }

        }
        return new ResponseVO(-2,"操作失败",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(GoodsPageModelDTO dto,Integer storeId){
        if(dto.getPlatePos()==null || StringUtils.isBlank(GoodsPlatePosEnum.getName(dto.getPlatePos()))){
            return new ResponseVO(-2,"请选择类型",null);
        }
        if(dto.getGitem()==null || dto.getGitem().isEmpty()){
            return new ResponseVO(-2,"商品信息不能为空",null);
        }
        Map editMap=new HashMap<>();
        editMap.put("storeId",storeId);
        editMap.put("updateTime",new Date());
        editMap.put("platePos",dto.getPlatePos());
        editMap.put("status", GoodsPageModelStatusEnum.goodsStatus0.getCode());
        editMap.put("jsonStr", JSON.toJSONString(dto.getGitem()));
        List<GoodsPageModelItemDTO> itemList=dto.getGitem();
        Set gids=new HashSet();
        for(GoodsPageModelItemDTO item:itemList){
            if(item.getGids()!=null && !item.getGids().isEmpty()){
                gids.addAll(item.getGids());
            }
        }
        if(!gids.isEmpty()){
            editMap.put("gids",gids.toString().replaceAll("[\\[\\]]",""));
        }
        ResponseVO res=null;
        if(dto.getId()==null || dto.getId()<=0){
            editMap.put("createTime",new Date());
            res=this.commonInsertMap("ddw_goods_page_model",editMap);
        }else{
            Map searchMap=new HashMap();
            searchMap.put("id",dto.getId());
            searchMap.put("storeId",storeId);
            res=this.commonUpdateByParams("ddw_goods_page_model",editMap,searchMap);
        }
        if(res.getReCode()!=1){
            return new ResponseVO(-2,"提交失败",null);
        }
        return new ResponseVO(1,"提交成功",null);

    }
}
