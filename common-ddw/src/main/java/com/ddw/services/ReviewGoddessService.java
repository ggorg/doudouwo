package com.ddw.services;

import com.ddw.beans.GoddessPO;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 成为女神审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewGoddessService extends CommonService {

    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findGoddessPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        return this.findPage(pageNo,condtion);
    }

    public ReviewPO getReviewById(int id)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);
    }

    public ReviewPO getReviewByCode(String drBusinessCode)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","drBusinessCode",drBusinessCode,ReviewPO.class);
    }

    /**
     * 审核后回调更新会员资料
     * @param drBusinessCode
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewGoddess(String drBusinessCode)throws Exception{
        ReviewPO reviewPO = this.getReviewByCode(drBusinessCode);
        Map setParams=new HashMap();
        setParams.put("goddessFlag",1);
        Map searchCondition=new HashMap();
        searchCondition.put("id",reviewPO.getDrProposer());
        //删除审核拒绝缓存
        CacheUtil.delete("review","goddess"+reviewPO.getDrProposer());
        //插入女神表
        GoddessPO goddessPO = new GoddessPO();
        goddessPO.setUserId(reviewPO.getDrProposer());
        goddessPO.setStoreId(reviewPO.getDrBelongToStoreId());
        goddessPO.setCreateTime(new Date());
        goddessPO.setUpdateTime(new Date());
        this.commonInsert("ddw_goddess",goddessPO);
        return this.commonUpdateByParams("ddw_userinfo",setParams,searchCondition);
    }
}
