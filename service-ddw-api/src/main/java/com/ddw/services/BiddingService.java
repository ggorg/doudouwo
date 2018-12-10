package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.beans.vo.BiddingVO;
import com.ddw.controller.AppOrderController;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.BiddingTimer;
import com.ddw.util.IMApiUtil;
import com.ddw.util.LiveRadioConstant;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 竞价
 */
@Service
@Transactional(readOnly = true)
public class BiddingService extends CommonService {
    private final Logger logger = Logger.getLogger(BiddingService.class);

    @Value("${bidding.bidEndTime.minute:15}")
    private Integer bidEndTimeMinute;
    @Value("${bidding.payTime.minute:30}")
    private Integer payTimeMinute;

    @Autowired
    private LiveRadioService liveRadioService;
    @Autowired
    private LiveRadioClientService liveRadioClientService;

    @Autowired
    private BaseDynService baseDynService;

   // @Autowired
   // private CacheService cacheService;
    @Autowired
    private ReviewGoddessService goddessService;

    @Autowired
    private PayCenterService payCenterService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ConsumeRankingListService consumeRankingListService;

    @Autowired
    private BaseBiddingService baseBiddingService;
    private String getSurplusTimeStr(Date date){

        long l=date.getTime()-System.currentTimeMillis();
        BigDecimal v= BigDecimal.valueOf(l).divide(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_DOWN);
        long part=(long)v.doubleValue();
        BigDecimal point=v.subtract(BigDecimal.valueOf(part)).multiply(BigDecimal.valueOf(60));
        StringBuilder builder=new StringBuilder();
        builder.append(part).append("分").append(point.intValue()).append("秒");
        return builder.toString();

    }

    private ResponseVO saveBidding(String token,String groupId,Date bidTime)throws Exception{
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");
        Map insertMap=new HashMap();
        insertMap.put("userId",Integer.parseInt(useridStr));
        insertMap.put("createTime",new Date());
        insertMap.put("updateTime",new Date());
        insertMap.put("version",1);
        insertMap.put("groupId",groupId);
        insertMap.put("bidEndTime",bidTime);
        return this.commonInsertMap("ddw_goddess_bidding",insertMap);
    }
    public ResponseApiVO  biddingSuccess(String groupId,BiddingVO bv)throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("groupId",groupId);
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","t1.createTime desc","t1.id,t1.userId,ct0.headImgUrl,ct0.nickName goddessName",0,1,searchMap,cb);
        List<Map> bidList=this.getCommonMapper().selectObjects(csb);
        Map map=bidList.get(0);
        //map.get("id");
        Map updateMap=new HashMap();
       // updateMap.put("endTime",DateUtils.addMinutes(new Date(),bv.getTime()));
        Date payCountDown=DateUtils.addMinutes(new Date(),this.payTimeMinute);
        updateMap.put("luckyDogUserId",bv.getUserId());
        updateMap.put("luckyDogUserName",bv.getUserName());
        updateMap.put("price",bv.getPrice());
        updateMap.put("updateTime",new Date());
        updateMap.put("times",bv.getTime());
        updateMap.put("payEndTime",payCountDown);
        this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",map.get("id"));
        Map m=new HashMap();
        m.put("msg","恭喜"+bv.getUserName()+"以"+Double.parseDouble(bv.getPrice())/100+"元竞价成功");
        m.put("openid",bv.getOpenId());
        m.put("bidPrice",bv.getPrice());
        m.put("time",bv.getTime());
        m.put("name",bv.getUserName());


        Map searchOrderBidMap=new HashMap();
        searchOrderBidMap.put("biddingId",map.get("id"));
        searchOrderBidMap.put("creater",bv.getUserId());
        Map orderBidding=this.commonObjectBySearchCondition("ddw_order_bidding_pay",searchOrderBidMap);
        if(orderBidding!=null && orderBidding.containsKey("dorCost")){
           Integer dorCost=(Integer) orderBidding.get("dorCost");
            String orderNo=(String) orderBidding.get("orderNo");
            m.put("needPayPrice",Integer.parseInt(bv.getPrice())-dorCost+"");
            m.put("code",map.get("id"));


            m.put("headImgUrl",map.get("headImgUrl"));
            m.put("goddessName",map.get("goddessName"));

            m.put("payEndTime",DateFormatUtils.format(payCountDown,"yyyy-MM-dd HH:mm:ss"));
            Map retMap=new HashMap(m);
            m.put("goddessUserId",map.get("userId"));
            m.put("earnestPrice",dorCost);
            m.put("earnestOrderNo",orderNo);
            /*BiddingPayVO payVO=new BiddingPayVO();
            PropertyUtils.copyProperties(payVO,m);
            payVO.setBiddingId((Integer)map.get("id"));
            Map payMap=new HashMap();
            payMap.put(bv.getUserId(),payVO);*/

            CacheUtil.put("pay","bidding-pay-"+bv.getUserId()+"-"+map.get("id"),m);
            CacheUtil.put("pay","bidding-success-"+map.get("id")+"-"+groupId,bv.getUserId()+"-"+map.get("id"));
           // CacheUtil.delete("pay","groupId-"+groupId);


           return  new ResponseApiVO(1,"成功",retMap);
        }else{
            throw new GenException("更新竞价表失败");
        }

    }
    public ResponseApiVO refundBidding(String groupId,Integer filterUserId)throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("groupId",groupId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        Map map=bidList.get(0);
        Map searchOrderBidMap=new HashMap();
        searchOrderBidMap.put("biddingId",map.get("id"));
        searchOrderBidMap.put("creater,!=",filterUserId);
        List<Map> list=this.commonList("ddw_order_bidding_pay",null,"t1.orderId",null,null,searchOrderBidMap);
        List orders=new ArrayList();
        if(list!=null && !list.isEmpty()){
            list.forEach(a->orders.add(a.get("orderId")));
        }
        ResponseApiVO apiVo=payCenterService.exitOrder(orders);
       // apiVo.setData(filterUserMap);
        return apiVo;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO cancelBidPayByGoddess(String token)throws Exception{
        /*String streamId=TokenUtil.getStreamId(token);
        if(StringUtils.isBlank(streamId)){
            return new ResponseApiVO(-2,"取消失败",null);
        }

        String groupId=streamId.replaceFirst("[0-9]+_","");
        GroupIdDTO groupIdDTO=new GroupIdDTO();
        groupIdDTO.setGroupId(groupId);
        Map bidMap=this.getCurrentBidMapNoBidEndTime(groupId);*/
        Map bidMap= this.getCurrentBidByUserId(TokenUtil.getUserId(token));
        if(bidMap==null){
            return new ResponseApiVO(-2,"取消失败",null);
        }
        Date endTime=(Date)bidMap.get("endTime");
        if(endTime!=null){
            if(endTime.after(new Date())){
                return new ResponseApiVO(-2,"取消不了，约玩中",null);

            }else if(endTime.before(new Date())){
                return new ResponseApiVO(-2,"取消不了，约玩已结束",null);

            }
        }
        String groupId=(String)bidMap.get("groupId");
        Integer bidCode=(Integer)bidMap.get("id");
        String ub=(String)CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
        //Map payMap=(Map) CacheUtil.get("pay","bidding-pay-"+ub);
        String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidCode+"-"+TokenUtil.getUserId(token));
        if(StringUtils.isNotBlank(retStr)){
            return new ResponseApiVO(-2,"没法取消，用户已经支付金额",null);

        }
        Map setMap=new HashMap();
        Date current=new Date();
        setMap.put("endTime",current);
        if(!bidMap.containsKey("startTime") ||  bidMap.get("startTime")==null){
            setMap.put("startTime",current);

        }
        setMap.put("status",BiddingStatusEnum.Status10.getCode());
        ResponseVO resVo=this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",setMap,"id",bidCode);
        if(resVo.getReCode()!=1){
            return new ResponseApiVO(-2,"取消失败",null);
        }

        CacheUtil.delete("pay","bidding-success-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-pay-"+ub);
        CacheUtil.delete("pay","groupId-"+bidCode+"-"+groupId);
        CacheUtil.delete("pay","bidding-cancel-"+groupId);
        CacheUtil.delete("pay","bidding-earnest-pay"+ub);



        return new ResponseApiVO(1,"成功",null);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO cancelBidPayByUserId(String token,BiddingUserCancelPayDTO dto,boolean isUser)throws Exception{
        String groupId=TokenUtil.getGroupId(token);
        if(StringUtils.isBlank(groupId) && (dto.getBidCode()==null || dto.getBidCode()<=0)){
            return new ResponseApiVO(-2,"参数异常",null);
        }

        Map searchMap=new HashMap();
        searchMap.put("luckyDogUserId",TokenUtil.getUserId(token));
        if(dto!=null && dto.getBidCode()!=null && dto.getBidCode()>0){
            searchMap.put("id",dto.getBidCode());
        }else  if(StringUtils.isNotBlank(groupId)){
            searchMap.put("groupId",groupId);
        }
        Map bidMap=this.getCurrentBidMapBySearchMap(searchMap);
        if(bidMap==null){
            return new ResponseApiVO(-2,"取消失败",null);
        }
        Date endTime=(Date)bidMap.get("endTime");
        if(endTime!=null){
            if(endTime.after(new Date())){
                return new ResponseApiVO(-2,"取消不了，约玩中",null);

            }else if(endTime.before(new Date())){
                return new ResponseApiVO(-2,"取消不了，约玩已结束",null);

            }
        }
        Integer bidCode=(Integer) bidMap.get("id");
        groupId=(String) bidMap.get("groupId");
        String ub=(String)CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
        if(StringUtils.isBlank(ub)){
            return new ResponseApiVO(-3,"操作失败",null);
        }else if(isUser && !ub.startsWith(TokenUtil.getUserId(token)+"-")){
            return new ResponseApiVO(-2,"抱歉，当前用户没有权限取消",null);

        }
        CacheUtil.put("pay","bidding-cancel-"+bidCode+"-"+groupId,"true");
        return new ResponseApiVO(1,"成功",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO makeSureFinishPay(String token)throws Exception{


       /*String streamId=TokenUtil.getStreamId(token);
       if(StringUtils.isBlank(streamId)){

           return new ResponseApiVO(-2,"直播房间不存在",null);
       }
       String groupId=streamId.replaceFirst("[0-9]+_","");
        Map bidMap=this.getCurrentBidMapNoBidEndTime(groupId);
        if(bidMap==null){
            return new ResponseApiVO(-2,"确认失败",null);
        }*/
        Map bidMap=getCurrentBidByUserId(TokenUtil.getUserId(token));
        if(bidMap==null){
            return new ResponseApiVO(-2,"确认失败",null);
        }
        Date endTime=(Date)bidMap.get("endTime");
        if(endTime!=null){
            if(endTime.after(new Date())){
                return new ResponseApiVO(-2,"确认失败，约玩中",null);

            }else if(endTime.before(new Date())){
                return new ResponseApiVO(-2,"确认失败，约玩已结束",null);

            }
        }
        String groupId=(String)bidMap.get("groupId");
        Integer bidCode=(Integer)bidMap.get("id");
        Integer luckyDogUserId=(Integer)bidMap.get("luckyDogUserId");
       String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidCode+"-"+TokenUtil.getUserId(token));
       if(StringUtils.isBlank(retStr)){
           return new ResponseApiVO(-2,"用户还没有支付",null);

       }
        String ub=(String)CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
        if(StringUtils.isBlank(ub)){
            return new ResponseApiVO(-2,"确认失败，没有竞价用户信息",null);

        }
        Map updateMap=new HashMap();
       Date currentDate=new Date();
       updateMap.put("endTime",DateUtils.addMinutes(currentDate,(Integer) bidMap.get("times")));
       updateMap.put("startTime",currentDate);
       //updateMap.put("times",(Integer) payMap.get("time"));
       this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",bidCode);
        baseDynService.saveBideDyn(bidMap,(Date)updateMap.get("endTime"));
        Map map=(Map) CacheUtil.get("pay","bidding-pay-"+ub);
       Integer earnestPrice=(Integer) map.get("earnestPrice");
      // this.incomeService.commonIncome(TokenUtil.getUserId(token),earnestPrice, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType4,(String)map.get("earnestOrderNo"));

        this.consumeRankingListService.save(luckyDogUserId,TokenUtil.getUserId(token),earnestPrice,IncomeTypeEnum.IncomeType1);

       CacheUtil.delete("pay","groupId-"+bidCode+"-"+groupId);
       CacheUtil.delete("pay","bidding-finish-pay-"+bidCode+"-"+TokenUtil.getUserId(token));
       CacheUtil.delete("pay","bidding-success-"+bidCode+"-"+groupId);
       CacheUtil.delete("pay","bidding-pay-"+ub);
       CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
       return new ResponseApiVO(1,"成功",null);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO chooseBidding(String openId,String token)throws Exception{
       // GoddessPO  gpo=this.goddessService.getAppointment(TokenUtil.getGroupId(token));


      // LiveRadioPO po=this.liveRadioService.getLiveRadio(TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
        /*Map bidMap=this.getCurrentBidMapNoBidEndTime(po.getGroupId());
        if(bidMap==null){
            return new ResponseApiVO(-2,"选择失败",null);
        }
        Date endTime=(Date)bidMap.get("endTime");
        if(endTime!=null){
            if(endTime.after(new Date())){
                return new ResponseApiVO(-2,"选择失败，约玩中",null);

            }else if(endTime.before(new Date())){
                return new ResponseApiVO(-2,"选择失败，约玩已结束",null);

            }
        }*/
        Map bidMap=this.getCurrentBidByUserId(TokenUtil.getUserId(token));
        if(bidMap==null){
            return new ResponseApiVO(-2,"选择失败",null);
        }
        Date endTime=(Date)bidMap.get("endTime");
        if(endTime!=null) {
            if (endTime.after(new Date())) {
                return new ResponseApiVO(-2, "选择失败，约玩中", null);

            } else if (endTime.before(new Date())) {
                return new ResponseApiVO(-2, "选择失败，约玩已结束", null);

            }
        }
        Integer bidCode=(Integer) bidMap.get("id");
        String groupId=(String) bidMap.get("groupId");
        String ub=(String)CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
        if(ub!=null){
            Map m=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
            m.remove("goddessUserId");
            return new ResponseApiVO(1,"成功",m);

        }
        List<BiddingVO> list=(List)CacheUtil.get("pay","groupId-"+bidCode+"-"+groupId);
        ResponseApiVO vo=null;
        ResponseApiVO refundVo=null;
        ResponseVO inVo=null;
        Set<String> s=new HashSet();
        if(list!=null){
            BiddingVO bv=null;
            for(int i=list.size()-1;i>-1;i--){
                bv=list.get(i);
                if(bv.getOpenId().equals(openId.trim())){
                    if(vo!=null && refundVo!=null){
                        continue;
                    }
                    vo=biddingSuccess(groupId,bv);
                    if(vo.getReCode()!=1){
                        throw new GenException("选择失败");
                    }
                    refundVo=this.refundBidding(groupId,bv.getUserId());
                    if(refundVo.getReCode()!=1){
                        throw new GenException("退款失败");
                    }
                    //break;
                }else{
                    s.add(bv.getOpenId().trim());

                }
            }
            if(vo!=null && refundVo!=null){
                logger.info("退定金用户："+s+",不用退定金的用户："+openId+",BiddingVOList:"+list);
                Date payCountDown=DateUtils.addMinutes(new Date(),this.payTimeMinute);

                IMApiUtil.pushSimpleChat(LiveRadioConstant.ACCOUNT_TONG_ZHI,openId,"你竞价预约的主播已经选择了你，请您在30分钟内，在我的订单->预约女神订单查看并支付完成，并到店陪她一起玩耍吧！时间到计时："+this.getSurplusTimeStr(payCountDown));

                for(String k:s){
                    if(!k.equals(openId.trim())){
                        IMApiUtil.pushSimpleChat(LiveRadioConstant.ACCOUNT_TONG_ZHI,k,"由于主播没有选择你，你竞价主播的定金已经退回，请在退款订单查看");

                    }
                }
                return vo;
            }


        }
        return new ResponseApiVO(-2,"选择失败",null);

    }
    public ResponseApiVO getCurrentMaxPrice(String token)throws Exception{
        String groupId=TokenUtil.getGroupId(token);
        Integer storeId=TokenUtil.getStoreId(token);
        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请先选择直播房间",null);

        }

        if(storeId==null){
            return new ResponseApiVO(-2,"请先选择门店",null);
        }
        if(!groupId.matches("^[0-9]+_[0-9]+_[0-9]{12}$")){
            return new ResponseApiVO(-2,"群组ID格式异常",null);

        }
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");

        GoddessPO gpo=this.goddessService.getAppointment(Integer.parseInt(useridStr),storeId);
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"约玩已关闭",null);

        }

        //查询竞价表是否有记录，若有或者未过期的就表示陪玩中，否则就是空闲
        Map bidMap=getCurrentBidByUserId(gpo.getUserId());
        if(bidMap!=null && !bidMap.isEmpty()){
            Integer bidCode=(Integer) bidMap.get("id");
            Date endTime=(Date) bidMap.get("endTime");
            groupId=(String) bidMap.get("groupId");
            String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidCode+"-"+gpo.getUserId());
            if(StringUtils.isNotBlank(retStr)){
                Map map=new HashMap();
                map.put("status",3);
                map.put("statusMsg","用户已支付");
                map.put("orderNo",retStr);
                return new ResponseApiVO(1,"成功",map);

            }
            String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
            if(userIdBidId!=null ){
                Map payMap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
                Map map=new HashMap();
                map.put("status",2);
                map.put("statusMsg","支付中");
                map.put("payEndTime",payMap.get("payEndTime"));
               // m.put("payCountDown",this.getSurplusTimeStr(payCountDown));

              //  m.put("payEndTime",DateFormatUtils.format(payCountDown,"yyyy-MM-dd HH:mm:ss"));
                return new ResponseApiVO(6,"本轮竞价已经结束，"+payMap.get("msg"),map);
            }

            if(endTime!=null && endTime.after(new Date())){
                Map voMap=new HashMap();
                voMap.put("endTime",DateFormatUtils.format(endTime,"yyyy-MM-dd HH:mm:ss"));
                voMap.put("startTime",DateFormatUtils.format((Date)bidMap.get("startTime"),"yyyy-MM-dd HH:mm:ss"));

                voMap.put("status",5);
                voMap.put("statusMsg","约玩中");
                return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(endTime)+"后",voMap);
            }
            List<BiddingVO> list=(List)CacheUtil.get("pay","groupId-"+bidCode+"-"+groupId);
            if(list!=null && !list.isEmpty()){
                list.remove("handling");
                if(list.size()>0){
                    Map map=new HashMap();
                    map.put("bidEndTime",list.get(0).getBidEndTime());
                    map.put("status",1);
                    map.put("statusMsg","竞价中");
                    map.put("list",list);
                    return new ResponseApiVO(1,"成功",map);
                }

            }
        }
        // BiddingVO vo=new BiddingVO();
        // vo.setPrice(bidPrice+"");
        Map map=new HashMap();
        map.put("status",6);
        map.put("statusMsg","空闲中");
        map.put("price", gpo.getBidPrice());
        return new ResponseApiVO(2,"目前还没人竞价,起投金额为："+(double) gpo.getBidPrice()/100+"元",map);

       /* if(bidList!=null && !bidList.isEmpty()){
            Map dataMap=(Map)bidList.get(0);
            Map voMap=new HashMap();
            voMap.put("endTime",DateFormatUtils.format((Date)dataMap.get("endTime"),"yyyy-MM-dd HH:mm:ss"));
            voMap.put("status",5);
            voMap.put("statusMsg","约玩中");
            return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(dataMap)+"后",voMap);
        }
        //获取缓存中的最高竞价
        List<BiddingVO> list=(List)cacheService.get("groupId-"+TokenUtil.getBidCode()+groupId);
        if(list==null || list.isEmpty()){

            Integer bidPrice=this.commonSingleFieldBySingleSearchParam("ddw_goddess","userId",Integer.parseInt(useridStr),"bidPrice",Integer.class);
           // BiddingVO vo=new BiddingVO();
           // vo.setPrice(bidPrice+"");
            Map map=new HashMap();
            map.put("status",6);
            map.put("statusMsg","空闲中");
            map.put("price",bidPrice);
            return new ResponseApiVO(2,"目前还没人竞价,起投金额为："+(double)bidPrice/100+"元",map);
        }
        list.remove("handling");
        Map map=new HashMap();
        map.put("bidEndTime",list.get(0).getBidEndTime());
        map.put("status",1);
        map.put("statusMsg","竞价中");
        map.put("list",list);
        return new ResponseApiVO(1,"成功",map);*/
    }
    public Map getBidMapById(Integer bidId){
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("id",bidId);
        searchMap.put("bidEndTime,>=",new Date());
        return this.commonObjectBySearchCondition("ddw_goddess_bidding",searchMap);

    }
    public Map getBidMapById2(Integer bidId){
        Map searchMap=new HashMap();
        searchMap.put("endTime,>=",new Date());
        searchMap.put("id",bidId);
        return this.commonObjectBySearchCondition("ddw_goddess_bidding",searchMap);

    }
    public Map getCurrentBidMap(String groupId){
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("groupId",groupId);
        searchMap.put("bidEndTime,>=",new Date());
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){
            return bidList.get(0);
        }
        return null;
    }
    public Map getCurrentBidByUserId(Integer userId){
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("userId",userId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){
            return bidList.get(0);
        }
        return null;
    }
    public Map getCurrentBidMapNoBidEndTime(String groupId){
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("groupId",groupId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){
            return bidList.get(0);
        }
        return null;
    }
    public Map getCurrentBidMapBySearchMap(Map searchMap){

        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){
            return bidList.get(0);
        }
        return null;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO putPrice(String token,BiddingDTO dto)throws Exception{
        String groupId=TokenUtil.getGroupId(token);
        Integer storeId=TokenUtil.getStoreId(token);

        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请先选择一个直播房间",null);
        }
        if(storeId==null){
            return new ResponseApiVO(-2,"请先选择门店",null);

        }
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");
        GoddessPO gpo=this.goddessService.getAppointment(Integer.parseInt(useridStr),storeId);
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"抱歉约玩已被关闭，没法竞价",null);

        }
        boolean liveFlag=this.liveRadioClientService.getCurrentLiveRadioFlagByGroupId(groupId);
        if(!liveFlag){
            return new ResponseApiVO(-10,"当前直播状态没法竞价",null);
        }

      //  Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
       // searchMap.put("groupId",groupId);
        //searchMap.put("bidEndTime,>=",new Date());
        Map bidMap=this.getCurrentBidByUserId(Integer.parseInt(useridStr));
        //List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        Integer bidId=null;
        boolean flag=false;
        Date bidEndTime=null;
        if(bidMap==null ){
            bidEndTime=DateUtils.addMinutes(new Date(),this.bidEndTimeMinute);
            ResponseVO<Integer> res=this.saveBidding(token,groupId,bidEndTime);
            if(res.getReCode()!=1){
                throw new GenException("创建竞价记录失败");
            }
            bidId=res.getData();
            flag=true;
           // return new ResponseApiVO(-2,"竞价时间已经结束，请",null);
        }else{
            bidId=(Integer) bidMap.get("id");
            String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+bidId+"-"+groupId);
            if(userIdBidId!=null ){
                logger.info("putPrice->本轮竞价已经结束->"+userIdBidId);
                Map payMap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
                return new ResponseApiVO(6,"本轮竞价已经结束，"+payMap.get("msg"),null);
            }
            Date currentDate=new Date();

            bidEndTime=(Date) bidMap.get("bidEndTime");
            Date endTime=(Date) bidMap.get("endTime");
            if(bidEndTime.after(currentDate) && endTime==null){
                Map earnestMap=new HashMap();
               earnestMap.put("creater",TokenUtil.getUserId(token));
               earnestMap.put("biddingId",bidId);
                logger.info("putPrice->(bidEndTime.after(currentDate) && endTime==null)->bidId:"+bidId+"+bidEndTime:"+DateFormatUtils.format(bidEndTime,"yyyy-MM-dd HH:mm:ss")+",endTime:"+endTime+",userId:"+TokenUtil.getUserId(token));


                //List list=this.commonList("","createTime desc",null,null,earnestMap);
                Map orderSearch=new HashMap();
                orderSearch.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
               CommonSearchBean csb=new CommonSearchBean("ddw_order_bidding_pay","t1.createTime desc","ct0.*",null,null,earnestMap,
                       new CommonChildBean("ddw_order","id","orderId",orderSearch));
                List list=this.getCommonMapper().selectObjects(csb);
                logger.info("putPrice->判断定金有没有交->bidId:"+bidId+"，order:"+list);
                if(list==null|| list.isEmpty()){
                    flag=true;
                 }

           }else if(endTime!=null && endTime.after(currentDate)){
                logger.info("putPrice->endTime!=null && endTime.after(currentDate)->陪玩中，空闲时间约在->bidId:"+bidId+"，endTime:"+endTime);

                Map voMap=new HashMap();
                voMap.put("endTime",DateFormatUtils.format((Date)bidMap.get("endTime"),"yyyy-MM-dd HH:mm:ss"));
                return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(endTime)+"后",voMap);

            }else if((endTime!=null && endTime.before(currentDate)) || (userIdBidId==null && bidEndTime.before(currentDate))){
                logger.info("putPrice->(endTime!=null && endTime.before(currentDate)) || (userIdBidId==null && bidEndTime.before(currentDate))->创建竞价记录->bidId:"+bidId+"，bidEndTime:"+bidEndTime+"，endTime:"+endTime);

                bidEndTime=DateUtils.addMinutes(new Date(),this.bidEndTimeMinute);
                ResponseVO<Integer> res=this.saveBidding(token,groupId,bidEndTime);
                if(res.getReCode()!=1){
                    throw new GenException("创建竞价记录失败");
                }
                bidId=res.getData();
                flag=true;
            }


        }

        if(flag){
            if(gpo.getEarnest()==null || gpo.getEarnest()<=0){
                throw new GenException("请联系管理员配置定金额度");
            }
            /*BiddingEarnestVO be=new BiddingEarnestVO();
            be.setCode(bidId);
            be.setPrice(gpo.getEarnest());*/
            Map earnestMap=new HashMap();
            earnestMap.put("code",bidId);
            earnestMap.put("price",gpo.getEarnest());
            CacheUtil.put("pay","bidding-earnest-pay-"+TokenUtil.getUserId(token)+"-"+bidId,gpo.getEarnest()+"-"+gpo.getUserId());
            return new ResponseApiVO(4,"请完成定金支付",earnestMap);
        }

        if(dto.getPrice()==null ||  dto.getPrice()<=0 ){
            return new ResponseApiVO(-2,"请输入有效的金额",null);
        }

        if(dto.getTime()==null ||  dto.getTime()<=0 ){
            return new ResponseApiVO(-2,"请输入有效的时间",null);
        }
        List list=null;
        for(int i=1;i<=5;i++){
            list=(List)CacheUtil.get("pay","groupId-"+bidId+"-"+groupId);
            if(list==null || list.isEmpty()){
                break;
            }
            if(list.contains("handling")){
                if(i==5){
                    return new ResponseApiVO(-2,"竞价繁忙中",null);
                }
                Thread.sleep(i*200);
            }else{
                BiddingVO bvo=(BiddingVO)list.get(list.size()-1);
                if(dto.getPrice()<=Integer.parseInt(bvo.getPrice())){
                    return new ResponseApiVO(-2,"价位不能小于当前价位"+Double.parseDouble(bvo.getPrice())/100+"元",null);

                }
                list.add("handling");
                CacheUtil.put("pay","groupId-"+bidId+"-"+groupId,list);
                break;
            }

        }
        BiddingVO vo=null;
        if(list==null){
            if(dto.getPrice()<gpo.getBidPrice()){
                return new ResponseApiVO(-2,"抱歉，竞价金额不能小于"+(double)gpo.getBidPrice()/100+"元",null);
            }else{
                list=new CopyOnWriteArrayList();
                vo=new BiddingVO();
                vo.setPrice(dto.getPrice()+"");
                vo.setOpenId(TokenUtil.getUserObject(token).toString());
                vo.setUserName(TokenUtil.getUserName(token));
                vo.setUserId(TokenUtil.getUserId(token));
                vo.setTime(dto.getTime());
                vo.setBidEndTime(DateFormatUtils.format(bidEndTime,"yyyy-MM-dd HH:mm:ss"));
                handleBidList(list,vo.getOpenId());
                list.add(vo);
                CacheUtil.put("pay","groupId-"+bidId+"-"+groupId,list);
            }
        }else{

            vo=new BiddingVO();
            vo.setPrice(dto.getPrice()+"");
            vo.setOpenId(TokenUtil.getUserObject(token).toString());
            vo.setUserName(TokenUtil.getUserName(token));
            vo.setUserId(TokenUtil.getUserId(token));
            vo.setTime(dto.getTime());
            vo.setBidEndTime(DateFormatUtils.format(bidEndTime,"yyyy-MM-dd HH:mm:ss"));
            handleBidList(list,vo.getOpenId());
            list.add(vo);
            list.remove("handling");
            CacheUtil.put("pay","groupId-"+bidId+"-"+groupId,list);

        }
       // IMApiUtil.sendGroupMsg(groupId,new ResponseVO(1,"成功",vo));
        //liveRadioService.getLiveRadioByIdAndStoreId(TokenUtil.getStoreId(token))
       // this.commonSingleFieldBySingleSearchParam()
          return new ResponseApiVO(1,"成功",null);
    }
    private void handleBidList(List list,String openId){
        if(list!=null){
            BiddingVO bv=null;
            for(Object o:list){
                if(o instanceof  BiddingVO){
                    bv=(BiddingVO) o;
                    if(bv.getOpenId().equals(openId)){
                        list.remove(bv);
                    }
                }

            }

        }
    }
    public ResponseApiVO searchWaitPayByGoddess(String token,BiddingSearchWaitPayDTO dto)throws Exception{
        String streamId=TokenUtil.getStreamId(token);
        String groupId=null;
       if(StringUtils.isBlank(streamId) && (dto.getBidCode()==null ||dto.getBidCode()<=0)){
           return new ResponseApiVO(-2,"参数异常",null);
       }else if(dto!=null && dto.getBidCode()!=null && dto.getBidCode()<=0){
           return new ResponseApiVO(-2,"竞价code异常",null);
       }else if(dto.getBidCode()==null){
           groupId=streamId.replaceFirst("[0-9]+_","");
       }
        return commmonSearchWaiyPay(token,groupId,dto,true);
    }
    public ResponseApiVO commmonSearchWaiyPay(String token,String groupId,BiddingSearchWaitPayDTO dto,boolean isGoddess)throws Exception{


        Map searchMap=new HashMap();

        if(dto.getBidCode()!=null && dto.getBidCode()>0){
            searchMap.put("id",dto.getBidCode());
        }else{
            searchMap.put("groupId",groupId);
        }
        if(isGoddess){
            searchMap.put("userId",TokenUtil.getUserId(token));
        }else{
            searchMap.put("luckyDogUserId",TokenUtil.getUserId(token));
        }
        Map bidMap=this.getCurrentBidMapBySearchMap(searchMap);
        if(bidMap==null){
            return new ResponseApiVO(-2,"查询失败",null);

        }
        Integer bidCode=(Integer)bidMap.get("id");
        groupId=(String)bidMap.get("groupId");
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
        if(userIdBidId==null){
            return new ResponseApiVO(-2,"没有待付款的用户",null);

        }
        Map map=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
        if(map==null){
            return new ResponseApiVO(-2,"没有待付款的竞价金额",null);
        }else{
            String paycancel=(String) CacheUtil.get("pay","bidding-cancel-"+bidCode+"-"+groupId);
            if(StringUtils.isNotBlank(paycancel)){
                Map m=new HashMap();
                m.put("openId",map.get("openid"));
                m.put("bidPrice",map.get("bidPrice"));
                m.put("time",map.get("time"));
                m.put("name",map.get("name"));
                return new ResponseApiVO(-2,"用户已取消支付",m);

            }
            map.remove("goddessUserId");
            return new ResponseApiVO(1,"成功",map);

        }
    }
    public ResponseApiVO searchWaitPayByUser(String token,BiddingSearchWaitPayDTO dto)throws Exception{

      return commmonSearchWaiyPay(token,TokenUtil.getGroupId(token),dto,false);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO  doEndPlay(String token)throws Exception{

        Map map=this.getCurrentBidByUserId(TokenUtil.getUserId(token));
        if(map==null ){
            return new ResponseApiVO(-2,"结束失败",null);
        }
        if(!map.containsKey("endTime") ||  map.get("endTime")==null){
            return new ResponseApiVO(-2,"约玩还没开始",null);
        }else{
            Date current=new Date();
            Map setMap=new HashMap();
            setMap.put("endTime",current);
            if(!map.containsKey("startTime") ||  map.get("startTime")==null){
                setMap.put("startTime",current);

            }
            ResponseVO vo=this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",setMap,"id",map.get("id"));
            if(vo.getReCode()!=1){
                return new ResponseApiVO(-2,"结束失败",null);
            }
            this.incomeService.handleGoddessIncome((Integer) map.get("id"));
        }
        return new ResponseApiVO(1,"成功",null);

    }

    private List commonHandleBidOrder(List<Map> bidList){
        Date currentDate=new Date();
        bidList.forEach(a->{
            Date endTime=(Date) a.get("endTime");
            Date bidEndTime=(Date) a.get("bidEndTime");
            Integer bidUserId=(Integer) a.get("luckyUserId");
            Integer bidCode=(Integer) a.get("bidCode");
            Integer goddessUserId=(Integer) a.get("userId");
            Integer status=(Integer) a.get("bidStatus");
            String groupId=(String) a.get("groupId");
            if(bidUserId==null){
                if(endTime==null){
                    if(DateUtils.addMinutes(bidEndTime,30).before(new Date())){
                        a.put("status",BiddingStatusEnum.Status10.getCode());
                        a.put("statusMsg",BiddingStatusEnum.Status10.getName());
                    }else{
                        a.put("status",BiddingStatusEnum.Status1.getCode());
                        a.put("statusMsg",BiddingStatusEnum.Status1.getName());
                    }
                }

            }else if(bidUserId>0 && endTime==null){
                if(DateUtils.addMinutes(bidEndTime,30).before(new Date())){
                    a.put("status",BiddingStatusEnum.Status10.getCode());
                    a.put("statusMsg",BiddingStatusEnum.Status10.getName());
                }else{

                    String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidCode+"-"+goddessUserId);
                    if(StringUtils.isNotBlank(retStr)){
                        a.put("status",BiddingStatusEnum.Status7.getCode());
                        a.put("statusMsg",BiddingStatusEnum.Status7.getName());
                    }else{
                        retStr=(String)CacheUtil.get("pay","bidding-cancel-"+bidCode+"-"+groupId);
                        if(StringUtils.isNotBlank(retStr)){
                            a.put("status",BiddingStatusEnum.Status4.getCode());
                            a.put("statusMsg",BiddingStatusEnum.Status4.getName());
                        }else{
                            a.put("status",BiddingStatusEnum.Status9.getCode());
                            a.put("statusMsg",BiddingStatusEnum.Status9.getName());
                        }

                    }

                }

            }else if(endTime.after(currentDate)){
                a.put("status",BiddingStatusEnum.Status5.getCode());
                a.put("statusMsg",BiddingStatusEnum.Status5.getName());
            }else{
               if(BiddingStatusEnum.Status10.getCode().equals(status)){
                   a.put("status",BiddingStatusEnum.Status10.getCode());
                   a.put("statusMsg",BiddingStatusEnum.Status10.getName());
               }else{
                   a.put("status",BiddingStatusEnum.Status8.getCode());
                   a.put("statusMsg",BiddingStatusEnum.Status8.getName());
               }

            }
            // a.put("headImg",this.userInfoService.getPhotograph(goddessUserId));
            if(endTime!=null) a.replace("endTime",DateFormatUtils.format(endTime,"yyyy-MM-dd HH:mm:ss"));
            a.replace("createTime",DateFormatUtils.format((Date)a.get("createTime"),"yyyy-MM-dd HH:mm:ss"));
            a.remove("luckyUserId");
            a.remove("bidEndTime");
            a.remove("userId");
            a.remove("groupId");
        });
        return bidList;
    }
    public ResponseApiVO getBidOrderList(String token,Integer pageN0,boolean isGoddess){
        Page p=new Page(pageN0==null?1:pageN0,10);

        Integer userId=TokenUtil.getUserId(token);
        Integer dgId=null;
        if(isGoddess){
            dgId=this.commonSingleFieldBySingleSearchParam("ddw_goddess","userId",userId,"id",Integer.class);

        }
        Map searchMap=new HashMap();
        String childKeyName=null;
        if(dgId!=null){
            childKeyName="luckyDogUserId";
            searchMap.put("userId",userId);

        }else{
            childKeyName="userId";
            searchMap.put("luckyDogUserId",userId);
        }
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id",childKeyName,null);
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","createTime desc","t1.groupId,t1.status bidStatus,t1.createTime,t1.bidEndTime,t1.price,t1.endTime ,DATE_FORMAT(t1.startTime,'%Y-%m-%d %H:%i:%S') startTime,DATE_FORMAT(t1.payEndTime,'%Y-%m-%d %H:%i:%S') payEndTime,DATE_FORMAT(t1.makeSureEndTime,'%Y-%m-%d %H:%i:%S') makeSureEndTime,t1.luckyDogUserId luckyUserId,t1.times time,t1.id bidCode,ct0.headImgUrl,ct0.nickName,t1.userId,ct0.openid,t1.isEvaluate",p.getStartRow(),p.getEndRow(),searchMap,cb);

        List<Map> bidList=this.getCommonMapper().selectObjects(csb);
        if(bidList==null || bidList.isEmpty()){
            return new ResponseApiVO(2,"没有竞价信息",new ListVO(new ArrayList()));
        }

        return new ResponseApiVO(1,"成功",new ListVO(commonHandleBidOrder(bidList)));

    }

    public ResponseApiVO getBidOrderInfoByBidCode(Integer bidCode,String token,boolean flag)throws Exception{
        Integer storeId=TokenUtil.getStoreId(token);
        Map bidMap=this.getBidMapById2(bidCode);
        Map a=new HashMap();
            if(bidMap==null){
            a.put("status",BiddingStatusEnum.Status8.getCode());
            a.put("statusMsg",BiddingStatusEnum.Status8.getName());
            return new ResponseApiVO(2,"约玩已结束",a);
        }else{
            a.put("status",BiddingStatusEnum.Status5.getCode());
            a.put("statusMsg",BiddingStatusEnum.Status5.getName());
        }
        Integer userId=(Integer) bidMap.get("userId");
        GoddessPO gpo=this.goddessService.getAppointment(userId,storeId);
        if(gpo==null){
            return new ResponseApiVO(-2,"失败",null);
        }else{
            a.put("price",gpo.getBidPrice());
            a.put("bidCode",bidCode);
            a.put("goddessUserId",userId);
            a.put("endTime",bidMap.get("endTime"));
            if(flag){
                BiddingRenewVO biddingRenewVO=new BiddingRenewVO();
                PropertyUtils.copyProperties(biddingRenewVO,a);
                return new ResponseApiVO(1,"成功",biddingRenewVO);
            }
        }
        return new ResponseApiVO(1,"成功",a);
    }
    public ResponseApiVO makeSureRenew(BiddingRenewDTO dto,String token)throws Exception{
        Integer userId=TokenUtil.getUserId(token);
        ResponseApiVO<Map> res=getBidOrderInfoByBidCode(dto.getBidCode(),token,false);
        if(res.getReCode()==1){
            Map m=res.getData();
            Integer time=dto.getTime()/60;

            time=time<1?1:time;
            Integer price=((Integer) m.get("price"))*time;
            m.put("time",dto.getTime());
            m.put("price",price);
            CacheUtil.put("pay","bidding-renew-"+userId+"-"+dto.getBidCode(),m);
            return new ResponseApiVO(1,"成功",null);
        }else{
            return res;
        }
    }
    public ResponseApiVO getBidOrderInfoByGoddess(String token,Integer bidCode,boolean isGoddess){
        Integer userId=TokenUtil.getUserId(token);
        if(bidCode==null){
            if(!isGoddess){
                Map map=new HashMap();
                String groupId=TokenUtil.getGroupId(token);
                Map bidMap=this.getCurrentBidMapNoBidEndTime(groupId);
                if(bidMap==null){
                    return new ResponseApiVO(-2,"没有竞价信息",null);
                }
                bidCode=(Integer)bidMap.get("id");
                String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+bidCode+"-"+groupId);
                Map paymap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);

                String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidCode+"-"+TokenUtil.getUserId(token));
                if(StringUtils.isNotBlank(retStr)){
                    map.put("status", BiddingStatusEnum.Status7.getCode());
                    map.put("statusMsg",BiddingStatusEnum.Status7.getName());
                    map.put("userName",paymap.get("name"));
                    //map.put("openId",paymap.get("openid"));
                    map.put("time",paymap.get("time"));
                    map.put("price",paymap.get("bidPrice"));
                    map.put("headImgUrl",paymap.get("headImgUrl"));
                    map.put("goddessName",paymap.get("goddessName"));
                    return new ResponseApiVO(2,"用户已支付",map);
                }
            }
            return new ResponseApiVO(-2,"竞价code为空",null);

        }
        Integer dgId=null;
        if(isGoddess){
            dgId=this.commonSingleFieldBySingleSearchParam("ddw_goddess","userId",userId,"id",Integer.class);

        }
        //Map map=new HashMap();

       /*
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
        Map paymap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);

        String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
        if(StringUtils.isNotBlank(retStr)){
            map.put("status", BiddingStatusEnum.Status7.getCode());
            map.put("statusMsg",BiddingStatusEnum.Status7.getName());
            map.put("userName",paymap.get("name"));
            map.put("openId",paymap.get("openid"));
            map.put("time",paymap.get("time"));
            map.put("price",paymap.get("bidPrice"));
            return new ResponseApiVO(2,"用户已支付",null);
        }
        if(StringUtils.isNotBlank(userIdBidId)){
            map.put("status", BiddingStatusEnum.Status7.getCode());
            map.put("statusMsg",BiddingStatusEnum.Status7.getName());
            map.put("userName",paymap.get("name"));
            map.put("openId",paymap.get("openid"));
            map.put("time",paymap.get("time"));
            map.put("price",paymap.get("bidPrice"));
            return new ResponseApiVO(3,"用户未支付",map);
        }*/
        Map searchMap=new HashMap();
        searchMap.put("id",bidCode);
        String childKeyName=null;
        if(dgId!=null){
            childKeyName="luckyDogUserId";
            searchMap.put("userId",userId);
        }else{
            searchMap.put("luckyDogUserId",userId);
            childKeyName="userId";

        }
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id",childKeyName,null);
        //CommonChildBean cb=new CommonChildBean("ddw_order_view,"id",childKeyName,null);
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","createTime desc","t1.groupId,t1.status bidStatus,t1.createTime,t1.bidEndTime,t1.price,t1.endTime,DATE_FORMAT(t1.startTime,'%Y-%m-%d %H:%i:%S') startTime,DATE_FORMAT(t1.payEndTime,'%Y-%m-%d %H:%i:%S') payEndTime,DATE_FORMAT(t1.makeSureEndTime,'%Y-%m-%d %H:%i:%S') makeSureEndTime,t1.luckyDogUserId luckyUserId,t1.times time,t1.id bidCode,ct0.headImgUrl,ct0.nickName,t1.userId",0,1,searchMap,cb);

        List<Map> bidList=this.getCommonMapper().selectObjects(csb);
        if(bidList==null || bidList.isEmpty()){
            return new ResponseApiVO(2,"没有竞价订单信息",null);
        }

        Map a=(Map)commonHandleBidOrder(bidList).get(0);

        Map orderRsbMap=new HashMap();
        orderRsbMap.put("biddingId",bidCode);

        Map orderCbMap=new HashMap();
        if(dgId==null){
            orderRsbMap.put("creater",userId);
            orderCbMap.put("doCustomerUserId",userId);
        }
        CommonChildBean orderCb=new CommonChildBean("ddw_order","id","orderId",orderCbMap);
        CommonSearchBean orderRsb=new CommonSearchBean("ddw_order_bidding_pay","createTime desc","t1.createTime,t1.dorCost price,t1.orderNo,ct0.doType orderType",null,null,orderRsbMap,orderCb);
        List<Map> orderList=this.getCommonMapper().selectObjects(orderRsb);
        if(orderList!=null){
            orderList.forEach(b->{
                b.put("orderTypeName",OrderTypeEnum.getName((Integer) b.get("orderType")));
                b.put("createTime",DateFormatUtils.format((Date)b.get("createTime"),"yyyy-MM-dd HH:mm:ss"));
            });
        }
        a.put("priceList",orderList);
        return new ResponseApiVO(1,"成功",a);


    }
    public ResponseApiVO getCurrentAllBidding(String token)throws Exception{
       /* String streamId=TokenUtil.getStreamId(token);
        if(streamId==null){
            return new ResponseApiVO(-2,"直播房间不存在",null);
        }*/

       // String groupId=streamId.replaceFirst("[0-9]+_","");

        Map bidMap=getCurrentBidByUserId(TokenUtil.getUserId(token));

        if(bidMap==null){
            Map map=new HashMap();
            map.put("status",6);
            map.put("statusMsg","空闲中");
            return new ResponseApiVO(3,"空闲中",map);

        }else{
            Date currentDate=new Date();
            Date endTime=(Date) bidMap.get("endTime");
            if(endTime!=null && endTime.after(currentDate)){
                Map voMap=new HashMap();
                voMap.put("endTime",DateFormatUtils.format((Date)bidMap.get("endTime"),"yyyy-MM-dd HH:mm:ss"));
                voMap.put("startTime",DateFormatUtils.format((Date)bidMap.get("startTime"),"yyyy-MM-dd HH:mm:ss"));
                voMap.put("status",5);
                voMap.put("statusMsg","约玩中");
                return new ResponseApiVO(4,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(endTime)+"后",voMap);

            }else if(endTime!=null && endTime.before(currentDate)){
                Map map=new HashMap();
                map.put("status",6);
                map.put("statusMsg","空闲中");
                return new ResponseApiVO(3,"空闲中",map);
            }
        }
        String groupId=(String)bidMap.get("groupId");
        List<BiddingVO> list=(List)CacheUtil.get("pay","groupId-"+(Integer)bidMap.get("id")+"-"+groupId);

        if(list==null || list.isEmpty()){
            return new ResponseApiVO(2,"没有竞价数据",new ListVO(new ArrayList()));

        }

        list.remove("handling");
        Map map=new HashMap();
        map.put("list",list);
        map.put("bidEndTime",list.get(0).getBidEndTime());
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+(Integer)bidMap.get("id")+"-"+groupId);
        if(userIdBidId==null){
            map.put("status",1);
            map.put("statusMsg","竞价中");
            TokenUtil.putBidCode(token,(Integer)bidMap.get("id"));
            return new ResponseApiVO(1,"成功",map);
        }
        Map paymap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
        Integer bidcode=(Integer) paymap.get("code");
        String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+bidcode+"-"+TokenUtil.getUserId(token));
        if(StringUtils.isNotBlank(retStr)){
            map.put("status",3);
            map.put("statusMsg","用户已支付");
            map.put("orderNo",retStr);
            return new ResponseApiVO(1,"成功",map);

        }else{
            retStr=(String) CacheUtil.get("pay","bidding-cancel-"+bidcode+"-"+groupId);
            if(StringUtils.isNotBlank(retStr)){
                map.put("status",4);
                map.put("statusMsg","用户已取消支付");
                return new ResponseApiVO(1,"成功",map);

            }

        }

        map.put("status",2);
        map.put("statusMsg","接单中");
        map.put("bidSuccessOpenId",paymap.get("openid"));
        map.put("payEndTime",paymap.get("payEndTime"));

        return new ResponseApiVO(1,"成功",map);

    }


    public static void main(String[] args) {
        long l=(DateUtils.addMinutes(new Date(),150).getTime()-System.currentTimeMillis());
        System.out.println(l);

        //System.out.println((doubble)l/1000);
        BigDecimal v= BigDecimal.valueOf(l).divide(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_DOWN);
        long part=(long)v.doubleValue();
        BigDecimal point=v.subtract(BigDecimal.valueOf(part)).multiply(BigDecimal.valueOf(60));
        System.out.println(part+"分"+point.intValue()+"秒");
        System.out.println(new ArrayList().remove("125"));
        String useridStr="1_41_180606213932".replaceAll("^([0-9]+_)(.*)(_[0-9]+)$","$2");
        System.out.println(useridStr);

    }
}
