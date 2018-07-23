package com.ddw.services;

import com.ddw.enums.BiddingStatusEnum;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BaseBiddingService extends CommonService {

    @Autowired
    private IncomeService incomeService;
    @Autowired
    private BaseConsumeRankingListService  baseConsumeRankingListService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int execute(String ub)throws Exception{

        String[] ubs=ub.split("-");
        Integer userId=Integer.parseInt(ubs[0]);
        Integer bidCode=Integer.parseInt(ubs[1]);

        Map bidMap=this.commonObjectBySingleParam("ddw_goddess_bidding","id",bidCode);
        Date endTime=(Date) bidMap.get("endTime");
        if(endTime!=null){
            removeBiddingTimer(ub);
            return -2;
        }
        Integer goddessUserId=(Integer) bidMap.get("userId");
        String groupId=(String) bidMap.get("groupId");
        Map updateMap=new HashMap();
        Date currentDate=new Date();

        updateMap.put("updateTime",currentDate);
        updateMap.put("endTime", DateUtils.addMinutes(currentDate,(Integer) bidMap.get("times")));
        updateMap.put("startTime",currentDate);

        //updateMap.put("times",(Integer) payMap.get("time"));
        this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",bidCode);
        Map map=(Map) CacheUtil.get("pay","bidding-pay-"+ub);
        Integer earnestPrice=(Integer) map.get("earnestPrice");
        this.incomeService.commonIncome(goddessUserId,earnestPrice, IncomeTypeEnum.IncomeType1, OrderTypeEnum.OrderType4,(String)map.get("earnestOrderNo"));

        this.baseConsumeRankingListService.save(userId,goddessUserId,earnestPrice,IncomeTypeEnum.IncomeType1);

        CacheUtil.delete("pay","groupId-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-finish-pay-"+bidCode+"-"+goddessUserId);
        CacheUtil.delete("pay","bidding-success-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-pay-"+ub);
        CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
        removeBiddingTimer(ub);
        return 1;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void executeNoPay(String ubg)throws Exception{
        String[] ubs=ubg.split("-");
        Integer userId=Integer.parseInt(ubs[0]);
        Integer bidCode=Integer.parseInt(ubs[1]);
        Integer goddessUserId=Integer.parseInt(ubs[2]);

        String groupId=ubs[3];
        String ub=userId+"-"+bidCode;
        Map updateMap=new HashMap();
        Date currentDate=new Date();
        updateMap.put("updateTime",currentDate);
        updateMap.put("endTime",currentDate);
        updateMap.put("status", BiddingStatusEnum.Status10.getCode());
        this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",bidCode);
        Map map=(Map) CacheUtil.get("pay","bidding-pay-"+ub);
        Integer earnestPrice=null;
        String orderNo=null;
        if(map==null){
            earnestPrice=Integer.parseInt(ubs[4]);
            orderNo=ubs[5];
        }else{
            earnestPrice=(Integer) map.get("earnestPrice");
            orderNo=(String)map.get("earnestOrderNo");
        }
        this.incomeService.commonIncome(goddessUserId,earnestPrice, IncomeTypeEnum.IncomeType1, OrderTypeEnum.OrderType4,orderNo);

        this.baseConsumeRankingListService.save(userId,goddessUserId,earnestPrice,IncomeTypeEnum.IncomeType1);

        CacheUtil.delete("pay","groupId-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-finish-pay-"+bidCode+"-"+goddessUserId);
        CacheUtil.delete("pay","bidding-success-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-pay-"+ub);
        CacheUtil.delete("pay","bidding-earnest-pay-"+ub);

    }
    public void removeBiddingTimer(String ub){
        Map m=(Map)CacheUtil.get("publicCache","bidding-make-sure-end-time");
        if(m.containsKey(ub)){
            m.remove(ub);
            CacheUtil.put("publicCache","bidding-make-sure-end-time",m);
        }

    }

}
