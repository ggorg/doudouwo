package com.ddw.services;

import com.ddw.beans.PracticePO;
import com.ddw.beans.ReviewPO;
import com.ddw.beans.ReviewPracticePO;
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
import java.util.List;
import java.util.Map;

/**
 * 成为代练审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewPracticeService extends CommonService {

    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findPracticePageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
        return this.findPage(pageNo,condtion);
    }

    public ReviewPO getReviewById(int id)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);
    }

    public ReviewPO getReviewByCode(String drBusinessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",drBusinessCode);
        return this.commonObjectBySingleParam("ddw_review","drBusinessCode",drBusinessCode,ReviewPO.class);

    }

    public ReviewPracticePO getReviewPracticeByCode(String drBusinessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",drBusinessCode);
        return this.commonObjectBySingleParam("ddw_review_practice","drBusinessCode",drBusinessCode,ReviewPracticePO.class);

    }

    /**
     * 审核后回调更新会员资料
     * @param drBusinessCode
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewPractice(String drBusinessCode)throws Exception{
        ReviewPO reviewPO = this.getReviewByCode(drBusinessCode);
        Map setParams=new HashMap();
        setParams.put("practiceGradeId",1);
        Map searchCondition=new HashMap();
        searchCondition.put("id",reviewPO.getDrProposer());
        //删除审核拒绝缓存
        CacheUtil.delete("review","practice"+reviewPO.getDrProposer());
        //插入代练表
        PracticePO practicePO = new PracticePO();
        practicePO.setUserId(reviewPO.getDrProposer());
        practicePO.setStoreId(reviewPO.getDrBelongToStoreId());
        practicePO.setCreateTime(new Date());
        practicePO.setUpdateTime(new Date());
        this.commonInsert("ddw_practice",practicePO);
        return this.commonUpdateByParams("ddw_userinfo",setParams,searchCondition);
    }

    public List<Map> gameList(){
        return this.commonList("ddw_game",null,1,9999,new HashMap<>());
    }
    public List<Map> rankList(){
        return this.commonList("ddw_rank",null,1,9999,new HashMap<>());
    }
}
