package com.ddw.services;

import com.ddw.beans.PracticeRefundPO;
import com.ddw.beans.ReviewCallBackBean;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
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
public class ReviewPracticeRefundService extends CommonService {


    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findRefundPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType9.getCode());
        return this.findPage(pageNo,condtion);
    }

    public ReviewPO getReviewById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);

    }

    public PracticeRefundPO getReviewRefundByCode(String drBusinessCode)throws Exception{
        return this.commonObjectBySingleParam("ddw_practice_refund","drBusinessCode",drBusinessCode,PracticeRefundPO.class);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewPracticeRefund(ReviewCallBackBean rb)throws Exception{
        //退款成功回调,更新状态,退款
        PracticeRefundPO practiceRefundPO = this.getReviewRefundByCode(rb.getBusinessCode());
        if(rb.getReviewPO().getDrReviewStatus() == 1){
            //TODO 审批成功后调用退款接口进行退款

        }
        Map setParams=new HashMap();
        setParams.put("status",rb.getReviewPO().getDrReviewStatus());
        setParams.put("updateTime",new Date());
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",rb.getBusinessCode());
        searchCondition.put("status",0);//查询未审核
        return this.commonUpdateByParams("ddw_practice_refund",setParams,searchCondition);
    }

}
