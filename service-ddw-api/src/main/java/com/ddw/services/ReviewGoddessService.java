package com.ddw.services;

import com.ddw.beans.ReviewPO;
import com.ddw.beans.ReviewRealNamePO;
import com.ddw.beans.UserInfoVO;
import com.ddw.enums.*;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 女神
 * Created by Jacky on 2018/4/16.
 */
@Service
public class ReviewGoddessService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO apply(UserInfoVO user,int storeId)throws Exception{
        Map conditionMap = new HashMap<>();
        conditionMap.put("drProposer",user.getId());
        conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        //查询状态审核未通过
        conditionMap.put("drReviewStatus,!=",2);
        ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
        if(realPO != null){
            return new ResponseVO(-3,"不允许重复提交申请",null);
        }
        //更新会员女神状态为审核中
        Map setConditionMap = new HashMap<>();
        setConditionMap.put("goddessFlag",2);
        this.commonUpdateBySingleSearchParam("ddw_userinfo",setConditionMap,"id",user.getId());
        //插入审批表
        ReviewPO reviewPO=new ReviewPO();
        String bussinessCode = String.valueOf(new Date().getTime());
        reviewPO.setDrBusinessCode(bussinessCode);
        reviewPO.setCreateTime(new Date());
        reviewPO.setDrProposerName(user.getRealName());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        reviewPO.setDrProposer(Integer.valueOf(user.getId()));
        reviewPO.setDrApplyDesc("申请成为女神");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.goddessFlag2.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        return this.commonReviewService.submitAppl(reviewPO);
    }

}
