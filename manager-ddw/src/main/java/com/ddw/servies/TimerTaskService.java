package com.ddw.servies;

import com.ddw.beans.PracticeGamePO;
import com.ddw.beans.vo.LiveRadioListVO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.services.*;
import com.ddw.util.BiddingTimer;
import com.ddw.util.IMApiUtil;
import com.ddw.util.LiveRadioApiUtil;
import com.ddw.util.PayApiUtil;
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

import javax.annotation.PostConstruct;
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

    @Autowired
    private BaseBiddingService baseBiddingService;

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    protected IncomeService incomeService;
    @Autowired
    protected BaseConsumeRankingListService baseConsumeRankingListService;



    /**
     * 黑房间定时处理，每10分钟扫描一次
     */
    //private Map<String,Integer> backRoomMap=new HashMap();
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleBackRoom()throws Exception{
        List<Map> list=liveRadioService.getAllActLiveRadio();
        Map<String,Integer> backRoomMap=(Map<String, Integer>) cacheService.get("backRoom");
        if(backRoomMap==null){
            backRoomMap=new HashMap();
        }
        if(list!=null && !list.isEmpty() && list.size()>0){
            String streamId=null;
            Integer num=null;
            //计算pv和观看人数
            handleLiveMemberNum(list);
            //扫黑房
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

    //@Scheduled(cron = "0 0/10 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleLiveMemberNum(List<Map> list )throws Exception{
        logger.info("计算直播访问量和观看数量-》"+list);

        final List groupIds=new ArrayList();
        list.forEach(a->groupIds.add(a.get("groupId")));
        Map map=IMApiUtil.getMemberNum(groupIds);
        Set<String> keys=map.keySet();
        Map param=null;
        Integer pv=null;
        Map search=null;
        List<LiveRadioListVO> alist= (List<LiveRadioListVO>)CacheUtil.get("publicCache","appIndexGoddess");

        for(String k:keys){
            param=new HashMap();
            search=new HashMap();
            pv=(Integer) CacheUtil.get("publicCache","livePv-"+k);
            param.put("pv",pv==null?0:pv);
            if(map.containsKey(k)){
                param.put("maxGroupNum",map.get(k));
            }
            if(alist!=null){
                alist.forEach(a->{
                    Integer userid=Integer.parseInt(k.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2"));
                    if(a.getUserId().equals(userid) && LiveStatusEnum.liveStatus1.getCode().equals(a.getLiveRadioFlag())){
                        a.setViewingNum((Integer) map.get(k));
                    }
                });
            }

            search.put("groupId",k);
            this.commonOptimisticLockUpdateByParam("ddw_live_radio_space",param,search,"version");
        }
        CacheUtil.put("publicCache", "appIndexGoddess",alist);

    }
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleIncome()throws Exception{
        this.incomeService.handleGoddessIncome(null);
    }
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleFreeBid()throws Exception{
        try {
            Map searchBid=new HashMap();
            searchBid.put("endTime,is","null");

            CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","t1.createTime","t1.id,t1.bidEndTime,t1.luckyDogUserId,ct0.orderId,t1.userId,t1.groupId,t1.makeSureEndTime,t1.payEndTime,ct0.dorCost,ct0.orderNo",null,null,searchBid,
                    new CommonChildBean("ddw_order_bidding_pay","biddingId","id",null));

            List<Map>  listMap= this.getCommonMapper().selectObjects(csb);
            if(listMap!=null){
                Set orderIds=new HashSet();
                Set bids=new HashSet();
                Set<String> ubgs=new HashSet();
                Set<String> bidCacheList=new HashSet();
                Date currentDate=new Date();
                String cancel=null;
                Integer bidCode=null;
                String groupId=null;
                String orderNo=null;
                Date bidEndTime=null;
                Integer luckyUserId=null;
                Integer goddessUserId=null;
                Integer dorCost=null;
                Date makeSureEndTime=null;
                Date payEndTime=null;
                for(Map map:listMap){
                    bidEndTime=(Date) map.get("bidEndTime");
                    luckyUserId=(Integer) map.get("luckyDogUserId");
                    dorCost=(Integer) map.get("dorCost");
                    goddessUserId=(Integer)map.get("userId");
                    makeSureEndTime=(Date) map.get("makeSureEndTime");
                    payEndTime=(Date) map.get("payEndTime");
                    bidCode=(Integer) map.get("id");
                    groupId=(String)map.get("groupId");
                    orderNo=(String)map.get("orderNo");
                    if(makeSureEndTime!=null && makeSureEndTime.after(currentDate)){
                        continue;
                    }
                    //用户超时没有支付
                    cancel=(String) CacheUtil.get("pay","bidding-cancel-"+bidCode+"-"+groupId);
                    if(StringUtils.isNotBlank(cancel) || (payEndTime!=null && payEndTime.before(currentDate) && makeSureEndTime==null)){
                       StringBuilder b=new StringBuilder();
                       b.append(luckyUserId).append("-");
                       b.append(bidCode).append("-");
                       b.append(goddessUserId).append("-");
                       b.append(groupId).append("-");
                       b.append(dorCost).append("-");
                       b.append(orderNo);
                        ubgs.add(b.toString());
                       // CacheUtil.get("pay","bidding-cancel-"+(Integer) map.get("id")+"-"+(String)map.get("groupId"));
                    }else if( DateUtils.addMinutes(bidEndTime,60).before(currentDate)){

                        orderIds.add(map.get("orderId"));
                        bids.add(bidCode);
                        bidCacheList.add(bidCode+"&"+goddessUserId+"&"+groupId);
                        // this.common
                    }
                }
                //        searchMap.put("id,in",orderIds.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
                if(!bids.isEmpty()){
                    Map setParam=new HashMap();
                    setParam.put("endTime",new Date());
                    setParam.put("updateTime",new Date());
                    setParam.put("status",BiddingStatusEnum.Status10.getCode());
                    Map searchParam=new HashMap();
                    searchParam.put("id,in",bids.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
                    this.commonUpdateByParams("ddw_goddess_bidding",setParam,searchBid);

                }

                if(!ubgs.isEmpty()){
                    logger.info("用户支付超时或者取消没有支付："+ubgs);
                    for(String ubg:ubgs){
                        baseBiddingService.executeNoPay(ubg);
                    }

                }

                logger.info("退款订单ID："+orderIds);
                if(!orderIds.isEmpty()){

                    baseOrderService.baseExitOrder(new ArrayList<>(orderIds));
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
                            CacheUtil.delete("pay","bidding-earnest-pay-"+ub);

                        }
                    });
                }
            }
        }catch (Exception e){
            logger.error("TimerTaskService->handleFreeBid",e);
            throw new GenException("定时处理竞价失败");
        }


    }

    /**
     * 查询订单支付超过30分钟的,设置过期,释放代练状态为0
     */
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void expirePracticeOrder(){
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("payState",0);//未支付
        searchCondition.put("updateTime,<",new Date(new Date().getTime() - 30*60*1000));
        try {
            List<Map> list = this.commonObjectsBySearchCondition("ddw_practice_order",searchCondition);
            Map setParams = new HashMap<>();
            setParams.put("payState",2);
            Map setParams3 = new HashMap<>();
            setParams3.put("appointment",0);
            if(!list.isEmpty()){
                for (Map map:list){
                    logger.info("处理代练支付订单超时："+map.get("id"));
                    this.commonUpdateBySingleSearchParam("ddw_practice_order",setParams,"id",map.get("id"));
                    Map searchCondition2 = new HashMap<>();
                    searchCondition2.put("userId",map.get("practiceId"));
                    searchCondition2.put("gameId",map.get("gameId"));
                    PracticeGamePO practiceGamePO = this.commonObjectBySearchCondition("ddw_practice_game",searchCondition2, PracticeGamePO.class);
                    if(practiceGamePO.getAppointment()==2){
                        this.commonUpdateByParams("ddw_practice_game",setParams3,searchCondition2);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("TimerTaskService->expirePracticeOrder",e);
        }
    }

    /**
     * 每小时查询超过24小时订单计算收益
     */
    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void incomePractice(){
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("payState",1);//已支付
        searchCondition.put("incomeState",0);//未计算收益
        searchCondition.put("realityRankId,!=",0);//排除未填写实际段位的
        searchCondition.put("updateTime,<",new Date(new Date().getTime() - 24*60*60*1000));
        try {
            List<Map> list = this.commonObjectsBySearchCondition("ddw_practice_order",searchCondition);
            Map setParams = new HashMap<>();
            setParams.put("incomeState",1);//已计算收益
            for(Map map:list){
                logger.info("计算超过24小时代练已支付订单："+map.get("id"));
                logger.info("收益代练->practiceId："+map.get("practiceId")+"->realityMoney->"+map.get("realityMoney"));
                int realityMoney = (Integer)map.get("realityMoney");
                int refund = (Integer)map.get("refund");
                //用户未结算,到期订单按照已完成处理
                if(map.get("realityMoney").toString().equals("0") && !map.get("money").toString().equals("0")){
                    setParams.put("status",2);//订单状态完成
                    logger.info("用户未结算,到期订单按照已完成处理->realityMoney->"+realityMoney);
                }
                if(realityMoney > 0){//实际金额需大于0才计算收益
                    this.incomeService.commonIncome((Integer) map.get("practiceId"),realityMoney, IncomeTypeEnum.IncomeType2, OrderTypeEnum.OrderType10,map.get("orderNo").toString());
                    this.baseConsumeRankingListService.save((Integer) map.get("userId"),(Integer) map.get("practiceId"),realityMoney,IncomeTypeEnum.IncomeType2);
                }
                //更新状态为已计算收益
                this.commonUpdateBySingleSearchParam("ddw_practice_order",setParams,"id",map.get("id"));
                if(setParams.containsKey("status")){
                    setParams.remove("status");
                }
            }
        } catch (Exception e) {
            logger.error("TimerTaskService->incomePractice",e);
        }
    }

    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void init()throws Exception{
        IMApiUtil.setDdwGlobals(ddwGlobals);
        PayApiUtil.setDdwGlobals(ddwGlobals);
        handleFreeBid();
        initBiddingTimer();
    }

    public void initBiddingTimer(){
        try {
            Map<String,String> m=(Map)CacheUtil.get("publicCache","bidding-make-sure-end-time");
/*            Map searchMap=new HashMap();
            searchMap.put("makeSureEndTime,>=",new Date());
            Map listMap=(Map)this.commonObjectsBySearchCondition("ddw_goddess_bidding",searchMap);*/
            logger.info("初始化竞价定时器-》"+m);
            if(m!=null){
                Set<String> keys=m.keySet();
                for(String k:keys){
                    Timer timer=new Timer();
                    timer.schedule(new BiddingTimer(this.baseBiddingService,k),DateUtils.parseDate(m.get(k),"yyyy-MM-dd HH:mm:ss"));
                }
            }

        }catch (Exception e){
            logger.error("initBiddingTimer",e);
        }

    }

}
