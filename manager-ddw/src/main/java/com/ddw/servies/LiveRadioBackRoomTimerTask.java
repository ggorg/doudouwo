package com.ddw.servies;

import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.services.LiveRadioService;
import com.ddw.util.LiveRadioApiUtil;
import com.gen.common.services.CacheService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LiveRadioBackRoomTimerTask {
    private final Logger logger = Logger.getLogger(LiveRadioBackRoomTimerTask.class);

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private CacheService cacheService;

    /**
     * 黑房间定时处理，每10分钟扫描一次
     */
    //private Map<String,Integer> backRoomMap=new HashMap();
    @Scheduled(cron = "0 0/10 * * * *")
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
                    if(num==2){
                        logger.info("关闭直播间："+streamId);

                        LiveRadioApiUtil.closeLoveRadio(streamId);
                        num=num+1;
                        backRoomMap.put(streamId,num);
                    }else if(num>=3){
                        try {
                            boolean flag=LiveRadioApiUtil.closeLoveRadio(streamId);
                            if(!flag){
                                logger.info("强制更新数据库直播间："+streamId);
                                this.liveRadioService.handleLiveRadioStatus(streamId, LiveEventTypeEnum.eventType0.getCode());
                                backRoomMap.remove(streamId);
                            }
                        }catch (Exception e){
                            logger.error("定时清理黑房间更新数据库状态失败",e);
                        }
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

}
