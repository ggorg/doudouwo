package com.ddw.services;

import com.ddw.beans.BannerPO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.gen.common.services.CommonService;
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
        //condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
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
        return this.commonObjectBySingleParam("ddw_review_banner","drBusinessCode",drBusinessCode,BannerPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewBanner(String drBusinessCode)throws Exception{
        Map setParams=new HashMap();
        setParams.put("status",1);
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",drBusinessCode);
        searchCondition.put("status",0);//未审核
        return this.commonUpdateByParams("ddw_review_banner",setParams,searchCondition);
    }

}
