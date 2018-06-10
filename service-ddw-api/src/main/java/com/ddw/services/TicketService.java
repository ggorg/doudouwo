package com.ddw.services;

import com.ddw.beans.AppIndexTicketVO;
import com.ddw.beans.ListVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.TicketTypeEnum;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TicketService extends CommonService{


    public ResponseApiVO getAllTicket()throws Exception{

        List list=getTicketList();
        if(list!=null){
            return new ResponseApiVO(1,"成功",new ListVO(list));

        }
        return new ResponseApiVO(-2,"失败",new ListVO(new ArrayList()));
    }
    public List<AppIndexTicketVO> getTicketList()throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("dtDisabled",0);
        //@Cacheable(value="publicCache",key="'allGift'")
        List<AppIndexTicketVO> cacheList=(List<AppIndexTicketVO>)CacheUtil.get("publicCache","allTicket");
        if(cacheList!=null){
            return cacheList;
        }
        CommonSearchBean csb=new CommonSearchBean("ddw_ticket","updateTime desc","t1.id code,t1.dtName name,t1.dtPrice price,t1.dtActPrice actPrice,t1.dtDesc 'desc',t1.dtType type,t1.dtActiveTime activeTime",null,null,searchMap);
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        List<AppIndexTicketVO> appIndexTicketList = new ArrayList<AppIndexTicketVO>();
        if(list!=null && !list.isEmpty()){
//            list.forEach(a->a.put("typeName",TicketTypeEnum.getName((Integer) a.get("type"))));
            for(Map map:list){
                AppIndexTicketVO at = new AppIndexTicketVO();
                PropertyUtils.copyProperties(at,map);
                at.setTypeName(TicketTypeEnum.getName(at.getType()));
                appIndexTicketList.add(at);
            }
            CacheUtil.put("publicCache","allTicket",appIndexTicketList);
            return appIndexTicketList;
        }
        return null;

    }
}
