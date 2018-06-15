package com.ddw.servies;

import com.ddw.beans.BannerDTO;
import com.ddw.beans.BannerPO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.*;
import com.ddw.services.CommonReviewService;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.FileService;
import com.gen.common.util.Page;
import com.gen.common.util.Tools;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;

    public Page findList(Integer storeId,Integer pageNum)throws Exception{
        Map condition=new HashMap();
        condition.put("storeId",storeId);
        return super.commonPage("ddw_review_banner","id desc",pageNum,10,condition);
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
        bannerPO.setPicUrl(mainGlobals.getServiceUrl() + fileInfoVo.getUrlPath());
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
        reviewPO.setDrApplyDesc(bannerPO.getDescribe());
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.banner7.getCode());
        this.submitAppl(reviewPO);
        bannerPO.setDrBusinessCode(bussinessCode);
        return super.commonInsert("ddw_review_banner",bannerPO);
    }

}
