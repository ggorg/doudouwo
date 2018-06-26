package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexBannerVO;
import com.ddw.enums.GoodsRecommendEnum;
import com.ddw.enums.GoodsStatusEnum;
import com.ddw.enums.GoodsTypeEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.dict.Dictionary;
import com.gen.common.dict.DictionaryUtils;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GoodsClientService extends CommonService {

    @Autowired
    private StoreGoodsTypeService storeGoodsTypeService;

    @Autowired
    private BannerService bannerService;

   public ResponseApiVO appIndex(String token)throws Exception{
       Integer storeId= TokenUtil.getStoreId(token);
       if(storeId==null){
           return new ResponseApiVO(-2,"请选择门店",null);
       }
       Map search=new HashMap();
       search.put("storeId",storeId);
       search.put("dgStatus", GoodsStatusEnum.goodsStatus1.getCode());
       List<Map> gList=this.commonList("ddw_goods","dgRecommend desc,dgSalesNumber desc,dgSort asc",null,null,search);
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
       typeMap.put("name","限时折扣");
       typeMap.put("list",goodsList);
       listData.add(typeMap);
       pos.put(-2000,goodsList);

       goodsList=new ArrayList();
       typeMap=new HashMap();
       typeMap.put("name","为你优选");
       typeMap.put("list",goodsList);
       listData.add(typeMap);
       pos.put(-3000,goodsList);

       goodsList=new ArrayList();
       typeMap=new HashMap();
       typeMap.put("name","餐饮商城");
       typeMap.put("list",goodsList);
       listData.add(typeMap);
       pos.put(-4000,goodsList);

       Integer dgRecommend=null;
       Integer actPrice=null;
       Map<Integer,Integer> discountPosMap=new HashMap();
       for(Map gmap:gList){
           vo=new GoodsItemVO();
           PropertyUtils.copyProperties(vo,gmap);
           vo.setProducts(new ArrayList());
           dgRecommend=(Integer) gmap.get("dgRecommend");
           if(GoodsRecommendEnum.goodsRecommend1.getCode().equals(dgRecommend) && pos.get(-2000).size()<5){
               pos.get(-3000).add(vo);
           }
           if(pos.get(-4000).size()<7){
               pos.get(-4000).add(vo);
           }
           for(Map p:pList){
               if(p.get("dghGoodsId").equals(vo.getId())){
                   Map pmap=new HashMap();
                   pmap.put("name",p.get("dghName"));
                   pmap.put("price",p.get("dghSalesPrice"));
                   actPrice=(Integer) p.get("dghActivityPrice");
                   pmap.put("actPrice",actPrice);
                   pmap.put("code",p.get("id"));
                   vo.getProducts().add(pmap);
                   if(actPrice!=null && actPrice>0){
                       if(discountPosMap.containsKey(vo.getId())){
                           ((GoodsItemVO)pos.get(-2000).get(discountPosMap.get(vo.getId()))).getProducts().add(pmap);
                       }else{

                           GoodsItemVO discountGI=new GoodsItemVO();
                           PropertyUtils.copyProperties(discountGI,vo);
                           discountGI.setProducts(new ArrayList());
                           discountGI.getProducts().add(pmap);

                           pos.get(-2000).add(discountGI);
                           discountPosMap.put(vo.getId(),discountGI.getProducts().size()-1);
                       }
                   }
               }
           }
       }

       Map goddessIndex=new HashMap();
       goddessIndex.put("list",listData);

       Object obBanner = CacheUtil.get("publicCache","appIndexBanner"+storeId);
       if(obBanner==null){
           obBanner = bannerService.getBannerList(storeId);
           CacheUtil.put("publicCache","appIndexButton"+storeId,obBanner);
       }
       goddessIndex.put("bannerList",obBanner);
       return new ResponseApiVO(1,"成功",goddessIndex);

   }


    public ResponseApiVO goodsIndex(String token)throws Exception{
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
        typeMap.put("name","限时折扣");
        typeMap.put("list",goodsList);
        listData.add(typeMap);
        pos.put(-2000,goodsList);
        goodsList=new ArrayList();
        typeMap=new HashMap();
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
        Map<Integer,Integer> discountPosMap=new HashMap();
        Integer gid=null;
        Integer actPrice=null;
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
                            actPrice=(Integer) p.get("dghActivityPrice");
                            pmap.put("actPrice",actPrice);
                            pmap.put("code",p.get("id"));
                            vo.getProducts().add(pmap);
                            if(actPrice!=null && actPrice>0){
                                if(discountPosMap.containsKey(gid)){
                                    ((GoodsItemVO)pos.get(-2000).get(discountPosMap.get(gid))).getProducts().add(pmap);
                                }else{

                                    GoodsItemVO discountGI=new GoodsItemVO();
                                    PropertyUtils.copyProperties(discountGI,vo);
                                    discountGI.setProducts(new ArrayList());
                                    discountGI.getProducts().add(pmap);

                                    pos.get(-2000).add(discountGI);
                                    discountPosMap.put(gid,discountGI.getProducts().size()-1);
                                }
                            }

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
