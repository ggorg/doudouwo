package com.ddw.services;

import com.ddw.beans.ReviewCallBackBean;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.enums.WithdrawStatusEnum;
import com.ddw.enums.WithdrawTypeEnum;
import com.ddw.util.PayApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批回调类
 */
@Service
@Transactional(readOnly = true)
public class ReviewCallBackService extends CommonService {

    @Autowired
    private LiveRadioService liveRadioService;
    @Autowired
    private ReviewRealNameService reviewRealNameService;
    @Autowired
    private ReviewGoddessService reviewGoddessService;
    @Autowired
    private ReviewPracticeService reviewPracticeService;
    @Autowired
    private ReviewBannerService reviewBannerService;
    @Autowired
    private ReviewPracticeRefundService reviewPracticeRefundService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeLiveRadio(ReviewCallBackBean rb)throws Exception{
        return liveRadioService.createLiveRadioRoom(rb.getBusinessCode(),rb.getStoreId(),rb.getReviewPO());

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeWithdraw(ReviewCallBackBean rb)throws Exception{
        if(ReviewStatusEnum.ReviewStatus1.getCode().equals(rb.getReviewPO().getDrReviewStatus())){
            Integer id=Integer.parseInt(rb.getBusinessCode());

            Map wmap=new HashMap();
            wmap.put("id",id);
            CommonSearchBean csb=new CommonSearchBean("ddw_withdraw_record",null,"t1.money,t1.incomeType,ct0.accountNoStr,ct0.accountType,ct0.accountRealName",null,null,wmap,
                    new CommonChildBean("ddw_withdraw_way","id","withdrawWayId",null));
            List<Map> list=this.getCommonMapper().selectObjects(csb);
            Map map=list.get(0);
           Integer money=(Integer) map.get("money");
           Integer incomeType=(Integer) map.get("incomeType");
           Integer userId=rb.getReviewPO().getDrProposer();
           Map setParam=new HashMap();
           setParam.put("status", WithdrawStatusEnum.withdrawStatus1.getCode());
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_withdraw_record",setParam,"id",id);
            if(res.getReCode()!=1){
                throw new GenException("更新提现记录失败");
            }
           Map searchIncome=new HashMap();
           searchIncome.put("userId",userId);
            Map setIncome=new HashMap();
            String fieldName=null;
            if(IncomeTypeEnum.IncomeType1.getCode().equals(incomeType)){
                fieldName="goddessIncome";
            }else{
                fieldName="practiceIncome";
            }
            setIncome.put(fieldName,-money);

            res=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setIncome,searchIncome,"version",new String[]{fieldName});
            if(res.getReCode()!=1){
                throw new GenException("更新钱包失败");
            }
            if(WithdrawTypeEnum.WithdrawType1.getCode().equals(map.get("accountType"))){
                String dcost=(double)money/100+"";
               res= PayApiUtil.requestAliTransfer(dcost,rb.getBusinessCode(),(String) map.get("accountNoStr"),(String) map.get("accountRealName"),IncomeTypeEnum.getName(incomeType)+"提现",IncomeTypeEnum.getName(incomeType)+"提现");
                if(res.getReCode()!=1){
                    throw new GenException("阿里转账失败");
                }
            }
        }
        return new ResponseVO(1,"成功",null);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeRealName(ReviewCallBackBean rb)throws Exception{
        return reviewRealNameService.updateReviewRealName(rb);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeGoddess(ReviewCallBackBean rb)throws Exception{
        return reviewGoddessService.updateReviewGoddess(rb);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executePractice(ReviewCallBackBean rb)throws Exception{
        return reviewPracticeService.updateReviewPractice(rb);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeBanner(ReviewCallBackBean rb)throws Exception{
        return reviewBannerService.updateReviewBanner(rb);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executePracticeRefund(ReviewCallBackBean rb)throws Exception{
        return reviewPracticeRefundService.updateReviewPracticeRefund(rb);

    }
}
