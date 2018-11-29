package com.weixin.services;

import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import com.weixin.entity.UserInfoDTO;
import com.weixin.entity.UserInfoPO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员
 * Created by Jacky on 2018/4/12.
 */
@Service
@Transactional(readOnly = true)
public class UserInfoService extends CommonService {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(UserInfoDTO userInfoDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        PropertyUtils.copyProperties(userInfoPO,userInfoDTO);
        userInfoPO.setId(null);
        userInfoPO.setGradeId(1);
        userInfoPO.setUserName(userInfoDTO.getNickName());
        userInfoPO.setGoddessGradeId(1);
        userInfoPO.setPracticeGradeId(1);
        userInfoPO.setGoddessFlag(0);
        userInfoPO.setPracticeFlag(0);
        userInfoPO.setCreateTime(new Date());
        userInfoPO.setUpdateTime(new Date());
        ResponseVO re=this.commonInsert("ddw_userinfo",userInfoPO);
        if(re.getReCode()==1){
            this.createWallet((Integer) re.getData());
        }
        return re;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO createWallet(Integer userid){
        Map wallet=new HashMap();
        wallet.put("userId",userid);
        wallet.put("money",0);
        wallet.put("coin",0);
        wallet.put("version",1);
        wallet.put("goddessIncome",0);
        wallet.put("practiceIncome",0);
        wallet.put("createTime",new Date());
        wallet.put("updateTime",new Date());
        return this.commonInsertMap("ddw_my_wallet",wallet);
    }

}
