package com.ddw.services;

import com.ddw.beans.ReviewPO;
import com.ddw.beans.ReviewRealNamePO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批
 */
@Service
public class ReviewRealNameService extends CommonService {

    @Autowired
    private CommonReviewService commonReviewService;


    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        //condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findMaterialPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType4.getCode());
        return this.findPage(pageNo,condtion);
    }

    @Cacheable(value="reviewInfo",key="#businessCode")
    public ReviewPO getReviewByBusinessCode(Object businessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",businessCode.toString());
        List list=this.commonList("ddw_review","createTime desc",null,null,conditoin);
        if(list!=null && !list.isEmpty()){
            ReviewPO rpo=new ReviewPO();
            PropertyUtils.copyProperties(rpo,list.get(0));
            return rpo;
        }
        return new ReviewPO();
    }

    public ReviewPO getReviewById(Integer id)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("id",id);
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);

    }

    public ReviewRealNamePO getReviewRealNameByCode(String drBusinessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",drBusinessCode);
        return this.commonObjectBySingleParam("ddw_review_realname","drBusinessCode",drBusinessCode,ReviewRealNamePO.class);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO editReivew(Integer id,Object businessCode,String drReviewDesc ,ReviewStatusEnum reviewStatus,ReviewBusinessTypeEnum businessType,Map userMap)throws Exception{

        Map params=new HashMap();
        params.put("drReviewDesc",drReviewDesc);
        params.put("drReviewStatus",reviewStatus.getCode());
        params.put("updateTime",new Date());
        params.put("drBusinessType",businessType.getCode());
        params.put("drReviewer", (Integer)userMap.get("id"));
        params.put("drReviewerName", (String)userMap.get("uNickName"));
        return this.commonReviewService.submitReview(params,id,businessCode.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewRealName(String drBusinessCode)throws Exception{
        Map setParams=new HashMap();
        setParams.put("status",1);
        setParams.put("updateTime",new Date());
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",drBusinessCode);
        searchCondition.put("status",0);//未审核
        this.commonUpdateByParams("ddw_review_realname",setParams,searchCondition);
        ReviewRealNamePO reviewRealNamePO = this.getReviewRealNameByCode(drBusinessCode);
        Map setParams2=new HashMap();
        setParams2.put("realName",reviewRealNamePO.getRealName());
        setParams2.put("idcard",reviewRealNamePO.getIdcard());
        setParams2.put("idcardFrontUrl",reviewRealNamePO.getIdcardFrontUrl());
        setParams2.put("idcardOppositeUrl",reviewRealNamePO.getIdcardOppositeUrl());
        Map searchCondition2=new HashMap();
        searchCondition2.put("id",reviewRealNamePO.getUserId());
        return this.commonUpdateByParams("ddw_userinfo",setParams2,searchCondition2);
    }

}
