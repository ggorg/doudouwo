package com.ddw.services;

import com.ddw.enums.DynamicsRoleTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BasePhotoService  extends CommonService{

    public String getPhotograph(Integer userId) {
        String imgUrl = (String) CacheUtil.get("commonCache", "goddess-photo-" + userId);
        if (StringUtils.isBlank(imgUrl)) {
            Map searchMap = new HashMap();
            searchMap.put("userId", userId);
            List<Map> list = this.commonList("ddw_photograph", "createTime desc", 1, 1, searchMap);
            if (list != null && !list.isEmpty()) {
                String imgUrlVo = (String) list.get(0).get("imgUrl");
                CacheUtil.put("commonCache", "goddess-photo-" + userId, imgUrlVo);
                return imgUrlVo;
            }
        }else{
            return imgUrl;
        }
        return "";

    }

}
