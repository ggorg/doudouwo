package com.ddw.services;

import com.ddw.beans.StoreDTO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppIndexService {
    private final Logger logger = Logger.getLogger(AppIndexService.class);
    @Autowired
    private TicketService ticketService;


    public ResponseApiVO toIndex(String token){
        List<Map> obj=(List)CacheUtil.get("stores","store");
        if(obj==null || obj.isEmpty()){
            return new ResponseApiVO(-2,"请先加载门店列表",null);
        }
        Integer storeId=TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }

        Map map=new HashMap();
        map.put("ticketList",ticketService.getTicketList());
        return new ResponseApiVO(1,"成功",map);

    }

}
