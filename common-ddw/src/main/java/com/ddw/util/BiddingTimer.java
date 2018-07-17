package com.ddw.util;

import com.ddw.services.BaseBiddingService;
import org.apache.log4j.Logger;

import java.util.*;

public class BiddingTimer extends TimerTask {
    private final Logger logger = Logger.getLogger(BiddingTimer.class);

    private String ub;

    private BaseBiddingService baseBiddingService;

    public BiddingTimer(BaseBiddingService a,String ub) {
        this.ub=ub;
        this.baseBiddingService=a;

    }

    @Override
    public void run() {
        try {
            logger.info("开始执行竞价定时器，当前user-bidcode为："+ub+"");
            int ren=baseBiddingService.execute(ub);
            logger.info("结束执行竞价定时器，当前ren为："+ren+"");

        }catch (Exception e){
            logger.error("BiddingTimer->run",e);
        }finally {
            this.cancel();
        }

    }
}
