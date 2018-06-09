package com.ddw.services;

import com.ddw.beans.GiftVO;
import com.ddw.beans.ListVO;
import com.ddw.beans.ResponseApiVO;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GiftService  extends CommonService{


    public ResponseApiVO getAllGift()throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("dgDisabled",0);
        //@Cacheable(value="publicCache",key="'allGift'")
        List cacheList=(List)CacheUtil.get("publicCache","allGift");
        if(cacheList!=null){
            return new ResponseApiVO(1,"成功",new ListVO(cacheList));
        }
        CommonSearchBean csb=new CommonSearchBean("ddw_gift","dgSort asc,updateTime desc","t1.id code,t1.dgName name,t1.dgPrice price,t1.dgActPrice actPrice,t1.dgImgPath imgUrl",null,null,searchMap);
        List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && !list.isEmpty()){

            CacheUtil.put("publicCache","allGift",list);
            return new ResponseApiVO(1,"成功",new ListVO(list));

        }
        return new ResponseApiVO(-2,"失败",new ListVO(new ArrayList()));

    }
}
