package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.LiveRadioListVO;
import com.ddw.dao.GoddessMapper;
import com.ddw.enums.GoddessFlagEnum;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.Distance;
import com.ddw.util.IMApiUtil;
import com.ddw.util.LiveRadioApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LiveRadioClientService  extends CommonService{
    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private ReviewGoddessService reviewGoddessService;


    @Autowired
    private MyAttentionService myAttentionService;

    @Autowired
    private BasePhotoService basePhotoService;

    @Autowired
    private GoddessMapper goddessMapper;


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
        if(!GoddessFlagEnum.goddessFlag1.getCode().equals(upo.get("goddessFlag"))){
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
    public ResponseApiVO getLiveRadioListByStore(LiveRadioListDTO dto,Integer storeId,String token)throws Exception{
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }

        String[] lls=null;
        if(StringUtils.isBlank(dto.getLanglat())){
            return new ResponseApiVO(-2,"坐标不能为空",null);

        }else{
             lls=dto.getLanglat().split(",");
            if(lls.length!=2 || !dto.getLanglat().matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
                return new ResponseApiVO(-2,"坐标格式有误",null);

            }
        }
        List<Map> obj=(List)CacheUtil.get("stores","store");
        if(obj==null || obj.isEmpty()){
            return new ResponseApiVO(-2,"请先加载门店列表",null);
        }
        List storeObj=obj.stream().filter(m->m.get("id").equals(storeId)).collect(Collectors.toList());
        if(storeObj==null ||  storeObj.isEmpty()){
            return new ResponseApiVO(-2,"门店不存在",null);

        }
        Map store=(Map)storeObj.get(0);
        double longitude=Double.parseDouble((String)store.get("dsLongitude"));
        double latitude=Double.parseDouble((String)store.get("dsLatitude"));
        String city=(String)store.get("dsCity");
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Integer userId=TokenUtil.getUserId(token);

        List<LiveRadioListVO> lists=this.goddessMapper.liveGoddess(page.getStartRow(),page.getEndRow(),storeId,userId);
        final List groupIds=new ArrayList();
        lists.forEach(a->{
            if(LiveStatusEnum.liveStatus1.getCode().equals(a.getLiveRadioFlag())){
                groupIds.add(a.getGroupId());
            }
        });
        Map map=null;
        if(groupIds.size()>0){
            map= IMApiUtil.getMemberNum(groupIds);
        }

        for(LiveRadioListVO o:lists){
            if(map!=null && LiveStatusEnum.liveStatus1.getCode().equals(o.getLiveRadioFlag())){
                o.setViewingNum((Integer) map.get(o.getGroupId()));
            }else{
                o.setViewingNum(0);
            }
            o.setCity(city);
           // o.setAge("20岁");
            o.setDistance(Distance.getDistance(longitude,latitude,Double.parseDouble(lls[0]),Double.parseDouble(lls[1]))+"km");
            o.setBackImgUrl(basePhotoService.getPhotograph(o.getUserId()));

        }
        ListVO list=new ListVO(lists);
        return new ResponseApiVO(1,"成功",list);
    }

    public List<Map> getLiveRadioList(Integer userId,Integer pageNo)throws Exception{
        Map condition=new HashMap();
        condition.put("liveStatus",LiveStatusEnum.liveStatus1.getCode());
        condition.put("endDate,>=",new Date());
        condition.put("userid,!=",userId);
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userid",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_live_radio_space",null,"t1.id ,ct0.userName,ct0.nickName,ct0.headImgUrl,ct0.label,ct0.id userId",null,null,condition,cb);
        Page p = this.commonPage(pageNo,10,csb);
        return p.getResult();
    }

    public ResponseApiVO selectLiveRadio(CodeDTO dto, String token)throws Exception{
        Integer storeId=TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        if(dto==null ||dto.getCode()==null){
            return new ResponseApiVO(-2,"请选择一个直播房间",null);

        }
        Map po=this.liveRadioService.getLiveRadioGodessInfoByIdAndStoreId(dto.getCode(),storeId);
        if(po!=null){
            TokenUtil.putGroupId(token,(String)po.get("groupId"));
            MyAttentionDTO madto=new MyAttentionDTO();
            madto.setGoddessId((Integer) po.get("userid"));
            SelectLiveRadioVO svo=new SelectLiveRadioVO();
            PropertyUtils.copyProperties(svo,po);
            svo.setGoddessCode(madto.getGoddessId());
            MyAttentionPO mapo=this.myAttentionService.query(TokenUtil.getUserId(token),madto);
            if(mapo==null){
                svo.setAttention(0);
            }else{
                svo.setAttention(1);
            }

            handleLiveNum((String)po.get("groupId"));

            return new ResponseApiVO(1,"成功",svo);
        }
        return new ResponseApiVO(-2,"主播在赶来的路上",null);



    }

    public void handleLiveNum(String groupId)throws Exception{
        Integer pv=(Integer) CacheUtil.get("publicCache","livePv-"+groupId);
        if(pv==null){
            pv=1;
        }else{
            pv++;
        }
        Map map= IMApiUtil.getMemberNum(Arrays.asList(groupId));
        Integer userid=Integer.parseInt(groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2"));
        List<LiveRadioListVO> alist= (List<LiveRadioListVO>)CacheUtil.get("publicCache","appIndexGoddess");
        if(alist!=null){
            alist.forEach(a->{
                if(a.getUserId().equals(userid) && LiveStatusEnum.liveStatus1.getCode().equals(a.getLiveRadioFlag())){
                    a.setViewingNum((Integer) map.get(groupId));

                }
            });
        }

        CacheUtil.put("publicCache", "appIndexGoddess",alist);
        CacheUtil.put("publicCache","livePv-"+groupId,pv);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO closeRoom(String token)throws Exception{
        GoddessPO gpo= reviewGoddessService.getAppointment(TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
        if(gpo==null){
            return new ResponseApiVO(-2,"权限不足",null);
        }
        String streamId=TokenUtil.getStreamId(token);
        if(StringUtils.isBlank(streamId)){
            return new ResponseApiVO(-2,"操作异常",null);

        }
        ResponseVO res=liveRadioService.handleLiveRadioStatus(streamId,0);
        CacheUtil.put("publicCache","closeCmd-"+streamId,streamId);

        if(res.getReCode()==1){
            boolean flag=LiveRadioApiUtil.closeLoveRadio(streamId);
            if(flag){
                return new ResponseApiVO(1,"成功",null);
            }
        }else{
            return new ResponseApiVO(-2,"关闭失败",null);

        }
        return new ResponseApiVO(-2,"失败",null);


    }


}
