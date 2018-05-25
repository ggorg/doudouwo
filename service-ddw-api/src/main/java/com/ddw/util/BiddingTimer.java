package com.ddw.util;

import com.ddw.beans.BiddingVO;
import com.ddw.services.BiddingService;
import com.gen.common.util.CacheUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BiddingTimer extends TimerTask {
    private String groupId;
    private BiddingService biddingService;
    public BiddingTimer(BiddingService biddingService,String groupId) {
        this.groupId=groupId;
        this.biddingService=biddingService;
    }

    @Override
    public void run() {
        try {
            System.out.println("开始执行竞价定时器，当前群组为："+groupId+"");
            List<BiddingVO> list=(List) CacheUtil.get("commonCache","groupId-"+groupId);

            if(list!=null){
                BiddingVO bv=list.get(list.size()-1);
                biddingService.refundBidding(groupId,bv.getUserId());
                biddingService.biddingSuccess(groupId,bv);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.cancel();
        }

    }
}
