package com.ddw.services;

import com.ddw.beans.GiftVO;
import com.ddw.beans.ListVO;
import com.ddw.beans.ResponseApiVO;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GiftService  extends CommonService{


    public ResponseApiVO getAllGift()throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("dgDisabled",0);
        //@Cacheable(value="publicCache",key="'allGift'")
        List cacheList=(List)CacheUtil.get("publicCache","allGift");
        if(cacheList!=null){
            return new ResponseApiVO(1,"成功",new ListVO(cacheList));
        }
        List list=this.commonList("ddw_gift","dgSort asc,updateTime desc",null,null,searchMap);
        if(list!=null && !list.isEmpty()){
            List newList=new ArrayList();
            GiftVO vo=null;
            for(Object o:list){
                vo=new GiftVO();
                PropertyUtils.copyProperties(vo,o);
                newList.add(vo);
            }
            CacheUtil.put("publicCache","allGift",newList);
            return new ResponseApiVO(1,"成功",new ListVO(newList));

        }
        return new ResponseApiVO(-2,"失败",new ListVO(new ArrayList()));

    }
}
