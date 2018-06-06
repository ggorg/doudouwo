package com.ddw.servies;

import com.ddw.beans.GiftDTO;
import com.ddw.beans.GiftPO;
import com.ddw.beans.StoreDTO;
import com.ddw.beans.StorePO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.DisabledEnum;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
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
import java.util.Map;

@Service
public class GiftService extends CommonService {

    @Autowired
    private FileService fileService;

    @Autowired
    private DDWGlobals ddwGlobals;

    @Autowired
    private MainGlobals mainGlobals;

    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_gift","dgSort asc,updateTime desc",pageNo,10,null);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_gift","id",id);
    }
    public GiftPO getBeanById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_gift","id",id,GiftPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String idStr,Integer status){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        Map map=new HashMap();
        map.put("dgDisabled",status);
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_gift",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){
            CacheUtil.delete("publicCache","allGift");
            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"发布成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"撤回成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(GiftDTO dto)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDgName())){
            return new ResponseVO(-2,"请填写礼物名称",null);

        }
        if(dto.getDgPrice()==null ||dto.getDgPrice()<0){
            return new ResponseVO(-2,"请填写有效的价格",null);

        }
        if(dto.getDgActPrice()!=null && dto.getDgActPrice()<0){
            return new ResponseVO(-2,"请填写有效的活动价格",null);

        }




        Map voMap=null;
        GiftPO giftPO=new GiftPO();
        if(dto.getId()!=null){
            voMap=this.getById(dto.getId());
            PropertyUtils.copyProperties(giftPO,voMap);

        }

        PropertyUtils.copyProperties(giftPO,dto);

        if(!dto.getDgImg().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getDgImg().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move(dto.getDgImg(),mainGlobals.getRsDir(), dmImgName);

            giftPO.setDgImgPath(ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

        }else if(dto.getIsUpdateImg()!=null && dto.getIsUpdateImg().contains("dsImgFile")){
            giftPO.setDgImgPath(giftPO.getDgImgPath());

        }
        giftPO.setUpdateTime(new Date());
        if(voMap!=null){

            Map updatePoMap= BeanToMapUtil.beanToMap(giftPO);
            CacheUtil.delete("publicCache","allGift");
            return this.commonUpdateBySingleSearchParam("ddw_gift",updatePoMap,"id",giftPO.getId());


        }else{
            CacheUtil.delete("publicCache","allGift");
            //giftPO.setDgDisabled(DisabledEnum.disabled0);
            giftPO.setCreateTime(new Date());
            return this.commonInsert("ddw_gift",giftPO);
        }
    }
}
