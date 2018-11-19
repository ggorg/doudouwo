package com.ddw.services;

import com.ddw.beans.ReviewCallBackBean;
import com.ddw.enums.*;
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

import java.util.Date;
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
    public ResponseVO executeGoodFriendPlay(ReviewCallBackBean rb)throws Exception{
        if(ReviewStatusEnum.ReviewStatus1.getCode().equals(rb.getReviewPO().getDrReviewStatus())){
            Integer roomId=Integer.parseInt(rb.getBusinessCode());
            Map search=new HashMap();
            search.put("id",roomId);
            CommonSearchBean csb=new CommonSearchBean("ddw_goodfriendplay_room",null,"ct0.id,ct0.tableNumber,ct0.status",null,null,new HashMap(),new CommonChildBean("ddw_goodfriendplay_tables","id","tableCode",null));
            List<Map> data=this.getCommonMapper().selectObjects(csb);
            if(data==null || data.isEmpty()){
                return new ResponseVO(-2,"业务参数异常",null);
            }
            Map map=data.get(0);
            if(TableStatusEnum.status1.getCode().equals((Integer) map.get("status"))){
                return new ResponseVO(-2,map.get("tableNumber").toString()+"已被使用",null);
            }
            Map roomUpdate=new HashMap();
            roomUpdate.put("status",GoodFriendPlayRoomStatusEnum.status1.getCode());
            roomUpdate.put("updateTime",new Date());
            roomUpdate.put("startTime",new Date());
            this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_room",roomUpdate,"id",roomId);
            Map ts=new HashMap();
            ts.put("id",map.get("id"));
            this.commonOptimisticLockUpdateByParam("ddw_goodfriendplay_tables",roomUpdate,ts,"version");
            return new ResponseVO(1,"成功",null);
        }else{
            Map roomUpdate=new HashMap();
            roomUpdate.put("status",GoodFriendPlayRoomStatusEnum.status21.getCode());
            roomUpdate.put("updateTime",new Date());
            this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_room",roomUpdate,"id",Integer.parseInt(rb.getBusinessCode()));
            return new ResponseVO(1,"成功",null);

        }


    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeWithdraw(ReviewCallBackBean rb)throws Exception{
        if(ReviewStatusEnum.ReviewStatus1.getCode().equals(rb.getReviewPO().getDrReviewStatus())){
            Integer id=Integer.parseInt(rb.getBusinessCode());
            Map map=this.commonObjectBySingleParam("ddw_withdraw_record","id",id);

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
            if(res.getReCode()==-2){
                throw new GenException("提现金额不足");
            }else if(res.getReCode()!=1){
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
