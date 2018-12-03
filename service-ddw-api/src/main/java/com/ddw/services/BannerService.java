package com.ddw.services;

import com.ddw.beans.vo.AppIndexBannerVO;
import com.ddw.enums.BannerTypeEnum;
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
public class BannerService extends CommonService {

    public List<AppIndexBannerVO> getBannerList(Integer storeId, BannerTypeEnum type)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        if(storeId!=null){
            searchCondition.put("storeId",storeId);
        }else{
            searchCondition.put("status",1);

        }
        searchCondition.put("enable",1);
        searchCondition.put("bType",type.getCode());

        List<Map> list = this.commonList("ddw_banner","createTime desc",1,10,searchCondition);
        List<AppIndexBannerVO> appIndexBannerList = new ArrayList<>();
        if (list != null) {
            for(Map map:list){
                AppIndexBannerVO bannerVO = new AppIndexBannerVO();
                PropertyUtils.copyProperties(bannerVO,map);
                appIndexBannerList.add(bannerVO);
            }
        }
        return appIndexBannerList;
    }
}
