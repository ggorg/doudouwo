package com.ddw.servies;

import com.ddw.beans.MaterialDTO;
import com.ddw.beans.MaterialPO;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 原材料
 */
@Service
public class MaterialService  extends CommonService {

    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;


    public Page findPage(Integer pageNo,Integer dmStatus)throws Exception{

        Map condtion=new HashMap();
        condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_material","dmSort asc,updateTime desc",pageNo,10,condtion);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateStatus(String idstr,Integer status)throws Exception{
        if(StringUtils.isBlank(idstr)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(status==null){
            return new ResponseVO(-2,"状态参数有异常",null);
        }
        String ids=MyEncryptUtil.getRealValue(idstr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数已失效",null);
        }
        String msg=null;
        if(status==2){
            msg="禁用";

        }else if(status==1){
            msg="发布";
        }else{
            Map voMap=this.getById(Integer.parseInt(ids));
            Integer val=null;
            if((val=(Integer)voMap.get("dmStatus"))!=null && val==1){
                msg="撤回发布";
            }else{
                msg="启用";
            }

        }
        if(status>-1 && status<3){
            Map poMap=new HashMap();
            poMap.put("dmStatus",status);
            poMap.put("updateTime",new Date());
           ResponseVO vo= this.commonUpdateBySingleSearchParam("ddw_material",poMap,"id",Integer.parseInt(ids));
           if(vo.getReCode()==1){

               vo.setReMsg(msg+"成功");
               return vo;
           }

        }
        return new ResponseVO(-2,msg+"失败",null);
    }
    /**
     * 入库
     * @param idstr
     * @param num
     * @param userId
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO inbound(String idstr,Integer num,Integer userId)throws Exception{
        if(StringUtils.isBlank(idstr)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(num==null){
            return new ResponseVO(-2,"增加库存数的参数有异常",null);
        }
        String ids=MyEncryptUtil.getRealValue(idstr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数已失效",null);
        }
        Integer id=Integer.parseInt(ids);
        for (int i=1;i<=5;i++) {
            Map voMap = this.getById(id);
            if (voMap == null || voMap.size() <= 0) {
                return new ResponseVO(-2, "当前材料不存在", null);
            }
            Integer dmVersion = (Integer) voMap.get("dmVersion");
            Integer dmCurrentCount = (Integer) voMap.get("dmCurrentCount");

            voMap.put("dmVersion", dmVersion==null?2:dmVersion+1);
            voMap.put("dmCurrentCount", dmCurrentCount==null?num:dmCurrentCount + num);
            voMap.put("updateTime", new Date());

            Map searchCondition = new HashMap();
            searchCondition.put("id", id);
            searchCondition.put("dmVersion", dmVersion);
            ResponseVO mVo = this.commonUpdateByParams("ddw_material", voMap, searchCondition);
            if (mVo.getReCode() == -2) {
                if(i==5){
                    return new ResponseVO(-2,"入库失败，系统繁忙",null);
                }
                Thread.sleep(i * 200);
                continue;
            }else {
                break;
            }
        }

        Map poInbound=new HashMap();
        poInbound.put("dmiInboundNumber",num);
        poInbound.put("dmiOutboundNumber",0);
        poInbound.put("createTime",new Date());
        poInbound.put("creater",userId);
        poInbound.put("dmiType",0);
        poInbound.put("materialId",id);
        ResponseVO rVo=this.commonInsertMap("ddw_material_inventory_record",poInbound);
        if(rVo.getReCode()==-2){
            throw new GenException("入库失败");
        }
        return new ResponseVO(1,"入库成功",null);

    }

    /**
     * 添加
     * @param materialDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(MaterialDTO materialDTO)throws Exception{
        if(materialDTO==null){
            return new ResponseVO(-2,"请填写原材料信息",null);

        }
        if(materialDTO.getDmUnit()==null){
            return new ResponseVO(-2,"请填写净含量信息",null);

        }
        if(StringUtils.isBlank(materialDTO.getDmName())){
            return new ResponseVO(-2,"请填写材料名称",null);

        }
        if(materialDTO.getDmCost()==null || materialDTO.getDmCost()<0){
            return new ResponseVO(-2,"请填写有效的成本价格",null);

        }
        if(materialDTO.getDmSales()==null || materialDTO.getDmSales()<0){
            return new ResponseVO(-2,"请填写有效的零售价格",null);

        }
        Map voMap=null;
        MaterialPO materialPO=new MaterialPO();
        if(materialDTO.getId()!=null){
            voMap=this.getById(materialDTO.getId());
            PropertyUtils.copyProperties(materialPO,voMap);
        }
       // Map mapPo=new HashMap();


        PropertyUtils.copyProperties(materialPO,materialDTO);
        if(!materialDTO.getDmImgFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( materialDTO.getDmImgFile().getOriginalFilename());
            FileInfoVo fileInfoVo=UploadFileMoveUtil.move( materialDTO.getDmImgFile(),mainGlobals.getRsDir(), dmImgName);

            materialPO.setDmImgPath(fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);
            File icoFile=new File(fileInfoVo.getFileSrc().replace(".","-ico."));
            Thumbnails.of(fileInfoVo.getFileSrc()).scale(0.5f).outputQuality(0.5f).toFile(icoFile);
            materialPO.setDmIcoImgPath(fileInfoVo.getUrlPath().replace(".","-ico."));

            CommonBeanFiles icoF=new CommonBeanFiles();
            PropertyUtils.copyProperties(icoF,f);
            icoF.setLocalPath(f.getLocalPath().replace(".","-ico."));
            icoF.setFileName(f.getFileName().replace(".","-ico."));
            icoF.setUrlPath(f.getUrlPath().replace(".","-ico."));
            this.fileService.saveFile(icoF);
        }else if(materialDTO.getIsUpdateImg()!=null && materialDTO.getIsUpdateImg().contains("dmImgFile")){
            materialPO.setDmIcoImgPath((String)voMap.get("dmIcoImgPath"));
            materialPO.setDmImgPath((String)voMap.get("dmImgPath"));
        }
        materialPO.setUpdateTime(new Date());
        if(voMap!=null){

            Map updatePoMap=BeanToMapUtil.beanToMap(materialPO);

            return this.commonUpdateBySingleSearchParam("ddw_material",updatePoMap,"id",materialDTO.getId());

        }else{
            materialPO.setCreateTime(new Date());
            materialPO.setDmCurrentCount(0);
            materialPO.setDmVersion(1);
            return this.commonInsert("ddw_material",materialPO);
        }
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_material","id",id);
    }
}
