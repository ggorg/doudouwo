package com.ddw.services;

import com.ddw.beans.LiveRadioApplDTO;
import com.ddw.beans.LiveRadioPO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.*;
import com.ddw.util.BusinessCodeUtil;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;

    @Autowired
    private LiveRadioService liveRadioService;

    /**
     * 判断是否未审核直播
     * @param userid
     * @return
     */
    public boolean hasLiveRadioReviewFromGoddess(Integer userid,Integer storeId){
        return this.hasReview(userid,storeId,ReviewBusinessTypeEnum.ReviewBusinessType3,ReviewBusinessStatusEnum.liveRadio10,ReviewReviewerTypeEnum.ReviewReviewerType1);
    }
    public ResponseApiVO getLiveRadioReviewStatus(Integer userid,Integer storeId)throws Exception{

        LiveRadioPO liveRadioPO=liveRadioService.getLiveRadio(userid,storeId);
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus0.getCode()) {
            return new ResponseApiVO(2,"直播等待中，请前往直播房间",liveRadioPO);

        }
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus1.getCode()){
            return new ResponseApiVO(-2002,"直播房间已开，请关闭再申请",liveRadioPO);
        }
        boolean hasReview=this.hasLiveRadioReviewFromGoddess(userid,storeId);
        if(hasReview){
            //直播审核中
            CacheUtil.put("review","liveRadio"+userid,2);
            return new ResponseApiVO(-2003,"正在审核中，请耐心等待",null);

        }
        return new ResponseApiVO(1,"未申请",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO applyLiveRadio(String userOpenId, Integer storeId, LiveRadioApplDTO dto)throws Exception{
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        Map upo=this.commonObjectBySingleParam("ddw_userinfo","openid",userOpenId);
        if(upo==null){
            return new ResponseApiVO(-2000,"用户不存在",null);
        }
        if(!GoddessFlagEnum.goddessFlag1.getCode().equals((Integer) upo.get("goddessFlag"))){
            return new ResponseApiVO(-2001,"请先申请当女神",null);
        }
        Integer userid=(Integer) upo.get("id");
        ResponseApiVO apiVO=getLiveRadioReviewStatus(userid,storeId);
        if(apiVO.getReCode()!=1){
            return apiVO;
        }
        ReviewPO reviewPO=new ReviewPO();
        reviewPO.setDrBusinessCode(BusinessCodeUtil.createLiveRadioCode(userid,storeId));
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setCreateTime(new Date());
        StringBuilder nameBuild=new StringBuilder();

        if(StringUtils.isNotBlank((String)upo.get("userName"))){
            nameBuild.append((String)upo.get("userName"));
        }else if(StringUtils.isNotBlank((String)upo.get("nickName"))){
            nameBuild.append((String)upo.get("nickName"));
        }
        if(StringUtils.isNotBlank((String)upo.get("realName"))){
            nameBuild.append("(").append((String)upo.get("realName")).append(")");
        }
        reviewPO.setDrProposerName(nameBuild.toString());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setDrProposer(userid);
        reviewPO.setDrApplyDesc("申请开通直播空间");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.liveRadio10.getCode());
        reviewPO.setDrExtend("房间名称-"+dto.getRoomName());
        return new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));

    }
    /**
     * 判断是否未审核
     * @param proposerId 业务表的流水号
     * @param storeId 审核单位的ID，空即是总店
     * @param businessType 业务类型
     * @param businessStatus 业务状态
     * @param reviewReviewerTypeEnum 审核人类型
     * @return
     */
    public boolean hasReview(Integer proposerId,Integer storeId, ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus,ReviewReviewerTypeEnum reviewReviewerTypeEnum){
        Map condition=new HashMap();
        condition.put("drProposer",proposerId);
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        condition.put("drReviewerType", reviewReviewerTypeEnum.getCode());
        if(storeId!=null && reviewReviewerTypeEnum.getCode()==ReviewReviewerTypeEnum.ReviewReviewerType1.getCode()){
            condition.put("drBelongToStoreId", storeId);
        }
        return this.commonCountBySearchCondition("ddw_review",condition)>0?true:false;
    }



}
