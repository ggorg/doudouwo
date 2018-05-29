package com.ddw.services;

import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 充值卷
 */
@Service
public class RechargeService extends CommonService {
    @Autowired
    private CacheService cacheService;

    @Cacheable(value ="commonCache",key="'recharge-'+#key" )
    public Integer getRechargeCost(Integer id){

        return this.commonSingleFieldBySingleSearchParam("ddw_recharge","id",id,"drCost",Integer.class);
    }
    @Cacheable(value ="commonCache",key="'recharge-all-'+#key" )
    public List getRechargeList(){
        CommonSearchBean csb=new CommonSearchBean("ddw_recharge","drSort asc","t1.id,t1.drName 'name',t1.drDesc 'desc'",null,null,null);
        return  this.getCommonMapper().selectObjects(csb);
    }
}
