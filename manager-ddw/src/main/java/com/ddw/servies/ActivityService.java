package com.ddw.servies;

import com.ddw.beans.ActivityDTO;
import com.ddw.beans.TicketDTO;
import com.ddw.beans.TicketPO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.ActivityTypeEnum;
import com.ddw.enums.DisabledEnum;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ActivityService extends CommonService {

    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private FileService fileService;

    @Value("${activity.page.dir}")
    private String dir;

    public Page findPage(Integer pageNo,Integer storeId)throws Exception{

        Map search=new HashMap();
        search.put("storeId",storeId);
        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_activity","updateTime desc",pageNo,10,search);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_activity","id",id);
    }
    public TicketPO getBeanById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_activity","id",id,TicketPO.class);
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
        map.put("dtDisabled",status);
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_activity",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){

            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"发布成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(ActivityDTO dto,Integer storeId)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDtTitle())){
            return new ResponseVO(-2,"请填活动标题",null);

        }
        if(StringUtils.isBlank(ActivityTypeEnum.getName(dto.getDtType()))){
            return new ResponseVO(-2,"类型有误",null);

        }
        Map map=BeanToMapUtil.beanToMap(dto,true);

        if(!dto.getDtImgFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getDtImgFile().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move(dto.getDtImgFile(),mainGlobals.getRsDir(), dmImgName);
            map.put("dtImgPath",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());

            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

        }
        if(ActivityTypeEnum.type2.getCode().equals(dto.getDtType())){

            if(!dto.getZipFile().isEmpty()){
                String baseName=FilenameUtils.getBaseName(dto.getZipFile().getOriginalFilename());
                File f=new File(dir,baseName);
                if(f.exists()){
                    f=new File(dir,baseName+ DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
                }
                f.mkdirs();
                List<String> files=ExtractZip.unZip(dto.getZipFile().getInputStream(),f.getPath());
                if(!files.contains("index.html")){
                    return new ResponseVO(-2,"压缩包里面必须要有index.html",null);
                }
                map.put("dirPath",f.getPath());
                map.put("dtTargetPath",ddwGlobals.getCallBackHost()+"/ddw/act/"+f.getName()+"/index.html");
            }else{
                map.remove("dtTargetPath");
            }

        }else if(ActivityTypeEnum.type3.getCode().equals(dto.getDtType())){
            StringBuilder builder=new StringBuilder();
            builder.append("<!DOCTYPE html><html lang=\"zh-CN\"><body>").append(dto.getDtContent()).append("</body></html>");

            if(dto.getId()==null){
                String filename="act"+ RandomStringUtils.randomAlphanumeric(10)+".html";
                FileUtils.write(new File(dir,filename),builder.toString(),"UTF-8");
                map.put("dirPath",dir+filename);
                map.put("dtTargetPath",ddwGlobals.getCallBackHost()+"/ddw/act/"+filename);
            }else{
                Map dataMap=this.commonObjectBySingleParam("ddw_activity","id",dto.getId());
                String filename=null;
                File f=null;
                if(!dataMap.containsKey("dirPath") || StringUtils.isBlank(dataMap.get("dirPath").toString())){
                    filename="act"+ RandomStringUtils.randomAlphanumeric(10)+".html";
                    f=new File(dir,filename);
                    map.put("dirPath",dir+filename);
                    map.put("dtTargetPath",ddwGlobals.getCallBackHost()+"/ddw/act/"+filename);
                }else{
                    f=new File(dataMap.get("dirPath").toString());
                }

                FileUtils.write(f,builder.toString(),"UTF-8");
            }

            //FileUtils.write(new File(dir,"activity"));
        }
        map.put("updateTime",new Date());
        map.remove("dtImgFile");
        map.remove("zipFile");
       // map.remove("isUpdateImg");
        if(StringUtils.isNotBlank(dto.getActiveTime())){
            String[] dateStr=dto.getActiveTime().split(" - ");
            map.put("dtStartTime", DateUtils.parseDate(dateStr[0],"yyyy-MM-dd HH:mm"));
            map.put("dtEndTime", DateUtils.parseDate(dateStr[1],"yyyy-MM-dd HH:mm"));
            map.remove("activeTime");

        }
        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("storeId",storeId);
            map.put("dtDisabled",DisabledEnum.disabled1.getCode());
            ResponseVO res=this.commonInsertMap("ddw_activity",map);
            if(res.getReCode()==1){
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            Map searchMap=new HashMap();
            searchMap.put("storeId",storeId);
            searchMap.put("id",dto.getId());
            ResponseVO res=this.commonUpdateByParams("ddw_activity",map,searchMap);
            if(res.getReCode()==1){
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
