package com.ddw.services;

import com.ddw.beans.ReviewCallBackBean;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewCallBackEnum;
import com.ddw.util.CallBackInvokeUtil;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class CommonReviewService extends CommonService {

    @Autowired
    private ReviewCallBackService reviewCallBackService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertReviewRecord(Map params)throws Exception{
        Map record=new HashMap(params);
        if(record.containsKey("drApplyDesc")) record.remove("drApplyDesc");
        if(record.containsKey("drReviewDesc")) record.remove("drReviewDesc");
        if(record.containsKey("id")) record.remove("id");
        // PropertyUtils.copyPropertiesrecord,params);
        record.put("createTime",new Date());
        return this.commonInsertMap("ddw_review_record",record);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO submitReview(Map params,Integer reviewId,String businessCode)throws Exception{
        ResponseVO res= this.commonUpdateBySingleSearchParam("ddw_review",params,"id",reviewId);
        if(res.getReCode()==1){
            Map conditoin=new HashMap();
            conditoin.put("id",reviewId);
            ReviewPO rpo=this.commonObjectBySingleParam("ddw_review","id",reviewId,ReviewPO.class);

            CacheUtil.delete("reviewInfo",rpo.getDrBusinessCode());
            ResponseVO resInsert=insertReviewRecord(BeanToMapUtil.beanToMap(rpo,false));
            if(resInsert.getReCode()!=1){
                throw new GenException("插入审批记录表失败");
            }
            ReviewCallBackBean rb=new ReviewCallBackBean();
            rb.setBusinessCode(rpo.getDrBusinessCode());
            rb.setStoreId(rpo.getDrBelongToStoreId());
            rb.setReviewPO(rpo);
            //回调处理
            if(StringUtils.isNotBlank(ReviewCallBackEnum.getName(rpo.getDrBusinessStatus()))){
                ResponseVO callBackRes=(ResponseVO) CallBackInvokeUtil.reviewInvoke(reviewCallBackService, ReviewCallBackEnum.getName(rpo.getDrBusinessStatus()), rb);
                if(callBackRes.getReCode()!=1){
                    throw new GenException("审批失败,"+callBackRes.getReMsg());
                }
            }

        }else {
            throw new GenException("审批失败");
        }
        return new ResponseVO(1,"审批成功",null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO submitAppl(Object data)throws Exception{
        ResponseVO<Integer> responseVO=null;
        String businessCode=null;
        Map mdata=null;
        if(data instanceof Map){
            mdata=(Map)data;
            businessCode=(String)mdata.get("drBusinessCode");
        }else{
            mdata=BeanToMapUtil.beanToMap(data,false);
            businessCode=(String)mdata.get("drBusinessCode");
        }
        responseVO=this.commonInsertMap("ddw_review",mdata);
        if(responseVO.getReCode()==1){
            Object obj= CacheUtil.get("reviewInfo",businessCode);
            if(obj!=null){
                CacheUtil.delete("reviewInfo",businessCode);
            }

            mdata.put("reviewId",responseVO.getData());
            ResponseVO res=insertReviewRecord(mdata);
            if(res.getReCode()!=1){
                throw new GenException("插入审批记录表失败");
            }
            return new ResponseVO(1,"提交申请成功",null);
        }
        return new ResponseVO(-2,"提交申请失败",null);

    }
}
