package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.*;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
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
 * 代练
 * Created by Jacky on 2018/4/16.
 */
@Service
public class ReviewPracticeService extends CommonService {
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private CommonReviewService commonReviewService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO apply(UserInfoVO user, String gameId, String rankId, MultipartFile photograph1,MultipartFile photograph2,MultipartFile photograph3)throws Exception{
        if(user == null || StringUtils.isBlank(gameId) || StringUtils.isBlank(rankId) ||
                photograph1 == null || photograph2 == null ||photograph3 == null ||
                photograph1.isEmpty() || photograph2.isEmpty() ||photograph3.isEmpty()){
            return new ResponseApiVO(-1,"参数不正确",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("drProposer",user.getId());
            conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
            //查询状态审核未通过
            conditionMap.put("drReviewStatus,!=",2);
            ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
            if(realPO != null){
                return new ResponseApiVO(-2,"不允许重复提交申请",null);
            }
            //更新会员代练状态为审核中
            Map setConditionMap = new HashMap<>();
            setConditionMap.put("practiceFlag",2);
            this.commonUpdateBySingleSearchParam("ddw_userinfo",setConditionMap,"id",user.getId());
            //插入审批表
            ReviewPO reviewPO=new ReviewPO();
            String bussinessCode = String.valueOf(new Date().getTime());
            reviewPO.setDrBusinessCode(bussinessCode);
            reviewPO.setCreateTime(new Date());
            reviewPO.setDrProposerName(user.getRealName());
            reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
            reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
            reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
            reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
            reviewPO.setDrProposer(Integer.valueOf(user.getId()));
            reviewPO.setDrApplyDesc("申请成为代练");
            reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.practiceFlag5.getCode());
            ResponseApiVO responseApiVO = new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));
            //插入代练认证审核表
            if(responseApiVO.getReCode()>0){
                ReviewPracticePO reviewPracticePO = new ReviewPracticePO();
                reviewPracticePO.setUserId(Integer.valueOf(user.getId()));
                reviewPracticePO.setDrBusinessCode(bussinessCode);
                reviewPracticePO.setGameId(Integer.valueOf(gameId));
                reviewPracticePO.setRankId(Integer.valueOf(rankId));
                reviewPracticePO.setCreateTime(new Date());
                reviewPracticePO.setStatus(0);
                String ImgName1= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo1= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName1);
                reviewPracticePO.setPicUrl1(mainGlobals.getServiceUrl()+fileInfoVo1.getUrlPath());
                CommonBeanFiles f1=this.fileService.createCommonBeanFiles(fileInfoVo1);
                this.fileService.saveFile(f1);

                String ImgName2= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo2= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName1);
                reviewPracticePO.setPicUrl2(mainGlobals.getServiceUrl()+fileInfoVo2.getUrlPath());
                CommonBeanFiles f2=this.fileService.createCommonBeanFiles(fileInfoVo2);
                this.fileService.saveFile(f2);

                String ImgName3= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo3= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName3);
                reviewPracticePO.setPicUrl3(mainGlobals.getServiceUrl()+fileInfoVo3.getUrlPath());
                CommonBeanFiles f3=this.fileService.createCommonBeanFiles(fileInfoVo3);
                this.fileService.saveFile(f3);


                Map updatePoMap= BeanToMapUtil.beanToMap(reviewPracticePO);
                this.commonInsertMap("ddw_review_practice",updatePoMap);
            }
            return responseApiVO;
        }
    }

}
