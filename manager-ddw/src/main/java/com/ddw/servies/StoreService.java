package com.ddw.servies;

import com.ddw.beans.StoreDTO;
import com.ddw.beans.StorePO;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

            storePO.setDsBusinessCodePath(fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);


        }else if(storeDTO.getIsUpdateImg()!=null && storeDTO.getIsUpdateImg().contains("dsImgFile")){
            storePO.setDsBusinessCodePath((String)voMap.get("dsBusinessCodePath"));
        }
        storePO.setUpdateTime(new Date());
        if(voMap!=null){

            Map updatePoMap= BeanToMapUtil.beanToMap(storePO);

            return this.commonUpdateBySingleSearchParam("ddw_store",updatePoMap,"id",storeDTO.getId());

        }else{
            storePO.setDmStatus(0);
            storePO.setCreateTime(new Date());
            return this.commonInsert("ddw_store",storePO);
        }
    }
    public List getRelateSysUsers(String idStr)throws Exception{
        String id= MyEncryptUtil.getRealValue(idStr);
        if(id==null)return null;
        List<Map> userList=this.commonList("baseUser","createTime desc",null,null,new HashMap<>());
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
                this.commonInsertMap("ddw_store_sysuser",pomap);
            }
            return new ResponseVO(1,"关联用户成功",null);
        }else{
            return new ResponseVO(1,"取消关联用户成功",null);
        }



    }
}
