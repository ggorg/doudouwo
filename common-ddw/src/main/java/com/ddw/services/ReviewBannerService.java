package com.ddw.services;

import com.ddw.beans.BannerPO;
import com.ddw.beans.ReviewCallBackBean;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewBannerService extends CommonService {

    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        Integer startRow = pageNo > 0 ? (pageNo - 1) * 10 : 0;
        CommonSearchBean csb=new CommonSearchBean("ddw_review","id desc","t1.*,ct0.enable ",startRow,10,condtion,new CommonChildBean("ddw_banner","drBusinessCode","drBusinessCode",null));
        return this.commonPage(pageNo,10,csb);
    }

    public Page findBannerPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType7.getCode());
        return this.findPage(pageNo,condtion);
    }

    public ReviewPO getReviewById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);
    }

    public BannerPO getReviewBannerByCode(String drBusinessCode)throws Exception{
        return this.commonObjectBySingleParam("ddw_banner","drBusinessCode",drBusinessCode,BannerPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateEnable(String drBusinessCode,Integer enable)throws Exception{
        BannerPO bannerPO = this.getReviewBannerByCode(drBusinessCode);
        CacheUtil.delete("publicCache","appIndexBanner");
        Map setParams=new HashMap();
        setParams.put("enable",enable);
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",drBusinessCode);
        searchCondition.put("status",1);//已审核
        return this.commonUpdateByParams("ddw_banner",setParams,searchCondition);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewBanner(ReviewCallBackBean rb)throws Exception{
        //根据审核结果处理
        if(rb.getReviewPO().getDrReviewStatus() == 1) {
            CacheUtil.delete("publicCache", "appIndexBanner");
            Map setParams = new HashMap();
            setParams.put("status", 1);
            setParams.put("enable", 1);
            Map searchCondition = new HashMap();
            searchCondition.put("drBusinessCode", rb.getBusinessCode());
            searchCondition.put("status", 0);//未审核
            return this.commonUpdateByParams("ddw_banner", setParams, searchCondition);
        }else{
            Map setParams = new HashMap();
            setParams.put("status", 2);
            Map searchCondition = new HashMap();
            searchCondition.put("drBusinessCode", rb.getBusinessCode());
            return this.commonUpdateByParams("ddw_banner", setParams, searchCondition);
        }
    }

}
