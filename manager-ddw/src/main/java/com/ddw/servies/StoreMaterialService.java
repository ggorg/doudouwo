package com.ddw.servies;

import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StoreMaterialService extends CommonService {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO buyInMaterial(Integer storeId,Integer mid,Integer countNum,String unit,Integer countNetWeight)throws Exception{
        //this.g
        Map map=null;
        Integer dsCountNetWeight=null;
        Integer dsCountNumber=null;
        Integer version=null;
        Map condition=new HashMap();
        condition.put("materialId",mid);
        condition.put("storeId",storeId);
        for(int i=0;i<5;i++){
            map= this.commonObjectBySearchCondition("ddw_store_material",condition);
            if(map!=null){
                dsCountNumber=(Integer) map.get("dsCountNumber");
                dsCountNetWeight=(Integer) map.get("dsCountNetWeight");
                version=(Integer) map.get("dsVersion");

                condition.put("dsVersion",version);
                map.put("dsCountNumber",dsCountNumber+countNum);
                map.put("dsCountNetWeight",dsCountNetWeight+countNetWeight);
                map.put("updateTime",new Date());
                map.put("dsUnit",unit);
                map.put("dsVersion",version+1);
                ResponseVO res=this.commonUpdateByParams("ddw_store_material",map,condition);
                if(res.getReCode()==1){
                    return res;
                }else{
                    if(i==4){
                        throw new GenException("购买的材料没法入库");
                    }
                    Thread.sleep(i * 200);
                    continue;
                }
            }else{
                map=new HashMap();
                map.put("dsCountNumber",countNum);
                map.put("dsCountNetWeight",countNetWeight);
                map.put("updateTime",new Date());
                map.put("createTime",new Date());
                map.put("dsUnit",unit);
                map.put("storeId",storeId);
                map.put("materialId",mid);
                map.put("dsVersion",1);
                return this.commonInsertMap("ddw_store_material",map);

            }
        }

        return new ResponseVO(-2,"门店材料入库失败",null);
    }

}
