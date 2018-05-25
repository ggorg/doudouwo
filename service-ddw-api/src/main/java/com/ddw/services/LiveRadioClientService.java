package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.GoddessFlagEnum;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.LiveRadioApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveRadioClientService  extends CommonService{
    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private ReviewGoddessService reviewGoddessService;


    public ResponseApiVO toLiveRadio(String token)throws Exception{
        Integer storeId=TokenUtil.getStoreId(token);
        String userOpenId=(String)TokenUtil.getUserObject(token);
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
            TokenUtil.putStreamId(token,liveRadioPO.getStreamid());

            return new ResponseApiVO(1,"成功",liveRadioPushVO);
        }else{
            return new ResponseApiVO(-2004,"请向管理员申请开通直播",null);

        }

    }
    public ResponseApiVO getLiveRadioListByStore(Integer pageNo,Integer storeId){
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        Page page=new Page(pageNo==null?1:pageNo,10);
        Map condition=new HashMap();
        condition.put("storeid",storeId);
        condition.put("liveStatus",LiveStatusEnum.liveStatus1.getCode());
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userid",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_live_radio_space",null,"t1.id code,ct0.userName,ct0.nickName,ct0.city,ct0.headImgUrl,ct0.label",page.getStartRow(),page.getEndRow(),condition,cb);
        List Map=this.getCommonMapper().selectObjects(csb);
        ListVO list=new ListVO(Map);
        return new ResponseApiVO(1,"成功",list);
    }
    public ResponseApiVO selectLiveRadio(CodeDTO dto, String token)throws Exception{
        Integer storeId=TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        if(dto==null ||dto.getCode()==null){
            return new ResponseApiVO(-2,"请选择一个直播房间",null);

        }
        LiveRadioPO po=this.liveRadioService.getLiveRadioByIdAndStoreId(dto.getCode(),storeId);
        if(po!=null){
            TokenUtil.putGroupId(token,po.getGroupId());
            SelectLiveRadioVO svo=new SelectLiveRadioVO();
            PropertyUtils.copyProperties(svo,po);
            TokenUtil.putStreamId(token,po.getStreamid());
            return new ResponseApiVO(1,"成功",svo);
        }
        return new ResponseApiVO(-2,"选择直播房间",null);



    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO closeRoom(String token)throws Exception{
        GoddessPO gpo= reviewGoddessService.getAppointment(TokenUtil.getUserId(token));
        if(gpo==null){
            return new ResponseApiVO(-2,"权限不足",null);
        }
        String streamId=TokenUtil.getStreamId(token);
        if(StringUtils.isBlank(streamId)){
            return new ResponseApiVO(-2,"操作异常",null);

        }
        boolean flag=LiveRadioApiUtil.closeLoveRadio(streamId);
        if(!flag){
            ResponseVO res=liveRadioService.handleLiveRadioStatus(streamId,0);
            if(res.getReCode()==1){
                return new ResponseApiVO(1,"成功",null);
            }
        }else{
            return new ResponseApiVO(1,"成功",null);

        }
        CacheUtil.delete("publicCache","closeCmd-"+streamId);
        return new ResponseApiVO(-2,"失败",null);


    }
}
