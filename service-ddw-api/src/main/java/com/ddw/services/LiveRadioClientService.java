package com.ddw.services;

import com.ddw.beans.LiveRadioPO;
import com.ddw.beans.LiveRadioPushVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.GoddessFlagEnum;
import com.ddw.enums.LiveStatusEnum;
import com.gen.common.services.CommonService;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LiveRadioClientService  extends CommonService{
    @Autowired
    private LiveRadioService liveRadioService;

    public ResponseApiVO toLiveRadio(String userOpenId, Integer storeId)throws Exception{
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        Map upo=this.commonObjectBySingleParam("ddw_userinfo","openid",userOpenId);
        if(upo==null){
            return new ResponseApiVO(-2000,"用户不存在",null);
        }
        if(!GoddessFlagEnum.goddessFlag1.getCode().equals((Integer) upo.get("goddessFlag"))){
            return new ResponseApiVO(-2001,"请先申请当女神",null);
        }
        Integer userid=(Integer) upo.get("id");
        String userName=(String) upo.get("userName");
        LiveRadioPO liveRadioPO=liveRadioService.getLiveRadio(userid,storeId);
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()<= LiveStatusEnum.liveStatus1.getCode()){
            LiveRadioPushVO liveRadioPushVO=new LiveRadioPushVO();

            PropertyUtils.copyProperties(liveRadioPushVO,liveRadioPO);


            return new ResponseApiVO(1,"成功",liveRadioPushVO);
        }else{
            return new ResponseApiVO(-2004,"请向管理员申请开通直播",null);

        }

    }
}
