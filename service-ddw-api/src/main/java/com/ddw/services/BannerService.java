package com.ddw.services;

import com.ddw.beans.AppIndexBannerVO;
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

    public List<AppIndexBannerVO> getBannerList(Integer storeId)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("storeId",storeId);
        searchCondition.put("status",1);
        List<Map> list = this.commonObjectsBySearchCondition("ddw_review_banner",searchCondition);
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
