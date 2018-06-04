package com.ddw.services;

import com.ddw.beans.AppIndexDTO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.controller.AppIndexController;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppIndexService {
    private final Logger logger = Logger.getLogger(AppIndexService.class);

    public ResponseApiVO toIndex(String token,AppIndexDTO dto){
        List<Map> obj=(List)CacheUtil.get("stores","store");
        if(obj==null || obj.isEmpty()){
            return new ResponseApiVO(-2,"请先加载门店列表",null);
        }
        if(dto.getStoreId()==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }
        Integer id=null;
        for(Map m:obj){
            id=(Integer) m.get("id");
            if(id.equals(dto.getStoreId())){
                TokenUtil.putStoreid(token,dto.getStoreId());
                //TokenUtil.putStoreLongLat(token,m.get("dsLongitude")+","+m.get("dsLatitude"));
                return new ResponseApiVO(1,"成功",new AppIndexVO());
            }
        }

        return new ResponseApiVO(-2,"失败",new AppIndexVO());

    }

}
