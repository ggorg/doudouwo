package com.ddw.servies;

import com.ddw.enums.BiddingStatusEnum;
import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.services.BaseOrderService;
import com.ddw.services.LiveRadioService;
import com.ddw.util.LiveRadioApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional(readOnly = true)
public class TimerTaskService extends CommonService {
    private final Logger logger = Logger.getLogger(TimerTaskService.class);

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private BaseOrderService baseOrderService;

    /**
     * 黑房间定时处理，每10分钟扫描一次
     */
    //private Map<String,Integer> backRoomMap=new HashMap();
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleBackRoom(){
        List<Map> list=liveRadioService.getAllActLiveRadio();
        Map<String,Integer> backRoomMap=(Map<String, Integer>) cacheService.get("backRoom");
        if(backRoomMap==null){
            backRoomMap=new HashMap();
        }
        if(list!=null){
            String streamId=null;
            Integer num=null;
            for(Map m:list){
               streamId=(String)m.get("streamid");
                num= backRoomMap.get(streamId);
                if(num==null){
                    num=0;
                }
               if(!LiveRadioApiUtil.isActLiveRoom(streamId)){
                    if(num>=2){
                        logger.info("关闭直播间："+streamId);
                        try{
                            ResponseVO vo=this.liveRadioService.handleLiveRadioStatus(streamId, LiveEventTypeEnum.eventType0.getCode());
                            if(vo.getReCode()==1){
                                LiveRadioApiUtil.closeLoveRadio(streamId);

                            }
                        }catch (Exception e){
                            logger.error("定时清理黑房间更新数据库状态失败",e);
                        }
                        backRoomMap.remove(streamId);
                    }else{
                        num=num+1;
                        backRoomMap.put(streamId,num);
                    }
                    cacheService.set("backRoom",backRoomMap);
               }else{
                   backRoomMap.remove(streamId);
                   cacheService.set("backRoom",backRoomMap);
               }
            }
        }
    }
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleFreeBid()throws Exception{
        try {
            Map searchBid=new HashMap();
            searchBid.put("endTime,is","null");
            CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","t1.createTime","t1.id,t1.bidEndTime,t1.luckyDogUserId,ct0.orderId,t1.userId,t1.groupId",null,null,searchBid,
                    new CommonChildBean("ddw_order_bidding_pay","biddingId","id",null));

            List<Map>  listMap= this.getCommonMapper().selectObjects(csb);
            if(listMap!=null){
                List orderIds=new ArrayList();
                List bids=new ArrayList();
                List<String> bidCacheList=new ArrayList();
                for(Map map:listMap){
                    Date bidEndTime=(Date) map.get("bidEndTime");
                    Integer bidUserId=(Integer) map.get("luckyUserId");

                    if( DateUtils.addMinutes(bidEndTime,30).before(new Date())){
                        orderIds.add(map.get("orderId"));
                        bids.add(map.get("id"));
                        bidCacheList.add((Integer) map.get("id")+"&"+(Integer)map.get("userId")+"&"+(String)map.get("groupId"));
                        // this.common
                    }
                }
                //        searchMap.put("id,in",orderIds.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
                if(!bids.isEmpty()){
                    Map setParam=new HashMap();
                    setParam.put("endTime",new Date());
                    setParam.put("updateTime",new Date());
                    Map searchParam=new HashMap();
                    searchParam.put("id,in",bids.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
                    this.commonUpdateByParams("ddw_goddess_bidding",setParam,searchBid);

                }
                if(!!orderIds.isEmpty()){
                    baseOrderService.baseExitOrder(orderIds);
                }
                logger.info("扫描空闲的竞价："+bidCacheList);
                if(!bidCacheList.isEmpty()){
                    bidCacheList.forEach(a->{
                        String[] str=a.split("&");
                        CacheUtil.delete("pay","groupId-"+str[0]+"-"+str[2]);
                        CacheUtil.delete("pay","bidding-cancel-"+str[0]+"-"+str[2]);
                        CacheUtil.delete("pay","bidding-finish-pay-"+str[0]+"-"+str[1]);
                        String ub=(String)CacheUtil.get("pay","bidding-success-"+str[0]+"-"+str[2]);
                        if(StringUtils.isNotBlank(ub)){
                            CacheUtil.delete("pay","bidding-pay-"+ub);
                            CacheUtil.delete("pay","bidding-success-"+str[0]+"-"+str[2]);
                        }
                    });
                }
            }
        }catch (Exception e){
            logger.error("TimerTaskService->handleFreeBid",e);
            throw new GenException("定时处理竞价失败");
        }


    }

}
