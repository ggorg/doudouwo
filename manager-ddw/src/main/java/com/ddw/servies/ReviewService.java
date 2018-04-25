package com.ddw.servies;

import com.ddw.beans.ReviewPO;
import com.ddw.enums.*;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Max;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批
 */
@Service
public class ReviewService extends CommonService {

    public Page findPage(Integer pageNo)throws Exception{

        Map condtion=new HashMap();
        //condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_review","updateTime desc",pageNo,10,condtion);
    }

    /**
     * 判断门店是否有申请审核
     * @param businessCode 业务流水号
     * @param businessType 业务类型
     * @param businessStatus 业务状态
     * @return
     */
    public boolean hasReviewFromStore(Object businessCode, ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus){
        Map condition=new HashMap();
        condition.put("drBusinessCode",businessCode.toString());
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType0.getCode());
        condition.put("drReviewerType", ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        return this.commonCountBySearchCondition("ddw_review",condition)>0?true:false;
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
    /**
     * 根据业务流水号获取审批记录
     * @param businessCode
     * @return
     * @throws Exception
     */
    public List getReviewRecord(Object businessCode)throws Exception{
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",businessCode);
       return  this.commonList("ddw_review_record","updateTime desc",null,null,searchCondition);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO editReivewFromHq(Integer id,Object businessCode,String drReviewDesc ,ReviewStatusEnum reviewStatus,ReviewBusinessTypeEnum businessType,Map userMap)throws Exception{

        Map params=new HashMap();
        params.put("drReviewDesc",drReviewDesc);
        params.put("drReviewStatus",reviewStatus.getCode());
        params.put("updateTime",new Date());
        params.put("drBusinessType",businessType.getCode());
        params.put("drReviewer", (Integer)userMap.get("id"));
        params.put("drReviewerName", (String)userMap.get("uNickName"));

        ResponseVO res= this.commonUpdateBySingleSearchParam("ddw_review",params,"id",id);
        if(res.getReCode()==1){
            ReviewPO rpo=this.getReviewById(id);

            CacheUtil.delete("reviewInfo",businessCode);
            ResponseVO resInsert=insertReviewRecord(BeanToMapUtil.beanToMap(rpo));
            if(resInsert.getReCode()!=1){
                throw new GenException("插入审批记录表失败");
            }
        }else {
            throw new GenException("审批失败");
        }
       return new ResponseVO(1,"更新审批成功",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveReviewFromStore(Object businessCode,String drApplyDesc,ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus,Map userMap)throws Exception{
        if(StringUtils.isBlank(drApplyDesc)){
            return new ResponseVO(-2,"请填写原因",null);
        }
        Map condition=new HashMap();
        condition.put("drBusinessCode",businessCode.toString());
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType0.getCode());
        condition.put("drReviewerType", ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condition.put("drApplyDesc", drApplyDesc);
        condition.put("drProposer", (Integer)userMap.get("id"));
        condition.put("drProposerName", (String)userMap.get("uNickName"));
        condition.put("createTime", new Date());

        ResponseVO<Integer> responseVO=this.commonInsertMap("ddw_review",condition);
        if(responseVO.getReCode()==1){
            Object obj=CacheUtil.get("reviewInfo",businessCode);
            if(obj!=null){
                CacheUtil.delete("reviewInfo",businessCode);
            }

            condition.put("reviewId",responseVO.getData());
            ResponseVO res=this.insertReviewRecord(condition);
            if(res.getReCode()!=1){
                throw new GenException("插入审批记录表失败");
            }
            return new ResponseVO(1,"提交申请成功",null);
        }
        return new ResponseVO(1,"提交申请失败",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertReviewRecord(Map params)throws Exception{
        Map record=new HashMap(params);
       if(record.containsKey("drApplyDesc")) record.remove("drApplyDesc");
        if(record.containsKey("drReviewDesc")) record.remove("drReviewDesc");
        if(record.containsKey("id")) record.remove("id");
       // PropertyUtils.copyProperties(record,params);
        record.put("createTime",new Date());
        return this.commonInsertMap("ddw_review_record",record);
    }

}
