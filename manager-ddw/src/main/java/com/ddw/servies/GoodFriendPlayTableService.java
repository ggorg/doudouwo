package com.ddw.servies;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.StorePO;
import com.ddw.beans.TableDTO;
import com.ddw.beans.TicketDTO;
import com.ddw.beans.TicketPO;
import com.ddw.config.DDWGlobals;
import com.ddw.controller.GoodFriendPlayTableController;
import com.ddw.enums.DisabledEnum;
import com.ddw.enums.GoodFriendPlayRoomStatusEnum;
import com.ddw.enums.TableStatusEnum;
import com.ddw.util.QrCodeCreateUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GoodFriendPlayTableService extends CommonService {
    private final Logger logger = Logger.getLogger(GoodFriendPlayTableService.class);

    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private FileService fileService;


    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_goodfriendplay_tables","updateTime desc",pageNo,10,null);
    }
    public boolean checkRoomStatus(Integer tableId,Integer storeId){
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("tableCode",tableId);
        search.put("status",GoodFriendPlayRoomStatusEnum.status1.getCode());
        long n=this.commonCountBySearchCondition("ddw_goodfriendplay_room",search);
        if(n>0)return true;
        return false;

    }
    public void loadQrCode(Integer id, StorePO po, HttpServletResponse response){
        OutputStream os=null;
        try {
            Map search=new HashMap();
            search.put("id",id);
            search.put("storeId",po.getId());
            Map dataMap=this.commonObjectBySearchCondition("ddw_goodfriendplay_tables",search);
            Map map=new HashMap();
            map.put("tableNumber",dataMap.get("tableNumber"));
            map.put("storeId",po.getId());
            map.put("storeName",po.getDsName());
            Map mainMap=new HashMap();
            mainMap.put("command","order");
            mainMap.put("commandDesc","商城扫码下单");
            mainMap.put("data",map);
            response.setContentType("image/jpeg");
            os=response.getOutputStream();
            QrCodeCreateUtil.createQrCode(os, JSONObject.toJSONString(mainMap),900,"jpeg");

        }catch (Exception e){
            logger.error("GoodFriendPlayTableService->loadQrCode",e);
        }finally {
            if(os!=null)IOUtils.closeQuietly(os);
        }
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_goodfriendplay_tables","id",id);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deleteByID(Integer id,Integer storeId)throws Exception{
        if(checkRoomStatus(id,storeId)){
            return new ResponseVO(-2,"当前桌号被使用中，没法删除",null);
        }
        Map searchMap=new HashMap();
        searchMap.put("storeId",storeId);
        searchMap.put("id",id);
        return this.commonDeleteByParams("ddw_goodfriendplay_tables",searchMap);
    }
    public TicketPO getBeanById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_goodfriendplay_tables","id",id,TicketPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(Integer id,Integer status,Integer storeId){
        if(id==null || id<=0){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(TableStatusEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        Map map=new HashMap();
        map.put("status",status);
        Map search=new HashMap();
        search.put("id",id);
        search.put("storeId",storeId);
        ResponseVO res=this.commonUpdateByParams("ddw_goodfriendplay_tables",map,search);
        if(res.getReCode()==1){

            return new ResponseVO(1,"状态更改成功",null);
        }
        return new ResponseVO(-2,"状态更改失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(TableDTO dto,Integer storeId)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getTableNumber())){
            return new ResponseVO(-2,"请填写桌号",null);

        }
        if(dto.getPeopleMaxNum()==null ||dto.getPeopleMaxNum()<0){
            return new ResponseVO(-2,"请填写座位数",null);

        }
        if(dto.getStatus()==null || StringUtils.isBlank(TableStatusEnum.getName(dto.getStatus()))){
            return new ResponseVO(-2,"请选择桌子状态",null);

        }
        Map map=BeanToMapUtil.beanToMap(dto,true);



        map.put("updateTime",new Date());

        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("storeId",storeId);
            map.put("status",TableStatusEnum.status0.getCode());
            ResponseVO res=this.commonInsertMap("ddw_goodfriendplay_tables",map);
            if(res.getReCode()==1){
               // CacheUtil.delete("publicCache","allTicket");
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            if(checkRoomStatus(dto.getId(),storeId)){
                return new ResponseVO(-2,"当前桌号被使用中，没法修改",null);
            }
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_tables",map,"id",dto.getId());
            if(res.getReCode()==1){
               // CacheUtil.delete("publicCache","allTicket");
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
