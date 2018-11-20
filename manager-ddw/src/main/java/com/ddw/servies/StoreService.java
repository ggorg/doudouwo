package com.ddw.servies;

import com.ddw.beans.StoreDTO;
import com.ddw.beans.StorePO;
import com.ddw.enums.DisabledEnum;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店
 */
@Service
@Transactional(readOnly = true)
public class StoreService extends CommonService{
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;



    public Page findPage(Integer pageNo)throws Exception{

        Map condtion=new HashMap();
       // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_store","dsSort asc,updateTime desc",pageNo,10,condtion);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_store","id",id);
    }
    public StorePO getBeanById(Integer id)throws Exception{

        return this.commonObjectBySingleParam("ddw_store","id",id,StorePO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String idStr, Integer status){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        Map map=new HashMap();
        map.put("dsStatus",status);
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_store",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){

            CacheUtil.delete("stores","store");

            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"启用成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(StoreDTO storeDTO)throws Exception{
        if(storeDTO==null){
            return new ResponseVO(-2,"请填写门店信息",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsName())){
            return new ResponseVO(-2,"请填写门店名称",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsLeaderName())){
            return new ResponseVO(-2,"请填写联系人名称",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsLeaderTelNo())){
            return new ResponseVO(-2,"请填写联系人电话号码",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsAddress())){
            return new ResponseVO(-2,"请填写门店地址",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsBankCardCode())){
            return new ResponseVO(-2,"请填写对公账户银行卡号",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsCompanyName())){
            return new ResponseVO(-2,"请填写公司名称",null);

        }
        if(StringUtils.isBlank(storeDTO.getDsLngLat())){
            return new ResponseVO(-2,"请埴写经纬度坐标",null);
        }
        String[] lls= storeDTO.getDsLngLat().split(",");
        if(lls.length!=2 || !storeDTO.getDsLngLat().matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
            return new ResponseVO(-2,"坐标格式有误",null);

        }


        Map voMap=null;
        StorePO storePO=new StorePO();
        if(storeDTO.getId()!=null){
            voMap=this.getById(storeDTO.getId());
            PropertyUtils.copyProperties(storePO,voMap);
        }
        PropertyUtils.copyProperties(storePO,storeDTO);
        storePO.setDsLongitude(lls[0]);
        storePO.setDsLatitude(lls[1]);
        if(!storeDTO.getDsImgFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( storeDTO.getDsImgFile().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( storeDTO.getDsImgFile(),mainGlobals.getRsDir(), dmImgName);

            storePO.setDsBusinessCodePath(mainGlobals.getServiceUrl()+fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);


        }else if(storeDTO.getIsUpdateImg()!=null && storeDTO.getIsUpdateImg().contains("dsImgFile")){
            storePO.setDsBusinessCodePath((String)voMap.get("dsBusinessCodePath"));
        }
        if(!storeDTO.getDsHeadFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( storeDTO.getDsHeadFile().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( storeDTO.getDsHeadFile(),mainGlobals.getRsDir(), dmImgName);

            storePO.setDsHeadUrl(mainGlobals.getServiceUrl()+fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);


        }else if(storeDTO.getIsUpdateImg()!=null && storeDTO.getIsUpdateImg().contains("dsHeadUrl")){
            storePO.setDsHeadUrl((String)voMap.get("dsHeadUrl"));
        }
        if(!storeDTO.getDsBannerFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( storeDTO.getDsBannerFile().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( storeDTO.getDsBannerFile(),mainGlobals.getRsDir(), dmImgName);

            storePO.setDsBannerUrl(mainGlobals.getServiceUrl()+fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);


        }else if(storeDTO.getIsUpdateImg()!=null && storeDTO.getIsUpdateImg().contains("dsBannerUrl")){
            storePO.setDsBannerUrl((String)voMap.get("dsBannerUrl"));
        }
        storePO.setUpdateTime(new Date());
        ResponseVO vo=null;
        if(voMap!=null){

            Map updatePoMap= BeanToMapUtil.beanToMap(storePO);

            vo=this.commonUpdateBySingleSearchParam("ddw_store",updatePoMap,"id",storeDTO.getId());

        }else{
            storePO.setDsStatus(DisabledEnum.disabled0.getCode());
            storePO.setCreateTime(new Date());
            vo= this.commonInsert("ddw_store",storePO);
            if(vo.getReCode()==1){

                IMApiUtil.createGroup("ddwGuanFang",vo.getData()+"_gf_"+ RandomStringUtils.randomNumeric(10),storePO.getDsName()+"聊天室");
            }
        }
        CacheUtil.delete("stores","store");
        return vo;
    }
    public List getRelateSysUsers(String idStr)throws Exception{
        String id= MyEncryptUtil.getRealValue(idStr);
        if(id==null)return null;
        List<Map> userList=this.commonList("base_user","createTime desc",null,null,new HashMap<>());
        List<Map> sslist=this.commonObjectsBySingleParam("ddw_store_sysuser","storeId",Integer.parseInt(id));
        if(sslist!=null && sslist.size()>0 && userList!=null){
            Integer userid=null;
            for(Map map:userList){
               userid=(Integer)map.get("id");
               Integer sysUserId=null;
               for(Map ssMap:sslist){
                   sysUserId=(Integer) ssMap.get("sysUserId");
                   if(sysUserId==userid){
                       map.put("isThisStore",true);
                   }
               }
            }
        }
        return userList;
    }

    public StorePO getStoreBySysUserid(Integer id)throws Exception{
        //@Cacheable(value="commonCache",key = "'StoreBySysUserid-'+#id")
        StorePO storePO=(StorePO) CacheUtil.get("commonCache","StoreBySysUserid-"+id);
        if(storePO==null){
            Map conditon=new HashMap();
            conditon.put("sysUserId",id);

            CommonSearchBean csb=new CommonSearchBean("ddw_store",null,new CommonChildBean("ddw_store_sysuser","storeId","id",conditon));
            List list=this.getCommonMapper().selectObjects(csb);
            if(list!=null && list.size()>0){
                StorePO spo=new StorePO();
                PropertyUtils.copyProperties(spo,list.get(0));
                CacheUtil.put("commonCache","StoreBySysUserid-"+id,spo);
                return spo;
            }
        }
        return storePO;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveRelateSysUsers(String idStr,Integer[] sysusers)throws Exception{
        String id= MyEncryptUtil.getRealValue(idStr);
        if(id==null){
            return new ResponseVO(-2,"参数异常",null);
        }
        this.commonDelete("ddw_store_sysuser","storeId",id);
        if(sysusers!=null && sysusers.length>0){
            Map pomap=null;
            for(Integer userid:sysusers){
                pomap=new HashMap();
                pomap.put("storeId",id);
                pomap.put("sysUserId",userid);
                if(CacheUtil.get("commonCache","StoreBySysUserid-"+userid)!=null){
                    CacheUtil.delete("commonCache","StoreBySysUserid-"+userid);
                }
                this.commonInsertMap("ddw_store_sysuser",pomap);
            }
            return new ResponseVO(1,"关联用户成功",null);
        }else{
            return new ResponseVO(1,"取消关联用户成功",null);
        }



    }
}
