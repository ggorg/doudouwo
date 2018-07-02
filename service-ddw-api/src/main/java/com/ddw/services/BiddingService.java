package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.BiddingTimer;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.sun.tools.javah.Gen;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 竞价
 */
@Service
@Transactional(readOnly = true)
public class BiddingService extends CommonService {

    @Value("${goddess.bidEndTime.minute:15}")
    private Integer bidEndTimeMinute;

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private CacheService cacheService;
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
    private String getSurplusTimeStr(Map m){
        Date endTime=(Date)m.get("endTime");
        long l=endTime.getTime()-System.currentTimeMillis();
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
        updateMap.put("luckyDogUserId",bv.getUserId());
        updateMap.put("luckyDogUserName",bv.getUserName());
        updateMap.put("price",bv.getPrice());
        updateMap.put("updateTime",new Date());
        updateMap.put("times",bv.getTime());
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
            m.put("needPayPrice",Integer.parseInt(bv.getPrice())-dorCost+"");
            m.put("code",map.get("id"));
            Map retMap=new HashMap(m);
            m.put("goddessUserId",map.get("userId"));
            m.put("headImgUrl",map.get("headImgUrl"));
            m.put("goddessName",map.get("goddessName"));
            /*BiddingPayVO payVO=new BiddingPayVO();
            PropertyUtils.copyProperties(payVO,m);
            payVO.setBiddingId((Integer)map.get("id"));
            Map payMap=new HashMap();
            payMap.put(bv.getUserId(),payVO);*/

            CacheUtil.put("pay","bidding-pay-"+bv.getUserId()+"-"+map.get("id"),m);
            CacheUtil.put("pay","bidding-success-"+groupId,bv.getUserId()+"-"+map.get("id"));
           // CacheUtil.delete("commonCache","groupId-"+groupId);


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
        List<Map> list= this.commonObjectsBySingleParam("ddw_order_bidding_pay","biddingId",map.get("id"));
        List orders=new ArrayList();
        Integer userid=null;
        Map vMap=null;
        Map filterUserMap=null;
        for(int b=list.size()-1;b>-1;b--){
            vMap=list.get(b);
            userid=(Integer) vMap.get("creater");
            if(!filterUserId.equals(userid)){
                orders.add(vMap.get("orderId"));
            }else{
                filterUserMap=vMap;
            }
        }

        ResponseApiVO apiVo=payCenterService.exitOrder(orders);
        apiVo.setData(filterUserMap);
        return apiVo;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO cancelBidPayByGoddess(String token)throws Exception{
        String streamId=TokenUtil.getStreamId(token);
        if(StringUtils.isBlank(streamId)){
            return new ResponseApiVO(-2,"取消失败",null);
        }
        String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
        if(StringUtils.isNotBlank(retStr)){
            return new ResponseApiVO(-2,"没法取消，用户已经支付金额",null);

        }
        String groupId=streamId.replaceFirst("[0-9]+_","");
        GroupIdDTO groupIdDTO=new GroupIdDTO();
        groupIdDTO.setGroupId(groupId);

        String ub=(String)CacheUtil.get("pay","bidding-success-"+groupId);
        Map payMap=(Map) CacheUtil.get("pay","bidding-pay-"+ub);
        Map setMap=new HashMap();
        setMap.put("bidEndTime",new Date());
        ResponseVO resVo=this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",setMap,"id",payMap.get("code"));
        if(resVo.getReCode()!=1){
            return new ResponseApiVO(-2,"取消失败",null);
        }

        CacheUtil.delete("pay","bidding-success-"+groupId);
        CacheUtil.delete("pay","bidding-pay-"+ub);
        CacheUtil.delete("commonCache","groupId-"+groupId);
        CacheUtil.delete("commonCache","bidding-cancel-"+groupId);



        return new ResponseApiVO(1,"成功",null);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO cancelBidPayByUserId(String token,GroupIdDTO dto,boolean isUser){
        if(StringUtils.isBlank(dto.getGroupId())){
            return new ResponseApiVO(-2,"群组不能为空",null);
        }
        String ub=(String)CacheUtil.get("pay","bidding-success-"+dto.getGroupId());
        if(StringUtils.isBlank(ub)){
            return new ResponseApiVO(-3,"操作失败",null);
        }else if(isUser && !ub.startsWith(TokenUtil.getUserId(token)+"-")){
            return new ResponseApiVO(-2,"抱歉，当前用户没有权限取消",null);

        }
        CacheUtil.put("commonCache","bidding-cancel-"+dto.getGroupId(),"true");
        return new ResponseApiVO(1,"成功",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO makeSureFinishPay(String token){
       String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
       if(StringUtils.isNotBlank(retStr)){

           String streamId=TokenUtil.getStreamId(token);
           if(StringUtils.isBlank(streamId)){

               return new ResponseApiVO(-2,"直播房间不存在",null);
           }
           String groupId=streamId.replaceFirst("[0-9]+_","");
           String ub=(String)CacheUtil.get("pay","bidding-success-"+groupId);
           Map payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
           Map updateMap=new HashMap();
           updateMap.put("endTime",DateUtils.addMinutes(new Date(),(Integer) payMap.get("time")));
           //updateMap.put("times",(Integer) payMap.get("time"));
           this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",payMap.get("code"));

           CacheUtil.delete("commonCache","groupId-"+groupId);
           CacheUtil.delete("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
           CacheUtil.delete("pay","bidding-success-"+groupId);
           CacheUtil.delete("pay","bidding-pay-"+ub);
           return new ResponseApiVO(1,"成功",null);

       }else{
           return new ResponseApiVO(-2,"用户还没有支付",null);

       }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO chooseBidding(String openId,String token)throws Exception{
       // GoddessPO  gpo=this.goddessService.getAppointment(TokenUtil.getGroupId(token));


        LiveRadioPO po=this.liveRadioService.getLiveRadio(TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
        String ub=(String)CacheUtil.get("pay","bidding-success-"+po.getGroupId());
        if(ub!=null){
            Map m=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
            m.remove("goddessUserId");
            return new ResponseApiVO(1,"成功",m);

        }
        List<BiddingVO> list=(List)CacheUtil.get("commonCache","groupId-"+po.getGroupId());
        ResponseApiVO vo=null;
        ResponseVO inVo=null;
        if(list!=null){
            BiddingVO bv=null;
            for(int i=list.size()-1;i>-1;i--){
                bv=list.get(i);
                if(bv.getOpenId().equals(openId)){
                    vo=this.refundBidding(po.getGroupId(),bv.getUserId());
                    Map bidPay=(Map)vo.getData();
                    Integer cost=(Integer)bidPay.get("dorCost");
                    inVo=this.incomeService.commonIncome(TokenUtil.getUserId(token),cost, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType4,(String)bidPay.get("orderNo"));
                    if(inVo.getReCode()==1){
                        consumeRankingListService.save(bv.getUserId(),TokenUtil.getUserId(token),cost,IncomeTypeEnum.IncomeType1);
                        return biddingSuccess(po.getGroupId(),bv);
                    }

                }
            }

        }
        return new ResponseApiVO(-2,"失败",null);

    }
    public ResponseApiVO getCurrentMaxPrice(String token)throws Exception{
        String groupId=TokenUtil.getGroupId(token);
        Integer storeId=TokenUtil.getStoreId(token);

        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请先选择一个直播房间",null);
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
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
        if(userIdBidId!=null ){
            Map payMap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
            Map map=new HashMap();
            map.put("status",2);
            map.put("statusMsg","支付中");
            return new ResponseApiVO(6,"本轮竞价已经结束，"+payMap.get("msg"),map);
        }
        //查询竞价表是否有记录，若有或者未过期的就表示陪玩中，否则就是空闲
        Map searchMap=new HashMap();
        searchMap.put("endTime,>=",new Date());
        searchMap.put("groupId",groupId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){
            Map dataMap=(Map)bidList.get(0);
            Map voMap=new HashMap();
            voMap.put("endTime",DateFormatUtils.format((Date)dataMap.get("endTime"),"yyyy-MM-dd HH:mm:ss"));
            voMap.put("status",5);
            voMap.put("statusMsg","约玩中");
            return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(dataMap)+"后",voMap);
        }
        //获取缓存中的最高竞价
        List<BiddingVO> list=(List)cacheService.get("groupId-"+groupId);
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
        return new ResponseApiVO(1,"成功",map);
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
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
        if(userIdBidId!=null ){
            Map payMap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
            return new ResponseApiVO(6,"本轮竞价已经结束，"+payMap.get("msg"),null);
        }
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("groupId",groupId);
        searchMap.put("bidEndTime,>=",new Date());
        Map bidMap=getCurrentBidMap(groupId);
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
            Date currentDate=new Date();
            bidId=(Integer) bidMap.get("id");
            bidEndTime=(Date) bidMap.get("bidEndTime");
            Date endTime=(Date) bidMap.get("endTime");
            if(bidEndTime.after(currentDate) && endTime==null){
               Map earnestMap=new HashMap();
               earnestMap.put("creater",TokenUtil.getUserId(token));
               earnestMap.put("biddingId",bidId);
               Map mapPO=this.commonObjectBySearchCondition("ddw_order_bidding_pay",earnestMap);
               if(mapPO!=null){
                  Integer orderId=(Integer) mapPO.get("orderId");
                   OrderPO orderPO=this.commonObjectBySingleParam("ddw_order","id",orderId,OrderPO.class);
                    if(!PayStatusEnum.PayStatus1.getCode().equals(orderPO.getDoPayStatus())){
                        flag=true;
                    }
               }else{
                   flag=true;

               }

           }else if(endTime!=null && endTime.after(currentDate)){
                Map voMap=new HashMap();
                voMap.put("endTime",DateFormatUtils.format((Date)bidMap.get("endTime"),"yyyy-MM-dd HH:mm:ss"));
                return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(bidMap)+"后",voMap);

            }else if(endTime!=null && endTime.before(currentDate)){
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
            if(gpo.getEarnest()==null){
                throw new GenException("请联系管理员配置定金额度");
            }
            /*BiddingEarnestVO be=new BiddingEarnestVO();
            be.setCode(bidId);
            be.setPrice(gpo.getEarnest());*/
            Map earnestMap=new HashMap();
            earnestMap.put("code",bidId);
            earnestMap.put("price",gpo.getEarnest());
            CacheUtil.put("pay","bidding-earnest-pay-"+TokenUtil.getUserId(token)+"-"+bidId,gpo.getEarnest());
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
            list=(List)CacheUtil.get("commonCache","groupId-"+groupId);
            if(list==null){
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
                cacheService.set("groupId-"+groupId,list);
                break;
            }

        }
        BiddingVO vo=null;
        if(list==null){
            if(dto.getPrice()<gpo.getBidPrice()){
                return new ResponseApiVO(-2,"抱歉，竞价金额不能小于"+(double)gpo.getBidPrice()/100+"元",null);
            }else{
                list=new ArrayList();
                vo=new BiddingVO();
                vo.setPrice(dto.getPrice()+"");
                vo.setOpenId(TokenUtil.getUserObject(token).toString());
                vo.setUserName(TokenUtil.getUserName(token));
                vo.setUserId(TokenUtil.getUserId(token));
                vo.setTime(dto.getTime());
                vo.setBidEndTime(DateFormatUtils.format(bidEndTime,"yyyy-MM-dd HH:mm:ss"));
                list.add(vo);
                cacheService.set("groupId-"+groupId,list);
            }
        }else{

            vo=new BiddingVO();
            vo.setPrice(dto.getPrice()+"");
            vo.setOpenId(TokenUtil.getUserObject(token).toString());
            vo.setUserName(TokenUtil.getUserName(token));
            vo.setUserId(TokenUtil.getUserId(token));
            vo.setTime(dto.getTime());
            vo.setBidEndTime(DateFormatUtils.format(bidEndTime,"yyyy-MM-dd HH:mm:ss"));
            list.add(vo);
            list.remove("handling");
            cacheService.set("groupId-"+groupId,list);
        }
       // IMApiUtil.sendGroupMsg(groupId,new ResponseVO(1,"成功",vo));
        //liveRadioService.getLiveRadioByIdAndStoreId(TokenUtil.getStoreId(token))
       // this.commonSingleFieldBySingleSearchParam()
          return new ResponseApiVO(1,"成功",null);
    }
    public ResponseApiVO searchWaitPayByGoddess(String token){
        String streamId=TokenUtil.getStreamId(token);
        if(streamId==null){
            return new ResponseApiVO(-2,"直播房间不存在",null);
        }
        String groupId=streamId.replaceFirst("[0-9]+_","");
        return commmonSearchWaiyPay(groupId);
    }
    public ResponseApiVO commmonSearchWaiyPay(String groupId){
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
        if(userIdBidId==null){
            return new ResponseApiVO(-2,"没有待付款的用户",null);

        }
        Map map=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);
        if(map==null){
            return new ResponseApiVO(-2,"没有待付款的竞价金额",null);
        }else{
            map.remove("goddessUserId");
            return new ResponseApiVO(1,"成功",map);

        }
    }
    public ResponseApiVO searchWaitPayByUser(String groupId,String token){
        if(StringUtils.isBlank(groupId)){
            groupId=TokenUtil.getGroupId(token);
        }
      return commmonSearchWaiyPay(groupId);
    }

    public ResponseApiVO  doEndPlay(String token){
        String streamId=TokenUtil.getStreamId(token);
        if(streamId==null){
            return new ResponseApiVO(-2,"直播房间不存在",null);
        }
        String groupId=streamId.replaceFirst("[0-9]+_","");

        Map searchMap=new HashMap();
        searchMap.put("groupId",groupId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc","t1.id,t1.endTime",1,1,searchMap);
        if(bidList==null || bidList.isEmpty()){
            return new ResponseApiVO(-2,"结束失败",null);
        }
        Map map=bidList.get(0);
        if(!map.containsKey("endTime") ||  map.get("endTime")==null){
            return new ResponseApiVO(-2,"约玩还没开始",null);
        }else{
            Map setMap=new HashMap();
            setMap.put("endTime",new Date());
            ResponseVO vo=this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",setMap,"id",map.get("id"));
            if(vo.getReCode()!=1){
                return new ResponseApiVO(-2,"结束失败",null);
            }
        }
        return new ResponseApiVO(1,"成功",null);

    }

    private List commonHandleBidOrder(List<Map> bidList){
        Date currentDate=new Date();
        bidList.forEach(a->{
            Date endTime=(Date) a.get("endTime");
            Integer bidUserId=(Integer) a.get("luckyUserId");
            // Integer goddessUserId=(Integer) a.get("userId");
            if(bidUserId==null){
                a.put("status",BiddingStatusEnum.Status1.getCode());
                a.put("statusMsg",BiddingStatusEnum.Status1.getName());
            }else if(bidUserId>0 && endTime==null){
                a.put("status",BiddingStatusEnum.Status7.getCode());
                a.put("statusMsg",BiddingStatusEnum.Status7.getName());
            }else if(endTime.after(currentDate)){
                a.put("status",BiddingStatusEnum.Status5.getCode());
                a.put("statusMsg",BiddingStatusEnum.Status5.getName());
            }else{
                a.put("status",BiddingStatusEnum.Status8.getCode());
                a.put("statusMsg",BiddingStatusEnum.Status8.getName());
            }
            // a.put("headImg",this.userInfoService.getPhotograph(goddessUserId));
            if(endTime!=null) a.replace("endTime",DateFormatUtils.format(endTime,"yyyy-MM-dd HH:mm:ss"));
            a.replace("createTime",DateFormatUtils.format((Date)a.get("createTime"),"MM/dd HH:mm:ss"));
            a.remove("luckyUserId");
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
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","createTime desc","t1.createTime,t1.price,t1.endTime,t1.luckyDogUserId luckyUserId,t1.times time,t1.id bidCode,ct0.headImgUrl,ct0.nickName",p.getStartRow(),p.getEndRow(),searchMap,cb);
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
                String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
                Map paymap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);

                String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
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
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_bidding","createTime desc","t1.createTime,t1.price,t1.endTime,t1.luckyDogUserId luckyUserId,t1.times time,t1.id bidCode,ct0.headImgUrl,ct0.nickName",0,1,searchMap,cb);

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
        String streamId=TokenUtil.getStreamId(token);
        if(streamId==null){
            return new ResponseApiVO(-2,"直播房间不存在",null);
        }
        String groupId=streamId.replaceFirst("[0-9]+_","");
        List<BiddingVO> list=(List)CacheUtil.get("commonCache","groupId-"+groupId);

        if(list==null || list.isEmpty()){
            Map bidMap=this.getCurrentBidMap(groupId);
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
                    voMap.put("status",5);
                    voMap.put("statusMsg","约玩中");
                    return new ResponseApiVO(4,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(bidMap)+"后",voMap);

                }
            }
            return new ResponseApiVO(2,"没有竞价数据",new ListVO(new ArrayList()));

        }

        list.remove("handling");
        Map map=new HashMap();
        map.put("list",list);
        map.put("bidEndTime",list.get(0).getBidEndTime());
        String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
        if(StringUtils.isNotBlank(retStr)){
            map.put("status",3);
            map.put("statusMsg","用户已支付");
            map.put("orderNo",retStr);
            return new ResponseApiVO(1,"成功",map);

        }else{
            retStr=(String) CacheUtil.get("commonCache","bidding-cancel-"+groupId);
            if(StringUtils.isNotBlank(retStr)){
                map.put("status",4);
                map.put("statusMsg","用户已取消支付");
                return new ResponseApiVO(1,"成功",map);

            }

        }
        String userIdBidId=(String) CacheUtil.get("pay","bidding-success-"+groupId);
        if(userIdBidId==null){
            map.put("status",1);
            map.put("statusMsg","竞价中");
        }else{
            Map paymap=(Map)CacheUtil.get("pay","bidding-pay-"+userIdBidId);;
            map.put("status",2);
            map.put("statusMsg","接单中");
            map.put("bidSuccessOpenId",paymap.get("openid"));
        }

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
        String useridStr="20202_1_41_180606213932".replaceAll("^([0-9]+_)(.*_)([0-9]+)$","$2");
        System.out.println(useridStr);

    }
}
