package com.ddw.services;

import com.ddw.enums.DisabledEnum;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 逗币卷
 */
@Service
@Transactional(readOnly = true)
public class DoubiClientService extends CommonService {



    public Map getObject(Integer id){
        List<Map> doubiList=(List)CacheUtil.get("publicCache","doubi-all");
        if(doubiList==null){
            doubiList=getList();
        }
        doubiList=doubiList.stream().filter(a->id.equals(a.get("id"))).collect(Collectors.toList());
        if(doubiList==null){
            return null;
        }
        Map doubi=doubiList.get(0);
        return doubi;
    }
    @Cacheable(value ="publicCache",key="'doubi-all'" )
    public List getList(){
        Map map=new HashMap<>();
        map.put("drDisabled", DisabledEnum.disabled0.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_doubi","drSort asc","t1.id,t1.drName 'name',t1.drDesc 'desc',t1.drDiscount discount,t1.drCost price,t1.drDoubiNum doubiNum",null,null,null);
        return  this.getCommonMapper().selectObjects(csb);
    }



}
