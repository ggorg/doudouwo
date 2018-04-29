package com.ddw.services;

import com.ddw.beans.RealNameReviewPO;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
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
public class RealNameReviewService extends CommonService {
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO realName(String userId, String realName, String idcard, MultipartFile idcardFront, MultipartFile idcardOpposite)throws Exception{
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(realName) || idcardFront.isEmpty() || idcardOpposite.isEmpty()){
            return new ResponseVO(-1,"参数不正确",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("userId",userId);
            //查询状态审核未通过
            conditionMap.put("status,!=",2);
            RealNameReviewPO realPO = this.commonObjectBySearchCondition("ddw_realname_review",conditionMap,new RealNameReviewPO().getClass());
            if(realPO != null){
                return new ResponseVO(-2,"不允许重复提交申请",null);
            }
            RealNameReviewPO realNameReviewPO = new RealNameReviewPO();
            realNameReviewPO.setUserId(Integer.valueOf(userId));
            realNameReviewPO.setRealName(realName);
            realNameReviewPO.setIdcard(idcard);
            realNameReviewPO.setStatus(0);
            String idcardFrontImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( idcardFront.getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( idcardFront,mainGlobals.getRsDir(), idcardFrontImgName);

            realNameReviewPO.setIdcardFrontUrl(fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

            String idcardOppositeImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( idcardOpposite.getOriginalFilename());
            FileInfoVo fileInfoVo2= UploadFileMoveUtil.move( idcardOpposite,mainGlobals.getRsDir(), idcardOppositeImgName);
            realNameReviewPO.setIdcardOppositeUrl(fileInfoVo2.getUrlPath());
            CommonBeanFiles f2=this.fileService.createCommonBeanFiles(fileInfoVo2);
            this.fileService.saveFile(f2);
            Map updatePoMap= BeanToMapUtil.beanToMap(realNameReviewPO);
            return this.commonInsertMap("ddw_realname_review",updatePoMap);
        }
    }
}
