package com.ddw.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.GoodsPageModelStatusEnum;
import com.ddw.enums.GoodsPlatePosEnum;
import com.ddw.enums.GoodsRecommendEnum;
import com.ddw.enums.GoodsStatusEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GoodsClientService extends CommonService {

    @Autowired
    private StoreGoodsPlateService storeGoodsPlateService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private AppStoresService appStoresService;
    @Autowired
    private UserGradeService userGradeService;

   public ResponseApiVO appIndex(String token)throws Exception{

       ResponseApiVO<GoodsListVO> responseApiVO=goodsIndex(token,GoodsPlatePosEnum.GoodsPlatePos1);
       if(responseApiVO.getReCode()!=1){
           return responseApiVO;
       }
      Integer storeId=TokenUtil.getStoreId(token);

       Map goddessIndex=new HashMap();
       goddessIndex.put("list",responseApiVO.getData().getList());

       Object obBanner = CacheUtil.get("publicCache","appIndexBanner"+storeId);
       if(obBanner==null){
           obBanner = bannerService.getBannerList(storeId);
           CacheUtil.put("publicCache","appIndexButton"+storeId,obBanner);
       }
       goddessIndex.put("bannerList",obBanner);
       return new ResponseApiVO(1,"成功",goddessIndex);

   }


    public ResponseApiVO goodsIndex(String token,GoodsPlatePosEnum platePosEnum)throws Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("platePos", platePosEnum.getCode());
        search.put("status", GoodsPageModelStatusEnum.goodsStatus1.getCode());
        List<Map> pmList=this.commonList("ddw_goods_page_model","updateTime desc",1,1,search);
        if(pmList==null || pmList.isEmpty() || pmList.size()==0){
            return new ResponseApiVO(-2,"没有发布页面",null);
        }
        Map pm=pmList.get(0);

        String gids=(String)pm.get("gids");
        if(StringUtils.isBlank(gids)){
            return new ResponseApiVO(-2,"页面没有发布商品",null);
        }
        Map gsearch=new HashMap();
        gsearch.put("id,in","("+gids+")");
        gsearch.put("dgStatus",GoodsStatusEnum.goodsStatus1.getCode());

        List<Map> gList= this.commonObjectsBySearchCondition("ddw_goods",gsearch);
        if(gList==null ||gList.isEmpty() || gList.size()==0){
            return new ResponseApiVO(-2,"没有商品上架",null);
        }
        gsearch=new HashMap();
        gsearch.put("dghGoodsId,in","("+gids+")");
        gsearch.put("dghStatus",GoodsStatusEnum.goodsStatus1.getCode());
        List<Map> pList=this.commonList("ddw_goods_product","dghSalesPrice asc,dghActivityPrice asc",null,null,gsearch);
        JSONArray ja=JSON.parseArray(pm.get("jsonStr").toString());
        JSONArray gidJa=null;
        JSONObject json=null;
        Integer gid=null;
        List goodsList=null;
        //Map goodsMap=null;
        /*Integer gradeId=TokenUtil.getUseGrade(token);
        BigDecimal dicount=null;

        if(gradeId!=null){
            dicount= userGradeService.getDiscount(gradeId);
                //countPrice=dicount.divide(BigDecimal.valueOf(countPrice)).intValue()
        }*/
        GoodsItemVO itemVO=null;
        GoodsInfoProductVO productVO=null;
        for(int i=0;i<ja.size();i++){
            json=ja.getJSONObject(i);
            gidJa=json.getJSONArray("gids");
            goodsList=new ArrayList();
            for(int j=0;j<gidJa.size();j++){
                gid=gidJa.getInteger(j);
                for(Map gMap:gList){
                    if(gid.equals((Integer) gMap.get("id"))){
                        itemVO=new  GoodsItemVO();

                        itemVO.setProducts(new ArrayList());
                        PropertyUtils.copyProperties(itemVO,gMap);
                        itemVO.setMonthSales(gMap.get("dgMohthSales")==null?0:(Integer) gMap.get("dgMohthSales"));
                        for(Map p:pList){
                            if(gid.equals((Integer)p.get("dghGoodsId"))){
                                productVO=new GoodsInfoProductVO();
                                PropertyUtils.copyProperties(productVO,p);
                                /*if(dicount!=null){
                                    productVO.setVipPrice(dicount.multiply(BigDecimal.valueOf(productVO.getDghActivityPrice()!=null?productVO.getDghActivityPrice():productVO.getDghSalesPrice())).intValue());
                                }*/
                                itemVO.getProducts().add(productVO);
                            }
                        }
                        goodsList.add(itemVO);

                    }
                }
            }
            json.put("list",goodsList);
            json.remove("gids");
        }
        GoodsListVO vo=new GoodsListVO();
        if(GoodsPlatePosEnum.GoodsPlatePos2.getCode().equals(platePosEnum.getCode())){
            List<Map> obj=appStoresService.getStoreList();
            obj=obj.stream().filter(a->((Integer)a.get("id")).equals(storeId)).collect(Collectors.toList());
            Map map=obj.get(0);
            vo.setHeadUrl((String)map.get("dsHeadUrl"));
            vo.setBannerUrl((String)map.get("dsBannerUrl"));
            vo.setName((String)map.get("dsName"));
            vo.setAddress((String)map.get("dsAddress"));
            vo.setDesc((String)map.get("dsDesc"));
        }
        vo.setList(ja);

        return new ResponseApiVO(1,"成功",vo);
       /* Map search=new HashMap();
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
        List<Map> gtypeList=this.storeGoodsPlateService.getGoodsPlate(storeId);
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
        }*/
       // return new ResponseApiVO(1,"成功",new ListVO(listData));
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
        gvo.setMonthNum((Integer) map.get("dgMohthSales"));
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
