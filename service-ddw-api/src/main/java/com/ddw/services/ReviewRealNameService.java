package com.ddw.services;

import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.ReviewPO;
import com.ddw.beans.ReviewRealNamePO;
import com.ddw.enums.*;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实名认证审核申请
 * Created by Jacky on 2018/4/29.
 */
@Service
@Transactional(readOnly = true)
public class ReviewRealNameService extends CommonService {
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private CommonReviewService commonReviewService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO realName(String userId, String realName, String idcard, MultipartFile idcardFront, MultipartFile idcardOpposite)throws Exception{
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(realName) || idcardFront.isEmpty() || idcardOpposite.isEmpty()){
            return new ResponseApiVO(-1,"参数不正确",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("drProposer",userId);
            conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType4.getCode());
            //查询状态审核未通过
            conditionMap.put("drReviewStatus,!=",ReviewStatusEnum.ReviewStatus2.getCode());
            ReviewPO rePO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewPO().getClass());
            if(rePO != null){
                return new ResponseApiVO(-2,"不允许重复提交申请",null);
            }
            //插入审批表
            ReviewPO reviewPO=new ReviewPO();
            String bussinessCode = String.valueOf(new Date().getTime());
            reviewPO.setDrBusinessCode(bussinessCode);
            reviewPO.setCreateTime(new Date());
            reviewPO.setDrProposerName(realName);
            reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType4.getCode());
            reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
            reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
            reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
            reviewPO.setDrProposer(Integer.valueOf(userId));
            reviewPO.setDrApplyDesc("申请实名认证");
            reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.realName11.getCode());
            ResponseApiVO responseApiVO = new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));
            //插入会员实名认证审核表
            if(responseApiVO.getReCode()>0){
                ReviewRealNamePO reviewRealNamePO = new ReviewRealNamePO();
                reviewRealNamePO.setUserId(Integer.valueOf(userId));
                reviewRealNamePO.setDrBusinessCode(bussinessCode);
                reviewRealNamePO.setRealName(realName);
                reviewRealNamePO.setIdcard(idcard);
                reviewRealNamePO.setCreateTime(new Date());
                reviewRealNamePO.setStatus(0);
                String idcardFrontImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( idcardFront.getOriginalFilename());
                FileInfoVo fileInfoVo= UploadFileMoveUtil.move( idcardFront,mainGlobals.getRsDir(), idcardFrontImgName);

                reviewRealNamePO.setIdcardFrontUrl(mainGlobals.getServiceUrl()+fileInfoVo.getUrlPath());
                CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
                this.fileService.saveFile(f);

                String idcardOppositeImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( idcardOpposite.getOriginalFilename());
                FileInfoVo fileInfoVo2= UploadFileMoveUtil.move( idcardOpposite,mainGlobals.getRsDir(), idcardOppositeImgName);
                reviewRealNamePO.setIdcardOppositeUrl(mainGlobals.getServiceUrl()+fileInfoVo2.getUrlPath());
                CommonBeanFiles f2=this.fileService.createCommonBeanFiles(fileInfoVo2);
                this.fileService.saveFile(f2);
                Map updatePoMap= BeanToMapUtil.beanToMap(reviewRealNamePO);
                ResponseVO rv =this.commonInsertMap("ddw_review_realname",updatePoMap);
                if (rv.getReCode() > 0) {
                    //更新缓存中审核状态为2审核中
                    CacheUtil.put("review","realname"+userId,2);
                }
            }
            return responseApiVO;
        }
    }
}
