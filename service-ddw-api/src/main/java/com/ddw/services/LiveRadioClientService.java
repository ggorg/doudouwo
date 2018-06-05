package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.GoddessFlagEnum;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.Distance;
import com.ddw.util.LanglatComparator;
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

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        Map condition=new HashMap();
        condition.put("storeid",storeId);
        condition.put("liveStatus",LiveStatusEnum.liveStatus1.getCode());
        condition.put("endDate,>=",new Date());
        condition.put("userid,!=",TokenUtil.getUserId(token));
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userid",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_live_radio_space",null,"t1.id ,ct0.userName,ct0.nickName,ct0.headImgUrl,ct0.label",page.getStartRow(),page.getEndRow(),condition,cb);
        List lists=this.getCommonMapper().selectObjects(csb);
        LiveRadioListVO vo=null;
        List newList=new ArrayList();
        for(Object o:lists){
            vo=new LiveRadioListVO();
            PropertyUtils.copyProperties(vo,o);
            vo.setCity(city);
            vo.setAge("20岁");
            vo.setDistance(Distance.getDistance(longitude,latitude,Double.parseDouble(lls[0]),Double.parseDouble(lls[1]))+"km");
            newList.add(vo);
        }
        lists.clear();
        ListVO list=new ListVO(newList);
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
            return new ResponseApiVO(1,"成功",svo);
        }
        return new ResponseApiVO(-2,"选择直播房间",null);



    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO closeRoom(String token)throws Exception{
        GoddessPO gpo= reviewGoddessService.getAppointment(TokenUtil.getStoreId(token),TokenUtil.getUserId(token));
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
