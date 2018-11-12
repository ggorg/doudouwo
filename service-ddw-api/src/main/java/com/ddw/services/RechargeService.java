package com.ddw.services;

import com.ddw.beans.RechargeVO;
import com.ddw.enums.DisabledEnum;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 充值卷
 */
@Service
@Transactional(readOnly = true)
public class RechargeService extends CommonService {


    public Map getRechargeCost(Integer id){
        List<Map> rechargeList=(List) CacheUtil.get("publicCache","recharge-all");
        if(rechargeList==null){
            rechargeList=getRechargeList();
        }
        rechargeList=rechargeList.stream().filter(a->id.equals(a.get("id"))).collect(Collectors.toList());
        if(rechargeList==null){
            return null;
        }
        Map map=rechargeList.get(0);
       Integer discount=(Integer) map.get("discount");
       Integer price=(Integer) map.get("price");
       Map costMap=new HashMap();
       costMap.put("discount",discount==null?price:discount);
       costMap.put("price",price);
        map.clear();map=null;
        return costMap;
    }
    @Cacheable(value ="publicCache",key="'recharge-all'" )
    public List getRechargeList(){
        Map map=new HashMap<>();
        map.put("drDisabled", DisabledEnum.disabled0.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_recharge","drSort asc","t1.id,t1.drName 'name',t1.drDesc 'desc',t1.drDiscount discount,t1.drCost price",null,null,null);
        return  this.getCommonMapper().selectObjects(csb);
    }

}
