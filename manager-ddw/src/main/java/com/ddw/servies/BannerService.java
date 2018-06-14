package com.ddw.servies;

import com.ddw.beans.BannerPO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.*;
import com.ddw.services.CommonReviewService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
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

    public Page findList(Integer storeId,Integer pageNum, Integer pageSize)throws Exception{
        Map condition=new HashMap();
        condition.put("storeId",storeId);
        return super.commonPage("ddw_review_banner","createTime",pageNum,pageSize,condition);
    }

    public BannerPO selectById(String id){
        try {
            return super.commonObjectBySingleParam("ddw_review_banner","id",id,BannerPO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(BannerPO bannerPO)throws Exception{
        if(bannerPO.getId() > 0 ){
            Map updatePoMap= BeanToMapUtil.beanToMap(bannerPO);
            return super.commonUpdateBySingleSearchParam("ddw_review_banner",updatePoMap,"id",bannerPO.getId());
        }else{
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
            reviewPO.setDrProposerName(vo.get("uName").toString());
            reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType7.getCode());
            reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
            reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType0.getCode());
            reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
            reviewPO.setDrApplyDesc("申请实名认证");
            reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.realName11.getCode());
            this.submitAppl(reviewPO);
            return super.commonInsert("ddw_review_banner",bannerPO);
        }
    }

}
