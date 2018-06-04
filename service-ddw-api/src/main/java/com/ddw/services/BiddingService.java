package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.enums.GoddessAppointmentEnum;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.BiddingTimer;
import com.ddw.util.IMApiUtil;
import com.gen.common.exception.GenException;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
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
public class BiddingService extends CommonService {

    @Value("${goddess.bidEndTime.minute}")
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
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        Map map=bidList.get(0);
        //map.get("id");
        Map updateMap=new HashMap();
        updateMap.put("endTime",DateUtils.addMinutes(new Date(),bv.getTime()));
        updateMap.put("luckyDogUserId",bv.getUserId());
        updateMap.put("price",bv.getPrice());
        updateMap.put("updateTime",new Date());
        this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",map.get("id"));
        Map m=new HashMap();
        m.put("msg","恭喜"+bv.getUserName()+"以"+Double.parseDouble(bv.getPrice())/100+"元竞价成功");
        m.put("openid",bv.getOpenId());
        m.put("bidPrice",bv.getPrice());

        IMApiUtil.sendGroupMsg(groupId,new ResponseVO(2,"竞价成功",m));
        Map searchOrderBidMap=new HashMap();
        searchOrderBidMap.put("biddingId",map.get("id"));
        searchOrderBidMap.put("creater",bv.getUserId());
        Map orderBidding=this.commonObjectBySearchCondition("ddw_order_bidding_pay",searchOrderBidMap);
        if(orderBidding!=null && orderBidding.containsKey("dorCost")){
           Integer dorCost=(Integer) orderBidding.get("dorCost");
            m.put("needPayPrice",Integer.parseInt(bv.getPrice())-dorCost+"");
            BiddingPayVO payVO=new BiddingPayVO();
            PropertyUtils.copyProperties(payVO,m);
            payVO.setBiddingId((Integer)map.get("id"));
            Map payMap=new HashMap();
            payMap.put(bv.getUserId(),payVO);

            CacheUtil.put("pay","bidding-pay-"+groupId,payMap);
            CacheUtil.delete("commonCache","groupId-"+groupId);
           return  new ResponseApiVO(1,"成功",m);
        }
        return  new ResponseApiVO(-2,"失败",m);

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
        for(int b=list.size()-1;b>-1;b--){
            vMap=list.get(b);
            userid=(Integer) vMap.get("creater");
            if(!filterUserId.equals(userid)){
                orders.add(vMap.get("orderNo"));
            }
        }
        return payCenterService.exitOrder(orders);
    }
    public ResponseApiVO chooseBidding(String openId,String token)throws Exception{
       // GoddessPO  gpo=this.goddessService.getAppointment(TokenUtil.getGroupId(token));
        LiveRadioPO po=this.liveRadioService.getLiveRadio(TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
        List<BiddingVO> list=(List)CacheUtil.get("commonCache","groupId-"+po.getGroupId());
        ResponseApiVO vo=null;
        if(list!=null){
            BiddingVO bv=null;
            for(int i=list.size()-1;i>-1;i--){
                bv=list.get(i);
                if(bv.getOpenId().equals(openId)){
                    this.refundBidding(po.getGroupId(),bv.getUserId());
                    return biddingSuccess(po.getGroupId(),bv);

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

        GoddessPO gpo=this.goddessService.getAppointment(storeId,Integer.parseInt(useridStr));
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"约玩已关闭",null);

        }
        Map<Integer,BiddingPayVO> payMap=(Map) CacheUtil.get("pay","bidding-pay-"+groupId);
        if(payMap!=null && payMap.containsKey(TokenUtil.getUserId(token))){
            return new ResponseApiVO(4,"本轮竞价已经结束，"+payMap.get(TokenUtil.getUserId(token)).getMsg(),null);
        }
        //查询竞价表是否有记录，若有或者未过期的就表示陪玩中，否则就是空闲
        Map searchMap=new HashMap();
        searchMap.put("endTime,>=",new Date());
        searchMap.put("groupId",groupId);
        List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        if(bidList!=null && !bidList.isEmpty()){

            return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr((Map)bidList.get(0))+"后",null);
        }
        //获取缓存中的最高竞价
        List<BiddingVO> list=(List)cacheService.get("groupId-"+groupId);
        if(list==null || list.isEmpty()){

            Integer bidPrice=this.commonSingleFieldBySingleSearchParam("ddw_goddess","userId",Integer.parseInt(useridStr),"bidPrice",Integer.class);
            BiddingVO vo=new BiddingVO();
            vo.setPrice(bidPrice+"");
            return new ResponseApiVO(2,"目前还没人竞价,起投金额为："+(double)bidPrice/100+"元",vo);
        }
        list.remove("handling");
        return new ResponseApiVO(1,"成功",list.get(list.size()-1));
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
        GoddessPO gpo=this.goddessService.getAppointment(storeId,Integer.parseInt(useridStr));
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"抱歉约玩已被关闭，没法竞价",null);

        }
        Map<Integer,BiddingPayVO> payMap=(Map) CacheUtil.get("pay","bidding-pay-"+groupId);
        if(payMap!=null && payMap.containsKey(TokenUtil.getUserId(token))){
            return new ResponseApiVO(4,"本轮竞价已经结束，"+payMap.get(TokenUtil.getUserId(token)).getMsg(),null);
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
            Timer timer=new Timer();
            timer.schedule(new BiddingTimer(this,groupId),DateUtils.addMinutes(new Date(),this.bidEndTimeMinute));
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
                return new ResponseApiVO(3,"陪玩中，空闲时间约在"+this.getSurplusTimeStr(bidMap)+"后",null);

            }else if(endTime!=null && endTime.before(currentDate)){
                bidEndTime=DateUtils.addMinutes(new Date(),this.bidEndTimeMinute);
                ResponseVO<Integer> res=this.saveBidding(token,groupId,bidEndTime);
                if(res.getReCode()!=1){
                    throw new GenException("创建竞价记录失败");
                }
                bidId=res.getData();
                flag=true;
                Timer timer=new Timer();
                timer.schedule(new BiddingTimer(this,groupId),bidEndTime);
            }


        }

        if(flag){
            if(gpo.getEarnest()==null){
                throw new GenException("定金为空");
            }
            BiddingEarnestVO be=new BiddingEarnestVO();
            be.setBidCode(MyEncryptUtil.encry(bidId+""));
            be.setPrice(gpo.getEarnest());
            return new ResponseApiVO(-2,"请完成定金支付",be);
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
        IMApiUtil.sendGroupMsg(groupId,new ResponseVO(1,"成功",vo));
        //liveRadioService.getLiveRadioByIdAndStoreId(TokenUtil.getStoreId(token))
       // this.commonSingleFieldBySingleSearchParam()
          return new ResponseApiVO(1,"成功",null);
    }
    public ResponseApiVO searchWaitPayByGoddess(String groupId){
        Map<Integer,BiddingPayVO> payMap=(Map) CacheUtil.get("pay","bidding-pay-"+groupId);
        if(payMap==null){
            return new ResponseApiVO(2,"没有待付款的竞价金额",null);
        }else{
            Set<Integer> keys=payMap.keySet();
            for(Integer id:keys){
                return new ResponseApiVO(1,"成功",payMap.get(id));
            }
        }
        return new ResponseApiVO(-2,"失败",null);
    }
    public ResponseApiVO searchWaitPayByUser(String groupId,String token){
        if(StringUtils.isBlank(groupId)){
            groupId=TokenUtil.getGroupId(token);
        }
      return searchWaitPayByGoddess(groupId);
    }
    public ResponseApiVO getCurrentAllBidding(String token)throws Exception{
        LiveRadioPO po=this.liveRadioService.getLiveRadio(TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
        if(po==null){
            return new ResponseApiVO(-2,"直播房间不存在",null);
        }
        List list=(List)CacheUtil.get("commonCache","groupId-"+po.getGroupId());
        list.remove("handling");
        return new ResponseApiVO(1,"成功",new ListVO(list));

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
    }
}
