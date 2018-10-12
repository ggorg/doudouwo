package com.ddw.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.LiveRadioListVO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.util.IMApiUtil;
import com.ddw.util.IndexGoddessComparator;
import com.ddw.util.LiveRadioApiUtil;
import com.ddw.util.LiveRadioConstant;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class LiveRadioService extends CommonService{
    @Autowired
    private DDWGlobals ddwGlobals;

    @Autowired
    private BasePhotoService basePhotoService;

    @Autowired
    private BaseDynService baseDynService;

    public Page findPage(Integer pageNo,Integer storeid)throws Exception{

        Map condtion=new HashMap();
        if(storeid!=null){
            condtion.put("storeid",storeid);
        }
        return this.commonPage("ddw_live_radio_space","updateTime desc",pageNo,10,condtion);
    }

    public List getAllActLiveRadio(){
        Map condtion=new HashMap();
        condtion.put("liveStatus,<",LiveStatusEnum.liveStatus2.getCode());
        return this.commonList("ddw_live_radio_space",null,null,null,condtion);
    }

    public LiveRadioPO getLiveRadioByGroupId(String groupId)throws Exception{
        return this.commonObjectBySingleParam("ddw_live_radio_space","groupId",groupId,LiveRadioPO.class);
    }
    public LiveRadioPO getLiveRadio(Integer userid,Integer storeid)throws Exception{

        Map condtion=new HashMap();
        condtion.put("userid",userid);
       // condtion.put("liveStatus", LiveStatusEnum.liveStatus0.getCode());
        condtion.put("endDate,>=", new Date());
        List list=this.commonList("ddw_live_radio_space","createTime desc",1,1,condtion);
        if(list!=null && !list.isEmpty()){
            LiveRadioPO liveRadioPO=new LiveRadioPO();
            PropertyUtils.copyProperties(liveRadioPO,list.get(0));
            return liveRadioPO;
        }
        return null;
    }

    public LiveRadioPO getLiveRadioByIdAndStoreId(Integer id,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("id",id);
        condtion.put("liveStatus", LiveStatusEnum.liveStatus1.getCode());
        condtion.put("endDate,>=", new Date());
        condtion.put("storeid", storeId);
        return  this.commonObjectBySearchCondition("ddw_live_radio_space",condtion, LiveRadioPO.class);
    }
    public Map getLiveRadioGodessInfoByIdAndStoreId(Integer id,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("id",id);
        condtion.put("liveStatus", LiveStatusEnum.liveStatus1.getCode());
        condtion.put("endDate,>=", new Date());
        condtion.put("storeid", storeId);

        CommonSearchBean csb=new CommonSearchBean("ddw_live_radio_space",null,"t1.userid,t1.pullUrl,t1.groupId,ct0.nickName,ct0.headImgUrl,ct0.openid openId",null,null,condtion,
        new CommonChildBean("ddw_userinfo","id","userid",null)
        );
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateLiveRadioStatus(String streamId,LiveStatusEnum liveStatusEnum)throws Exception{
        this.updateAppIndexCache(streamId,liveStatusEnum.getCode());
        Map param=new HashMap();
        param.put("liveStatus",liveStatusEnum.getCode());
        Map condition=new HashMap();
        condition.put("streamid",streamId);
        return this.commonOptimisticLockUpdateByParam("ddw_live_radio_space",param,condition,"version");

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO handleLiveRadioStatus(String streamId,Integer eventType)throws Exception{
        if(StringUtils.isNotBlank(streamId) && eventType!=null && StringUtils.isNotBlank(LiveEventTypeEnum.getName(eventType))){
            if(LiveEventTypeEnum.eventType0.getCode().equals(eventType)){
                ResponseVO res=this.updateLiveRadioStatus(streamId,LiveStatusEnum.liveStatus2);
                if(res.getReCode()==1){
                    IMApiUtil.setDdwGlobals(ddwGlobals);
                    boolean flag=IMApiUtil.destoryGroup(streamId.replace(LiveRadioConstant.BIZID+"_",""));
                    if(flag){
                        CacheUtil.delete("publicCache","livePv-"+streamId.replaceFirst("[0-9]+_",""));
                        baseDynService.doEndTime(streamId,DynamicsRoleTypeEnum.RoleType1);
                        return new ResponseVO(1,"关闭直播成功",null);
                    }else{
                        return new ResponseVO(-2,"关闭直播失败",null);
                    }
                }
            }else if(LiveEventTypeEnum.eventType1.getCode().equals(eventType)){
                baseDynService.saveGoddessLiveDyn(streamId);
                return this.updateLiveRadioStatus(streamId,LiveStatusEnum.liveStatus1);
            }
        }
        return new ResponseVO(-2,"更新直播状态失败",null);
    }



    //更新缓存首页女神直播状态
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void updateAppIndexCache(String streamId,Integer flag)throws Exception{
        if(LiveEventTypeEnum.eventType1.getCode().equals(flag)){
            CacheUtil.delete("publicCache","appIndexGoddess");
            return ;
        }
        String[] str = streamId.split("_");
        String storeId = str[1];
        Integer userId = Integer.parseInt(str[2]);
        List<LiveRadioListVO> appIndexGoddessList= (List<LiveRadioListVO>)CacheUtil.get("publicCache","appIndexGoddess");
        if(appIndexGoddessList != null){

            ListIterator<LiveRadioListVO> appIndexGoddessIterator = appIndexGoddessList.listIterator();
            while (appIndexGoddessIterator.hasNext()){
                LiveRadioListVO appIndexGoddessVO = appIndexGoddessIterator.next();
                if(appIndexGoddessVO.getUserId() == userId || userId.equals(appIndexGoddessVO.getUserId())){
                    String groupId=streamId.replaceFirst("[0-9]+_","");
                    List groupIds=Arrays.asList(groupId);
                    Integer pv=(Integer) CacheUtil.get("publicCache","livePv-"+groupId);
                    Map map=IMApiUtil.getMemberNum(groupIds);
                    Map search=new HashMap();
                    search.put("groupId",groupId);
                    Map param=new HashMap();
                    param.put("pv",pv==null?0:pv);
                    if(map.containsKey(groupId)){
                        param.put("maxGroupNum",map.get(groupId));
                    }
                    this.commonOptimisticLockUpdateByParam("ddw_live_radio_space",param,search,"version");
                    CacheUtil.delete("publicCache","appIndexGoddess");

                }
            }

           // appIndexGoddessList.
            //Collections.sort(appIndexGoddessList,new IndexGoddessComparator());

        }
    }
    /*public ResponseVO addPersonNum(String groupId){
        this.ad
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class);
    public ResponseVO updatePersonNum(String groupId){

    }*/
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO createLiveRadioRoom(String liveRadioBusinessCode,Integer storeId,ReviewPO reviewPO)throws Exception{
        Date date= new Date();
        Date endDate=DateUtils.addHours(date,12);
       // String txTime=Long.toHexString(endDate.getTime()/1000).toUpperCase();
        if(reviewPO==null){
            reviewPO=this.commonObjectBySingleParam("ddw_review","drBusinessCode",liveRadioBusinessCode, ReviewPO.class);

        }
        if(reviewPO==null){
            return new ResponseVO(-2,"创建直播间失败",null);
        }
        if(!ReviewStatusEnum.ReviewStatus1.getCode().equals(reviewPO.getDrReviewStatus())){
            return new ResponseVO(-2,"没有审批通过",null);
        }
        //直播审核中
        CacheUtil.put("review","liveRadio"+reviewPO.getDrProposer(),1);
        String streamIdExt=storeId+"_"+reviewPO.getDrProposer()+"_"+ DateFormatUtils.format(date,"yyMMddHHmmss");
        //创建推流拉流地址
        LiveRadioUrlBean liveRadioUrlBean= LiveRadioApiUtil.createLiveUrl(streamIdExt,endDate);


        UserInfoPO upo=this.commonObjectBySingleParam("ddw_userinfo","id",reviewPO.getDrProposer(), UserInfoPO.class);
        String spaceName=null;
        if(StringUtils.isNotBlank(reviewPO.getDrExtend())){
            if(reviewPO.getDrExtend().startsWith("房间名称-")){
                spaceName=reviewPO.getDrExtend().replaceFirst("房间名称-","");

            }else{
                spaceName=reviewPO.getDrExtend();

            }
        }else{
            spaceName= StringUtils.isBlank(upo.getNickName())?upo.getUserName():upo.getNickName()+"直播间";
        }
        String callBack=IMApiUtil.createGroup(reviewPO.getDrProposer().toString(),streamIdExt,spaceName);
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
        ResponseVO<Integer> res=this.commonInsertMap("ddw_live_radio_space",BeanToMapUtil.beanToMap(liveRadioPO));
        if(res.getReCode()==1){
            Map update=new HashMap();
            update.put("liveId",res.getData());
            this.commonUpdateBySingleSearchParam("ddw_goddess",update,"userId",liveRadioPO.getUserid());
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
