package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.GoodsStatusEnum;
import com.ddw.enums.GoodsTypeEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.dict.Dictionary;
import com.gen.common.dict.DictionaryUtils;
import com.gen.common.services.CommonService;
import com.gen.common.util.Tools;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsClientService extends CommonService {

    @Autowired
    private StoreGoodsTypeService storeGoodsTypeService;

    public ResponseApiVO index(String token)throws Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("dgStatus", GoodsStatusEnum.goodsStatus1.getCode());
        List<Map> gList=this.commonList("ddw_goods","dgSalesNumber desc",null,null,search);
        if(gList==null){
            return new ResponseApiVO(-2,"没有商品上架",null);
        }
        search=new HashMap();
        search.put("storeId",storeId);
        search.put("dghStatus",GoodsStatusEnum.goodsStatus1.getCode());
        List<Map> pList=this.commonList("ddw_goods_product","dghSalesPrice asc,dghActivityPrice asc",null,null,search);
        if(gList==null){
            return new ResponseApiVO(-2,"没有产品上架",null);
        }
        List<Map> gtypeList=this.storeGoodsTypeService.getGoodsType(storeId);
        if(gtypeList==null){
            return new ResponseApiVO(-2,"没有商品类型",null);

        }



        GoodsItemVO vo=null;
        Integer dgType=null;
        Integer typeId=null;

        List<Map> listData=new ArrayList();
        Map  typeMap=new HashMap();
        Map<Integer,List> pos=new HashMap();
        List goodsList=new ArrayList();

        typeMap.put("name","热销");
        typeMap.put("list",goodsList);
        listData.add(typeMap);
        pos.put(-1000,goodsList);
        for(Map gt:gtypeList){
            typeMap=new HashMap();
            goodsList=new ArrayList();
            listData.add(typeMap);
            typeMap.put("name",gt.get("dgtName"));
            typeMap.put("list",goodsList);
            pos.put((Integer) gt.get("id"),goodsList);
        }
        Integer gid=null;
        for(Map m:gList){
            dgType=(Integer) m.get("dgType");
            gid=(Integer) m.get("id");
            vo=new GoodsItemVO();
            PropertyUtils.copyProperties(vo,m);
            for(Map gt:gtypeList){

                typeId=(Integer) gt.get("id");
                if(dgType.equals(typeId)){
                    pos.get(typeId).add(vo);

                    vo.setProducts(new ArrayList());
                    for(Map p:pList){
                        if(p.get("dghGoodsId").equals(gid)){
                            Map pmap=new HashMap();
                            pmap.put("name",p.get("dghName"));
                            pmap.put("price",p.get("dghSalesPrice"));
                            pmap.put("actPrice",p.get("dghActivityPrice"));
                            pmap.put("code",p.get("id"));
                            vo.getProducts().add(pmap);
                        }
                    }
                }

            }
            if(pos.get(-1000).size()<5){
                pos.get(-1000).add(vo);
            }
        }
        return new ResponseApiVO(1,"成功",new ListVO(listData));
        //Map menusMap=
    }

    public ResponseApiVO getInfo(String token,CodeDTO dto)throws Exception{
        if(dto.getCode()==null ||dto.getCode()<0){
            return new ResponseApiVO(-2,"参数异常",null);
        }
        Integer storeId=TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("dgStatus", GoodsStatusEnum.goodsStatus1.getCode());
        search.put("id", dto.getCode());
        Map map= this.commonObjectBySearchCondition("ddw_goods",search);
        GoodsInfoVO gvo=new GoodsInfoVO();
        PropertyUtils.copyProperties(gvo,map);
        search=new HashMap();
        search.put("storeId",storeId);
        search.put("dghGoodsId",dto.getCode());
        search.put("dghStatus", GoodsStatusEnum.goodsStatus1.getCode());
        gvo.setPruduct(new ArrayList());
        List<Map> list=this.commonObjectsBySearchCondition("ddw_goods_product",search);
        GoodsInfoProductVO vo=null;
        for(Map m:list){
            vo=new GoodsInfoProductVO();
            PropertyUtils.copyProperties(vo,m);
            gvo.getPruduct().add(vo);

        }
        return new ResponseApiVO(1,"成功",gvo);
    }
}
