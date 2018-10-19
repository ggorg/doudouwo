package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.beans.vo.GoodFriendPlayChatCenterVO;
import com.ddw.config.DDWGlobals;
import com.ddw.dao.GoodFriendPlayMapper;
import com.ddw.enums.DisabledEnum;
import com.ddw.enums.GoodFriendPlayRoomStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class GoodFriendPlayService extends CommonService {

    @Autowired
    private GoodFriendPlayMapper goodFriendPlayMapper;

    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private FileService fileService;
    public ResponseApiVO getChatCenter(String token)throws Exception{
        Page page=new Page(1,10);
        Integer storeId= TokenUtil.getStoreId(token);
        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);
        if(gfp==null){
            return new ResponseApiVO(-2,"大聊天房还没建",null);
        }

        Map search=new HashMap();
        search.put("centerId",gfp.getId());
        search.put("disabled", DisabledEnum.disabled0.getCode());
        search.put("status",GoodFriendPlayRoomStatusEnum.status0.getCode());
        CommonChildBean ccb=new CommonChildBean("ddw_goodfriendplay_tables","id","tableCode",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room","t1.updateTime desc","t1.id code,t1.roomImgIcon,t1.name,ct0.tableNumber,t1.status,t1.peopleMaxNum",page.getStartRow(),page.getEndRow(),search,ccb);
        List onlist=this.getCommonMapper().selectObjects(csb);
        if(onlist==null || onlist.isEmpty()){
            gfp.setOnLineList(new ArrayList());
        }else{
            gfp.setOnLineList(onlist);
        }
        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getRoomList(gfp.getId(),DisabledEnum.disabled0.getCode(),null,GoodFriendPlayRoomStatusEnum.status1.getCode(),page.getStartRow(),page.getEndRow());
        if(offlist==null || offlist.isEmpty()){
            gfp.setOffLinelist(new ArrayList());
        }else{

            gfp.setOffLinelist(offlist);
        }
        return new ResponseApiVO(1,"成功",gfp);
    }
    public GoodFriendPlayChatCenterVO getChatCenterBean(Integer storeId)throws Exception{
        GoodFriendPlayChatCenterVO gfp=(GoodFriendPlayChatCenterVO)CacheUtil.get("publicCache","gfplay-chatcenter-"+storeId);
        if(gfp==null){
            gfp=this.commonObjectBySingleParam("ddw_goodfriendplay_center","storeId",storeId,GoodFriendPlayChatCenterVO.class);
            CacheUtil.put("publicCache","gfplay-chatcenter-"+storeId,gfp);
        }
        return gfp;
    }
    public ResponseApiVO getRoom(String token,CodeDTO dto)throws Exception{
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO<>(-2,"请选择聊天房间",null);
        }
        Map search=new HashMap();
        search.put("id",dto.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        Map chid1=new HashMap();
        chid1.put("disabled",DisabledEnum.disabled0.getCode());
        chid1.put("roomId",dto.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room",null,"t1.groupId,t1.id code,t1.roomImg,t1.name,t1.status,ct0.tableNumber,t1.peopleMaxNum,count(ct1.id) peopleNum",null,null,search,
                new CommonChildBean("ddw_goodfriendplay_tables","id","tableCode",null),
                new CommonChildBean("ddw_goodfriendplay_room_member","roomId","id",chid1));
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty() || !list.get(0).containsKey("code")){
            return new ResponseApiVO<>(-2,"房间不存在或者已过期",null);
        }
        return new ResponseApiVO(1,"成功",list.get(0));
    }
    public ResponseApiVO getRoomList(String token ,GoodFriendPlayRoomListDTO dto)throws Exception{
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Integer storeId= TokenUtil.getStoreId(token);
        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);

        List<GoodFriendPlayRoomListVO> offlist=this.goodFriendPlayMapper.getRoomList(gfp.getId(),DisabledEnum.disabled0.getCode(),dto.getType(),dto.getStatus(),page.getStartRow(),page.getEndRow());
        if(offlist==null || offlist.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(offlist));
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO createRoom(String token ,GoodFriendPlayCreateRoomDTO dto)throws Exception{

        Integer storeId= TokenUtil.getStoreId(token);
        if(StringUtils.isBlank(dto.getName())){
            return new ResponseApiVO(-2,"请填写主题名",null);

        }
        if(dto.getRoomImg()==null || dto.getRoomImg().isEmpty()){
            return new ResponseApiVO(-2,"请上传图片",null);

        }
        if(dto.getTableCode()==null || dto.getTableCode()<=0){
            return new ResponseApiVO(-2,"请选择桌号",null);

        }
        if(dto.getEndTime()==null){
            return new ResponseApiVO(-2,"请填写房间结束时间",null);

        }
        Map tableMap=getTableById(token,dto.getTableCode());
        if(tableMap==null || tableMap.isEmpty()){
            return new ResponseApiVO(-2,"请选择正确的桌号",null);

        }
        if(GoodFriendPlayRoomStatusEnum.status1.getCode().equals(tableMap.get("status"))){
            return new ResponseApiVO(-2,"当前桌号已被使用",null);

        }
        Map map= BeanToMapUtil.beanToMap(dto,true);
        map.put("createTime",new Date());
        map.put("updateTime",new Date());
        if(!dto.getRoomImg().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getRoomImg().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move(dto.getRoomImg(),mainGlobals.getRsDir(), dmImgName);
            map.put("roomImg",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());
            map.put("roomImgIcon",map.get("roomImg"));
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

        }

        GoodFriendPlayChatCenterVO gfp=this.getChatCenterBean(storeId);
        map.put("centerId",gfp.getId());
        map.put("status",GoodFriendPlayRoomStatusEnum.status0.getCode());
        map.put("roomOwner",TokenUtil.getUserId(token));
        map.put("groupId",IMApiUtil.createGroup((String)TokenUtil.getUserObject(token),storeId+"_"+TokenUtil.getUserId(token)+"_"+ RandomStringUtils.randomNumeric(10),dto.getName()));
        ResponseVO vo=this.commonInsertMap("ddw_goodfriendplay_room",map);
        Map mapret=new HashMap();
        mapret.put("roomCode",vo.getData());
        mapret.put("groupId",map.get("groupId"));
        return new ResponseApiVO(1,"成功",mapret);
    }

    public Map getTableById(String token,Integer tableCode )throws  Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("id",tableCode);
        Map m=this.commonObjectBySearchCondition("ddw_goodfriendplay_tables",search);
        return m;
    }
    public ResponseApiVO getTables(String token )throws  Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        List list=this.commonObjectsBySingleParam("ddw_goodfriendplay_tables","storeId",storeId);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(2,"成功",new ArrayList<>());
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(list));
    }
}