package com.ddw.services;

import com.alibaba.druid.sql.PagerUtils;
import com.ddw.beans.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.Distance;
import com.ddw.util.LanglatComparator;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class AppStoresService extends CommonService {

    public ResponseApiVO chooseStore(String token,StoreDTO dto){
        Integer id=null;
        List<Map> obj=getStoreList();
        for(Map m:obj){
            id=(Integer) m.get("id");
            if(id.equals(dto.getStoreId())){
                TokenUtil.putStoreid(token,dto.getStoreId());
                //TokenUtil.putStoreLongLat(token,m.get("dsLongitude")+","+m.get("dsLatitude"));
                return new ResponseApiVO(1,"成功",null);
            }
        }
        return new ResponseApiVO(-2,"失败",null);

    }
    public List getStoreList(){
        List<Map> obj=(List)CacheUtil.get("stores","store");
        if(obj==null){
            obj= this.commonList("ddw_store","dsSort asc",null,null,null);
            CacheUtil.put("stores","store",obj);
        }
        return obj;
    }
    public ResponseApiVO showNearby( AppStoresShowNearbyDTO dto)throws Exception{


        if(StringUtils.isBlank(dto.getLanglat()) || StringUtils.isBlank(dto.getDsCity())){
           // search.put("dsCity","成都");
            dto.setDsCity("成都");
        }
        List<Map> obj=getStoreList();
        Page p=new Page(dto.getPageNo(),10);

        List filterList=obj.stream().filter(m->((String)m.get("dsCity")).equals(dto.getDsCity())).collect(Collectors.toList());
        if(StringUtils.isNotBlank(dto.getLanglat())){
           String[] lls= dto.getLanglat().split(",");
            if(lls.length!=2 || !dto.getLanglat().matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
                return new ResponseApiVO(-2,"坐标格式有误",null);

            }
            Collections.sort(filterList,new LanglatComparator(dto.getLanglat()));
        }

        int size=filterList.size();
        List<Map> data=null;
        if(p.getStartRow()<size){
            int endN=p.getStartRow()+p.getEndRow();
            data=filterList.subList(p.getStartRow(),endN>size?size:endN);
            AppStoresShowNearbyVO vo=null;
            List objData=new ArrayList();

            for(Map m:data){
                vo=new AppStoresShowNearbyVO();
                if(data.size()==1  && StringUtils.isNotBlank(dto.getLanglat())){
                    String[] strs=dto.getLanglat().split(",");
                    double longN1=Double.parseDouble((String)m.get("dsLongitude"));
                    double latN1=Double.parseDouble((String)m.get("dsLatitude"));
                    vo.setDistance(Distance.getDistance(Double.parseDouble(strs[0]),Double.parseDouble(strs[1]),longN1,latN1)+"km");
                }else{
                    vo.setDistance((String)m.get("distance"));

                }
                vo.setDsName((String)m.get("dsName"));
                vo.setId((Integer) m.get("id"));
                vo.setImgUrl((String) m.get("dsHeadUrl"));
                objData.add(vo);
            }

            data.clear();
            filterList.clear();
            return new ResponseApiVO(1,"成功",new ListVO(objData));
        }
    return new ResponseApiVO(1,"成功",new ListVO(new ArrayList()));
    }
}
