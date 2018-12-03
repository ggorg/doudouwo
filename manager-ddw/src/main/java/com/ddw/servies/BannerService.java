package com.ddw.servies;

import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.services.CommonReviewService;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/16.
 */
@Service
@Transactional(readOnly = true)
public class BannerService extends CommonReviewService {
    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private FileService fileService;

    public Page findList(Integer storeId,Integer pageNum,BannerTypeEnum type)throws Exception{
        Map condition=new HashMap();
        condition.put("storeId",storeId);
        condition.put("bType",type.getCode());
        return super.commonPage("ddw_banner","createTime desc",pageNum,10,condition);
    }
    public Map getByIdAndType(Integer id,Integer storeId,BannerTypeEnum type)throws Exception{
        Map search=new HashMap();
        search.put("storeId",storeId);
        search.put("bType",type.getCode());
        search.put("id",id);
        return this.commonObjectBySearchCondition("ddw_banner",search);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(Integer id, Integer status,Integer storeId){
        if(id==null || id<=0){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        if(BannerEnableEnum.type1.getCode().equals(status)){
            Map countMap=new HashMap();
            countMap.put("enable",BannerEnableEnum.type1.getCode());
            countMap.put("bType",BannerTypeEnum.type2.getCode());
            countMap.put("storeId",storeId);
            long c=this.commonCountBySearchCondition("ddw_banner",countMap);
            if(c>=10){
                return new ResponseVO(-2,"抱歉，发布数量不能超过10个",null);
            }
        }

        Map map=new HashMap();
        map.put("enable",status);
        Map search=new HashMap();
        search.put("id",id);
        search.put("storeId",storeId);
        ResponseVO res=this.commonUpdateByParams("ddw_banner",map,search);
        if(res.getReCode()==1){
            CacheUtil.delete("publicCache","banner-shop-all-"+storeId);

            if(BannerEnableEnum.type1.getCode().equals(status)){
                return new ResponseVO(1,"发布成功",null);

            }else if(BannerEnableEnum.type0.getCode().equals(status)){
                return new ResponseVO(1,"撤回成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveShopBanner(BannerDTO dto,Integer storeId)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getName())){
            return new ResponseVO(-2,"请填写名称",null);

        }


        Map map=BeanToMapUtil.beanToMap(dto,true);

        if(!dto.getFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getFile().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move(dto.getFile(),mainGlobals.getRsDir(), dmImgName);
            map.put("picUrl",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());

            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

        }

        map.remove("file");
        map.put("storeId",storeId);
        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("enable",BannerEnableEnum.type0.getCode());
            map.put("bType",BannerTypeEnum.type2.getCode());
            ResponseVO res=this.commonInsertMap("ddw_banner",map);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","banner-shop-all-"+storeId);
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            Map search=new HashMap();
            search.put("id",dto.getId());
            search.put("storeId",storeId);

            ResponseVO res=this.commonUpdateByParams("ddw_banner",map,search);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","banner-shop-all-"+storeId);
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(Integer storeId,BannerDTO bannerDTO)throws Exception{

        String fileName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( bannerDTO.getFile().getOriginalFilename());
        FileInfoVo fileInfoVo= UploadFileMoveUtil.move( bannerDTO.getFile(),mainGlobals.getRsDir(), fileName);

        CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
        this.fileService.saveFile(f);

        BannerPO bannerPO = new BannerPO();
        PropertyUtils.copyProperties(bannerPO,bannerDTO);
        bannerPO.setCreateTime(new Date());
        bannerPO.setStoreId(storeId);
        bannerPO.setStatus(0);
        bannerPO.setPicUrl(mainGlobals.getServiceUrl() + fileInfoVo.getUrlPath());
        bannerPO.setbType(BannerTypeEnum.type1.getCode());
        Map conditionMap = new HashMap<>();
        conditionMap.put("drProposer",bannerPO.getStoreId());
        conditionMap.put("drBusinessType", ReviewBusinessTypeEnum.ReviewBusinessType7.getCode());
        //查询状态审核未通过
        conditionMap.put("drReviewStatus,!=", ReviewStatusEnum.ReviewStatus2.getCode());
        ReviewPO rePO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewPO().getClass());
        if(rePO != null){
            return new ResponseVO(-2,"不允许重复提交申请",null);
        }
        //操作员
        Map vo = (Map)Tools.getSession("user");
        //插入审批表
        ReviewPO reviewPO=new ReviewPO();
        String bussinessCode = String.valueOf(new Date().getTime());
        reviewPO.setDrBusinessCode(bussinessCode);
        reviewPO.setCreateTime(new Date());
        reviewPO.setDrProposer((Integer) vo.get("id"));
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setDrProposerName(vo.get("uName").toString());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType7.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType0.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        reviewPO.setDrApplyDesc(bannerPO.getbDescribe());
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.banner7.getCode());
        this.submitAppl(reviewPO);
        bannerPO.setDrBusinessCode(bussinessCode);
        return super.commonInsert("ddw_banner",bannerPO);
    }

}
