package com.ddw.services;

import com.ddw.enums.DynamicsContextTypeEnum;
import com.ddw.enums.DynamicsRoleTypeEnum;
import com.gen.common.services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BaseDynService extends CommonService {

    @Autowired
    private BasePhotoService basePhotoService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void saveGoddessLiveDyn(String streamId)throws Exception{
        String[] str = streamId.split("_");
        Integer userId = Integer.parseInt(str[2]);
        Map liveMap=this.commonObjectBySingleParam("ddw_live_radio_space","streamid",streamId);
        Map map=new HashMap();
        map.put("createTime",new Date());
        map.put("roleType", DynamicsRoleTypeEnum.RoleType1.getCode());
        map.put("dynType", DynamicsContextTypeEnum.DynamicsContextType2.getCode());
        map.put("userId",userId);
        map.put("busId",streamId);
        map.put("title","直播间-"+liveMap.get("spaceName"));
        map.put("imgs",basePhotoService.getPhotograph(userId));
        this.commonInsertMap("ddw_dynamics",map);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void saveBideDyn(Map bidMap,Date endTime)throws Exception{

        Map map=new HashMap();
        map.put("createTime",new Date());
        map.put("roleType", DynamicsRoleTypeEnum.RoleType3.getCode());
        map.put("dynType", DynamicsContextTypeEnum.DynamicsContextType2.getCode());
        map.put("userId",bidMap.get("userId"));
        map.put("busId",bidMap.get("id"));
        map.put("title","约玩获得者-"+bidMap.get("luckyDogUserName"));
        Map userMap=this.commonObjectBySingleParam("ddw_userinfo","id",bidMap.get("luckyDogUserId"));
        map.put("imgs",userMap.get("headImgUrl"));
        map.put("endTime",endTime);
        this.commonInsertMap("ddw_dynamics",map);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void doEndTime(Object busId,DynamicsRoleTypeEnum roleTypeEnum){
        Map param=new HashMap();
        param.put("endTime",new Date());
        Map search=new HashMap();
        search.put("busId",busId);
        search.put("roleType",roleTypeEnum.getCode());
        this.commonUpdateByParams("ddw_dynamics",param,search);
    }
}
