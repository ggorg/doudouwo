package com.ddw.services;

import com.ddw.beans.vo.AppIndexButtonVO;
import com.gen.common.services.CommonService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacky on 2018/6/16.
 */
@Service
@Transactional(readOnly = true)
public class ButtonService extends CommonService {

    public List<AppIndexButtonVO> getButtonList()throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        List<Map> list = this.commonList("ddw_button","sort",1,99,searchCondition);
        List<AppIndexButtonVO> appIndexButtonList = new ArrayList<>();
        if (list != null) {
            for(Map map:list){
                AppIndexButtonVO buttonVO = new AppIndexButtonVO();
                PropertyUtils.copyProperties(buttonVO,map);
                appIndexButtonList.add(buttonVO);
            }
        }
        return appIndexButtonList;
    }
}
