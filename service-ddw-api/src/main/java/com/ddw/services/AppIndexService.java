package com.ddw.services;

import com.ddw.beans.AppIndexDTO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.controller.AppIndexController;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AppIndexService {
    private final Logger logger = Logger.getLogger(AppIndexService.class);

    public ResponseApiVO toIndex(String token,AppIndexDTO dto){
        TokenUtil.putStoreid(token,dto.getStoreId());
        return new ResponseApiVO(1,"获取成功",new AppIndexVO());
    }

}
