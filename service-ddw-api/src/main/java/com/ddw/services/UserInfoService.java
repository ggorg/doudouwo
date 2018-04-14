package com.ddw.services;

import com.ddw.beans.UserInfoDTO;
import com.ddw.beans.UserInfoPO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 会员
 * Created by Jacky on 2018/4/12.
 */
@Service
public class UserInfoService extends CommonService {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(UserInfoDTO userInfoDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        PropertyUtils.copyProperties(userInfoPO,userInfoDTO);
        userInfoPO.setCreateTime(new Date());
        userInfoPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_userinfo",userInfoPO);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(UserInfoDTO userInfoDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        PropertyUtils.copyProperties(userInfoPO,userInfoDTO);
        userInfoPO.setUpdateTime(new Date());
        Map updatePoMap= BeanToMapUtil.beanToMap(userInfoPO);
        return this.commonUpdateBySingleSearchParam("ddw_userinfo",updatePoMap,"id",userInfoDTO.getId());
    }

    public Map query(String username)throws Exception{
        return this.commonObjectBySingleParam("ddw_userinfo","userName",username);
    }

    public Map queryByOpenid(String openid)throws Exception{
        return this.commonObjectBySingleParam("ddw_userinfo","userName",openid);
    }
}
