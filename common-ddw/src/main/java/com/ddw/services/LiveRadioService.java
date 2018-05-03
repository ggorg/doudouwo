package com.ddw.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.LiveRadioPO;
import com.ddw.beans.LiveRadioUrlBean;
import com.ddw.beans.ReviewPO;
import com.ddw.beans.UserInfoPO;
import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.util.IMApiUtil;
import com.ddw.util.LiveRadioApiUtil;
import com.ddw.util.LiveRadioConstant;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.tls.sigcheck.tls_sigcheck;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class LiveRadioService extends CommonService{
    public Page findPage(Integer pageNo,Integer storeid)throws Exception{

        Map condtion=new HashMap();
        if(storeid!=null){
            condtion.put("storeid",storeid);
        }
        return this.commonPage("ddw_live_radio_space","updateTime desc",pageNo,10,condtion);
    }
    public LiveRadioPO getLiveRadio(Integer userid,Integer storeid)throws Exception{

        Map condtion=new HashMap();
        condtion.put("userid",userid);
       // condtion.put("liveStatus", LiveStatusEnum.liveStatus0.getCode());
        condtion.put("endDate,>=", new Date());
       return  this.commonObjectBySearchCondition("ddw_live_radio_space",condtion, LiveRadioPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateLiveRadioStatus(String streamId,LiveStatusEnum liveStatusEnum)throws Exception{
        Map param=new HashMap();
        param.put("liveStatus",liveStatusEnum.getCode());
        Map condition=new HashMap();
        condition.put("streamid",streamId);
        return this.commonOptimisticLockUpdateByByParam("ddw_live_radio_space",param,condition,"version");

    }
    public ResponseVO handleLiveRadioStatus(String streamId,Integer eventType)throws Exception{
        if(StringUtils.isNotBlank(streamId) && eventType!=null && StringUtils.isNotBlank(LiveEventTypeEnum.getName(eventType))){
            if(LiveEventTypeEnum.eventType0.getCode().equals(eventType)){
                ResponseVO res=this.updateLiveRadioStatus(streamId,LiveStatusEnum.liveStatus2);
                if(res.getReCode()==1){
                    boolean flag=IMApiUtil.destoryGroup(streamId.replace(LiveRadioConstant.BIZID+"_",""));
                    if(flag){
                        return new ResponseVO(1,"关闭直播成功",null);
                    }else{
                        return new ResponseVO(-2,"关闭直播失败",null);
                    }
                }
            }else if(LiveEventTypeEnum.eventType1.getCode().equals(eventType)){
                return this.updateLiveRadioStatus(streamId,LiveStatusEnum.liveStatus1);
            }
        }
        return new ResponseVO(-2,"更新直播状态失败",null);
    }
    /*public ResponseVO addPersonNum(String groupId){
        this.ad
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class);
    public ResponseVO updatePersonNum(String groupId){

    }*/
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO createLiveRadioRoom(String liveRadioBusinessCode,Integer storeId)throws Exception{
        Date date= new Date();
        Date endDate=DateUtils.addHours(date,12);
       // String txTime=Long.toHexString(endDate.getTime()/1000).toUpperCase();
        ReviewPO reviewPO=this.commonObjectBySingleParam("ddw_review","drBusinessCode",liveRadioBusinessCode, ReviewPO.class);
        if(reviewPO==null){
            return new ResponseVO(-2,"创建直播间失败",null);
        }
        if(!ReviewStatusEnum.ReviewStatus1.getCode().equals(reviewPO.getDrReviewStatus())){
            return new ResponseVO(-2,"没有审批通过",null);
        }
        String streamIdExt=storeId+"_"+reviewPO.getDrProposer()+"_"+ DateFormatUtils.format(date,"yyMMddHHmmss");
        //创建推流拉流地址
        LiveRadioUrlBean liveRadioUrlBean= LiveRadioApiUtil.createLiveUrl(streamIdExt,endDate);


        UserInfoPO upo=this.commonObjectBySingleParam("ddw_userinfo","id",reviewPO.getDrProposer(), UserInfoPO.class);
        String spaceName=StringUtils.isBlank(upo.getNickName())?upo.getUserName():upo.getNickName()+"直播间";
        String callBack=IMApiUtil.createGroup(reviewPO.getDrProposer().toString().toString(),streamIdExt,spaceName);
        JSONObject jsonObject=JSON.parseObject(callBack);
        Integer errorCode=jsonObject.getInteger("ErrorCode");
        String groupId=null;
        if(errorCode.equals(0) || errorCode==0){
            groupId=jsonObject.getString("GroupId");

        }else{
            return new ResponseVO(-2,"创建直播间失败",null);
        }
        LiveRadioPO liveRadioPO=new LiveRadioPO();
        PropertyUtils.copyProperties(liveRadioPO,liveRadioUrlBean);
        liveRadioPO.setCreateTime(new Date());
        liveRadioPO.setUpdateTime(new Date());
        liveRadioPO.setEndDate(endDate);
        liveRadioPO.setLiveStatus(0);
        liveRadioPO.setSpaceName(spaceName);
        liveRadioPO.setGroupId(groupId);
        liveRadioPO.setStoreid(storeId);
        liveRadioPO.setUserid(reviewPO.getDrProposer());
        liveRadioPO.setVersion(1);
        liveRadioPO.setBusinessCode(liveRadioBusinessCode);
        liveRadioPO.setUserName(StringUtils.isBlank(upo.getNickName())?upo.getUserName():upo.getNickName());
        ResponseVO res=this.commonInsertMap("ddw_live_radio_space",BeanToMapUtil.beanToMap(liveRadioPO));
        if(res.getReCode()==1){
            res.setReMsg("创建直播成功");
        }else{
            res.setReMsg("创建直播失败");

        }
        return res;
    }


    public static void main(String[] args) {
        System.out.println(Long.toHexString(Long.parseLong(DateFormatUtils.format(new Date(),"yyMMddHHmmss"))));
    }
}
