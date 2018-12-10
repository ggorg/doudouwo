package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ExitOrderPO;
import com.ddw.beans.OrderPO;
import com.ddw.beans.OrderViewPO;
import com.ddw.dao.OrderSalesMapper;
import com.ddw.enums.*;
import com.ddw.util.BiddingTimer;
import com.ddw.util.IMApiUtil;
import com.ddw.util.PayApiUtil;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
public class BaseOrderService extends CommonService {
    private static final Logger logger = Logger.getLogger(BaseOrderService.class);


    @Autowired
    protected IncomeService incomeService;

    @Autowired
    protected OrderViewService orderViewService;

    @Autowired
    protected BaseConsumeRankingListService baseConsumeRankingListService;

    @Autowired
    private StraregyService straregyService;

    @Autowired
    private BaseBiddingService baseBiddingService;

    @Autowired
    private OrderSalesMapper orderSalesMapper;

    @Value("${bidding.makeSureEndTime.minute:30}")
    private Integer makeSureEndTime;


    @Value("${rechargePic.url}")
    private String rechargePic;

    public OrderPO getCacheOrder(String orderNo)throws Exception{
        String objStr=(String) CacheUtil.get("pay","orderObject-"+orderNo);
        if(objStr!=null){
            return JSONObject.parseObject(objStr,OrderPO.class);
        }
        return this.commonObjectBySingleParam("ddw_order","id",OrderUtil.getOrderId(orderNo),OrderPO.class);

    }

    /**
     * 调用微信或者支付宝支付接口回调时候执行的
     * @param payStatusEnum
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO pulbicUpdateOrderPayStatus(PayStatusEnum payStatusEnum, String orderNo,OrderPO...orderPO)throws Exception{
        if(StringUtils.isBlank(orderNo)){
            return new ResponseVO(-2,"订单号为空",null);
        }


        OrderPO cacheOrder=null;
        ResponseVO res=null;

        if(orderPO!=null && orderPO.length>0){
            cacheOrder=orderPO[0];
            res=new ResponseVO(1,null,null);
        }else{
            cacheOrder=this.getCacheOrder(orderNo);
            Map map=new HashMap();
            map.put("doPayStatus",payStatusEnum.getCode());
            res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",OrderUtil.getOrderId(orderNo));
        }
        Integer doType=cacheOrder.getDoType();
        if(res.getReCode()==1){
            if(OrderTypeEnum.OrderType3.getCode().equals(doType)){
                Map mapRecharge=this.commonObjectBySingleParam("ddw_order_recharge","orderNo",orderNo);
                Integer userid=(Integer) mapRecharge.get("creater");
                Integer dorActCost=(Integer) mapRecharge.get("dorActCost");
                Integer dorCost=(Integer) mapRecharge.get("dorCost");
                Map setParams=new HashMap();
                setParams.put("money",dorCost);
                Map condition=new HashMap();
                condition.put("userId",userid);
                ResponseVO wres=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,condition,"version",new String[]{"money"});
                if(wres.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                //根据充值金额匹配会员策略
                straregyService.recharge(dorCost,userid);
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                StringBuilder builder=new StringBuilder();
                builder.append(OrderTypeEnum.OrderType3.getName()).append("【").append((int)(dorCost/100)).append("元】");
                po.setName(builder.toString());
                po.setHeadImg(this.rechargePic);
                po.setNum(1);
                po.setOrderType(OrderTypeEnum.OrderType3.getCode());
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(dorActCost);
                po.setUserId(userid);
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType8.getCode().equals(doType)){
                Map mapRecharge=this.commonObjectBySingleParam("ddw_order_doubi","orderNo",orderNo);
                Integer userid=(Integer) mapRecharge.get("creater");
                Integer dodDoubiNum=(Integer) mapRecharge.get("dodDoubiNum");
                Map setParams=new HashMap();
                setParams.put("coin",dodDoubiNum);
                Map condition=new HashMap();
                condition.put("userId",userid);
                ResponseVO wres=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,condition,"version",new String[]{"coin"});
                if(wres.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType8.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderType(OrderTypeEnum.OrderType8.getCode());
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(cacheOrder.getDoCost());
                po.setUserId(userid);
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                Map doubiRecord=new HashMap();
                doubiRecord.put("coin",dodDoubiNum);
                doubiRecord.put("userId",userid);
                doubiRecord.put("type", DoubiRecordTypeEnum.Type0.getCode());
                doubiRecord.put("createTime",new Date());
                doubiRecord.put("updatetime",new Date());
                ResponseVO resIn=this.commonInsertMap("ddw_my_wallet_doubi_record",doubiRecord);
                if(resIn.getReCode()!=1){
                    CacheUtil.put("pay","order-"+orderNo,"fail");
                    throw new GenException("更新钱包充值状态失败");
                }
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType4.getCode().equals(doType)){
                String ub =(String) CacheUtil.get("pay","pre-pay-"+orderNo);

                String str=(String)CacheUtil.get("pay","bidding-earnest-pay-"+ub);
                String strs[]=str.split("-");
                Integer earnest=Integer.parseInt(strs[0]);
                Integer goddessUserId=Integer.parseInt(strs[1]);
                String headImgUrl=this.commonSingleFieldBySingleSearchParam("ddw_userinfo","id",goddessUserId,"headImgUrl",String.class);
                OrderViewPO po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType4.getName());
                po.setHeadImg(headImgUrl);
                po.setBusId(Integer.parseInt(ub.split("-")[1]));
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(earnest);
                po.setOrderType(OrderTypeEnum.OrderType4.getCode());
                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                CacheUtil.delete("pay","bidding-earnest-pay-"+ub);
                CacheUtil.delete("pay","pre-pay-"+orderNo);
                CacheUtil.delete("pay","orderObject-"+orderNo);
            }else if(OrderTypeEnum.OrderType5.getCode().equals(doType)){

                List<String> ubs =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(ubs==null){
                    throw new GenException("更新竞价金额支付状态失败");
                }
                Map payMap=null;

                //goddessUserId
                OrderViewPO po=null;
                List<String> bid_goddessid=new ArrayList();
                Integer gid=null;
                Integer needPayPrice=0;
                Date makeSureEndTime=null;
                Map m=(Map)CacheUtil.get("publicCache","bidding-make-sure-end-time");
                //+ub,
                if(m==null){
                    m=new HashMap();
                }
                String headImgUrl=null;
                for(String ub:ubs){

                    payMap=(Map)CacheUtil.get("pay","bidding-pay-"+ub);
                    if(payMap==null){
                        throw new GenException("更新竞价金额支付状态失败");
                    }

                    Map updateMap=new HashMap();
                    makeSureEndTime=DateUtils.addMinutes(new Date(),this.makeSureEndTime);
                    updateMap.put("makeSureEndTime",makeSureEndTime);
                    this.commonUpdateBySingleSearchParam("ddw_goddess_bidding",updateMap,"id",payMap.get("code"));
                    Timer timer=new Timer();
                    timer.schedule(new BiddingTimer(this.baseBiddingService,ub),makeSureEndTime);
                    m.put(ub,DateFormatUtils.format(makeSureEndTime,"yyyy-MM-dd HH:mm:ss"));
                    gid=(Integer) payMap.get("goddessUserId");
                    logger.info("payMap:"+payMap);
                    if(gid==null){
                        throw new GenException("更新竞价金额支付状态失败");
                    }
                    headImgUrl=this.commonSingleFieldBySingleSearchParam("ddw_userinfo","id",gid,"headImgUrl",String.class);

                    bid_goddessid.add(payMap.get("code").toString()+"-"+gid);
                    needPayPrice=Integer.parseInt((String) payMap.get("needPayPrice"));
                   // this.incomeService.commonIncome(gid,needPayPrice, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType5,orderNo);
                    this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),gid,needPayPrice,IncomeTypeEnum.IncomeType1);


                    po=new OrderViewPO();
                    po.setBusId((Integer) payMap.get("code"));
                    po.setCreateTime(new Date());
                    po.setName(OrderTypeEnum.OrderType5.getName());
                    po.setHeadImg(headImgUrl);
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType5.getCode());

                    po.setPrice(needPayPrice);
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                }
                bid_goddessid.forEach(a->{
                    CacheUtil.put("pay","bidding-finish-pay-"+a,orderNo);
                   // CacheUtil.delete("pay","bidding-pay-"+a);
                });
                CacheUtil.put("publicCache","bidding-make-sure-end-time",m);
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType9.getCode().equals(doType)){

                Map renewMap=(Map) CacheUtil.get("pay","pre-pay-"+orderNo);

                if(renewMap==null){
                    throw new GenException("续费失败");
                }


                Integer needPayPrice=(Integer)renewMap.get("price");
                Integer time=(Integer)renewMap.get("time");

                Integer gid=(Integer) renewMap.get("goddessUserId");
                //this.incomeService.commonIncome(gid,needPayPrice, IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType9,orderNo);
                this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),gid,needPayPrice,IncomeTypeEnum.IncomeType1);


                OrderViewPO po=new OrderViewPO();
                po.setBusId(gid);
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType9.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setOrderType(OrderTypeEnum.OrderType9.getCode());
                po.setPrice(needPayPrice);
                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                Map setMap=new HashMap();
                setMap.put("price",needPayPrice);
                setMap.put("times",time);
                setMap.put("endTime", DateUtils.addMinutes((Date)renewMap.get("endTime"),time));
                Map searchMap=new HashMap();
                searchMap.put("id",renewMap.get("bidCode"));
                this.commonCalculateOptimisticLockUpdateByParam("ddw_goddess_bidding",setMap,searchMap,"version",new String[]{"price","times"});
                CacheUtil.delete("pay","pre-pay-"+orderNo);

            }else if(OrderTypeEnum.OrderType1.getCode().equals(doType)){
                List<Map> list=(List) CacheUtil.get("pay","goodsPru-order-"+orderNo);
                Map search=null;
                Map setMap=null;
                OrderViewPO po=null;

                Map<Integer,Map> goods=new HashMap();
                for(Map m:list){
                    setMap=new HashMap();
                    setMap.put("dghSaleNumber",m.get("productBuyNumber"));
                    setMap.put("updateTime",new Date());
                    search=new HashMap();
                    search.put("id",m.get("productId"));
                    ResponseVO r=this.commonCalculateOptimisticLockUpdateByParam("ddw_goods_product",setMap,search,"dghVersion",new String[]{"dghSaleNumber"});
                    if(r.getReCode()!=1){
                        CacheUtil.put("pay","order-"+orderNo,"fail");

                        throw new GenException("更新货品销量失败");
                    }
                    //计算商品销量
                    if(goods.containsKey((Integer)m.get("gid"))){
                        Map gm=goods.get((Integer)m.get("gid"));
                        gm.replace("num",(Integer)gm.get("num")+(Integer) m.get("productBuyNumber"));
                    }else{
                        Map g=new HashMap();
                        g.put("num",m.get("productBuyNumber"));
                        g.put("time",m.get("currentUpdateTime"));
                        goods.put((Integer)m.get("gid"),g);

                    }
                    po=new OrderViewPO();
                    po.setBusId((Integer) m.get("productId"));
                    po.setCreateTime(new Date());
                    po.setName((String)m.get("productName"));
                    po.setHeadImg((String)m.get("headImg"));
                    po.setNum((Integer) m.get("productBuyNumber"));
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setOrderType(OrderTypeEnum.OrderType1.getCode());

                    po.setPrice((Integer) m.get("productUnitPrice"));
                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);

                }
                Set<Integer> keys=goods.keySet();
                String month=DateFormatUtils.format(new Date(),"MM");
                Map param=null;
                String[] calculatesName=null;
                Map s=null;
                for(Integer gid:keys){

                    String time=DateFormatUtils.format((Date)goods.get(gid).get("time"),"MM");
                    Integer num=(Integer) goods.get(gid).get("num");
                    param=new HashMap();
                    param.put("updateTime",new Date());
                    param.put("dgSalesNumber",num);
                    param.put("dgMohthSales",num);
                    s=new HashMap();
                    s.put("id",gid);
                    if(month.equals(time)){
                        calculatesName=new String[]{"dgSalesNumber","dgMohthSales"};
                    }else{
                        calculatesName=new String[]{"dgSalesNumber"};
                    }
                    this.commonCalculateOptimisticLockUpdateByParam("ddw_goods",param,s,"version",calculatesName);
                }
                CacheUtil.delete("pay","goodsPru-order-"+orderNo);

            }else if(OrderTypeEnum.OrderType7.getCode().equals(doType)){
                List<Map> ticketList =(List) CacheUtil.get("pay","pre-pay-"+orderNo);
                OrderViewPO po=null;
                Map<Integer,Map> tickets=new HashMap();
                for(Map ticketMap:ticketList){

                    po=new OrderViewPO();
                    po.setBusId((Integer) ticketMap.get("ticketId"));
                    po.setCreateTime(new Date());
                    po.setName((String)ticketMap.get("ticketName"));
                    po.setHeadImg((String)ticketMap.get("ticketImgUrl"));
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setPrice((Integer) ticketMap.get("ticketPrice"));
                    po.setOrderType(OrderTypeEnum.OrderType7.getCode());

                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                    //计算商品销量
                    if(tickets.containsKey(po.getBusId())){
                        Map gm=tickets.get(po.getBusId());
                        gm.replace("num",(Integer)gm.get("num")+1);
                    }else{
                        Map g=new HashMap();
                        g.put("num",1);
                        g.put("time",(Date)ticketMap.get("currentUpdateTime"));
                        tickets.put(po.getBusId(),g);
                    }
                }
                Set<Integer> keys=tickets.keySet();
                String month=DateFormatUtils.format(new Date(),"MM");
                Map param=null;
                String[] calculatesName=null;
                Map s=null;
                for(Integer gid:keys){

                    String time=DateFormatUtils.format((Date)tickets.get(gid).get("time"),"MM");
                    Integer num=(Integer) tickets.get(gid).get("num");
                    param=new HashMap();
                    param.put("updateTime",new Date());
                    param.put("allSales",num);
                    param.put("monthSales",num);
                    s=new HashMap();
                    s.put("id",gid);
                    if(month.equals(time)){
                        calculatesName=new String[]{"allSales","monthSales"};
                    }else{
                        calculatesName=new String[]{"allSales"};
                    }
                    this.commonCalculateOptimisticLockUpdateByParam("ddw_ticket",param,s,"version",calculatesName);
                }
            }else if(OrderTypeEnum.OrderType10.getCode().equals(doType)){
                Integer price =(Integer) CacheUtil.get("pay","pre-pay-"+orderNo);
                OrderViewPO po=null;
                po=new OrderViewPO();
                po.setCreateTime(new Date());
                po.setName(OrderTypeEnum.OrderType10.getName());
                po.setHeadImg(null);
                po.setNum(1);
                po.setOrderId(OrderUtil.getOrderId(orderNo));
                po.setOrderNo(orderNo);
                po.setPrice(price);
                po.setOrderType(OrderTypeEnum.OrderType10.getCode());

                po.setUserId(cacheOrder.getDoCustomerUserId());
                po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                po.setShipStatus(cacheOrder.getDoShipStatus());
                po.setStoreId(cacheOrder.getDoSellerId());
                this.orderViewService.saveOrderView(po);
                Map setParams = new HashMap<>();
                setParams.put("payState",1);
                Map searchCondition = new HashMap<>();
                searchCondition.put("orderId",OrderUtil.getOrderId(orderNo));
                this.commonUpdateByParams("ddw_practice_order",setParams,searchCondition);
                CacheUtil.delete("pay","practice-order-"+cacheOrder.getDoCustomerUserId());
                CacheUtil.delete("pay","pre-pay-"+orderNo);
                CacheUtil.delete("pay","orderObject-"+orderNo);

            }/*else if(OrderTypeEnum.OrderType6.getCode().equals(doType)){
                String jsonStr =(String) CacheUtil.get("pay","pre-pay-"+orderNo);
                if(jsonStr==null){
                    throw new GenException("更新礼物支付状态失败");
                }
                JSONObject json=JSONObject.parseObject(jsonStr);
                Integer goddUserId=json.getInteger("goddessUserId");
                JSONArray giftArray=json.getJSONArray("giftList");
                JSONObject jsonGift=null;
                for(int i=0;i<giftArray.size();i++){
                    jsonGift=giftArray.getJSONObject(i);
                    Integer cost=jsonGift.getInteger("cost");
                    OrderViewPO po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName(jsonGift.getString("name"));
                    po.setHeadImg(jsonGift.getString("headImg"));
                    po.setNum(1);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setPrice(cost);
                    po.setOrderType(OrderTypeEnum.OrderType6.getCode());

                    po.setUserId(cacheOrder.getDoCustomerUserId());
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(cacheOrder.getDoShipStatus());
                    po.setStoreId(cacheOrder.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                    if(goddUserId>-1){
                        this.incomeService.commonIncome(goddUserId,cost,IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType6,orderNo);
                        this.baseConsumeRankingListService.save(cacheOrder.getDoCustomerUserId(),goddUserId,cost,IncomeTypeEnum.IncomeType1);
                    }
                }

                CacheUtil.delete("pay","pre-pay-"+orderNo);
            }*/
            if(StringUtils.isNotBlank(cacheOrder.getDoCouponNo())){
                Map setParam=new HashMap();
                setParam.put("used",1);
                Map search=new HashMap();
                search.put("couponId",Integer.parseInt(cacheOrder.getDoCouponNo()));
                search.put("userId",cacheOrder.getDoCustomerUserId());
                ResponseVO updatevo=this.commonUpdateByParams("ddw_userinfo_coupon",setParam,search);
                if(updatevo.getReCode()==-1){
                    throw new GenException("更新优惠卷失败");
                }
            }
            CacheUtil.put("pay","order-"+orderNo,"success");

            return new ResponseVO(1,"更新支付状态成功",null);

        }
        CacheUtil.put("pay","order-"+orderNo,"fail");
        return new ResponseVO(-2,"更新支付状态失败",null);


    }

    /**
     *
     * @param map ,key->orderId，value->退款金额(分钱)
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO baseExitOrderByMap(Map<Integer,Integer> map)throws Exception{
        List list=new ArrayList(map.keySet());
        return this.baseExitOrder(list,map);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO baseExitOrder(List<Integer> orderIds,Map ...exitOrderMaps)throws Exception{
        if(orderIds==null){
            return new ResponseVO(-2,"参数异常",null);

        }else if(orderIds.isEmpty()){
            return new ResponseVO(1,"成功",null);

        }
        ExitOrderPO exitOrderPO=null;
        Map searchMap=new HashMap();
        searchMap.put("id,in",orderIds.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
        searchMap.put("doPayStatus,<=",PayStatusEnum.PayStatus1.getCode());
        List<Map> orders=this.commonObjectsBySearchCondition("ddw_order",searchMap);
        Map<Integer,Integer> exitCostMap=null;
        if(exitOrderMaps!=null && exitOrderMaps.length>0){
            exitCostMap=exitOrderMaps[0];
        }
        Integer intBaseExitCost=null;
        for(Map o:orders){
            // orderPO=this.commonObjectBySingleParam("ddw_order","id",OrderUtil.getOrderId(o),OrderPO.class);
            if(exitCostMap!=null){
                intBaseExitCost=exitCostMap.get((Integer) o.get("id"));
            }
            exitOrderPO=new ExitOrderPO();
            exitOrderPO.setCreater((Integer) o.get("doCustomerUserId"));
            exitOrderPO.setCreaterName((String) o.get("creater"));
            exitOrderPO.setCreateTime(new Date());
            exitOrderPO.setTotalCost((Integer) o.get("doCost"));
            exitOrderPO.setExitCost(intBaseExitCost!=null?intBaseExitCost:exitOrderPO.getTotalCost());
            if(exitOrderPO.getExitCost()==null || exitOrderPO.getExitCost()<=0){
                throw new GenException("退款金额不能为0");

            }
            if(exitOrderPO.getExitCost()>exitOrderPO.getTotalCost()){
                throw new GenException("退款金额比总金额大");
            }
            exitOrderPO.setOrderId((Integer) o.get("id"));
            exitOrderPO.setOrderType((Integer)o.get("doType"));
            exitOrderPO.setOrderNo(OrderUtil.createOrderNo((String)o.get("doOrderDate"),(Integer) o.get("doType"),(Integer) o.get("doPayType"),exitOrderPO.getOrderId()));
            exitOrderPO.setExitOrderNo(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss")+ RandomStringUtils.randomNumeric(6));
            ResponseVO inserRes=this.commonInsert("ddw_exit_order",exitOrderPO);
            if(inserRes.getReCode()!=1){
                throw new GenException("新建退订单失败");
            }
            if(PayTypeEnum.PayType1.getCode().equals(o.get("doPayType"))){

                Map<String,String> callMap= PayApiUtil.reqeustWeiXinExitOrder(exitOrderPO.getOrderNo(),exitOrderPO.getExitOrderNo(),exitOrderPO.getTotalCost(),exitOrderPO.getExitCost());
                logger.info("reqeustWeiXinExitOrder->response->"+callMap);
                if(callMap==null ||  "FAIL".equals(callMap.get("return_code")) ||  "FAIL".equals(callMap.get("result_code"))){
                    throw new GenException("微信申请退款失败");
                }/*else if(callMap!=null && "SUCCESS".equals(callMap.get("return_code")) && "SUCCESS".equals(callMap.get("result_code"))){
                    CacheUtil.put("pay","weixin-refund-"+o,exitOrderPO.getExitOrderNo());

                }*/

            }else if(PayTypeEnum.PayType2.getCode().equals(o.get("doPayType"))){
                ResponseVO resvo=PayApiUtil.requestAliExitOrder(exitOrderPO.getOrderNo(),exitOrderPO.getExitCost());
                logger.info("reqeustAliExitOrder->response->"+resvo);
                if(resvo.getReCode()!=1){
                    throw new GenException("阿里申请退款失败");

                }
            }else if(PayTypeEnum.PayType5.getCode().equals(o.get("doPayType"))){
                Map walletSearchMap=new HashMap();
                walletSearchMap.put("userId",exitOrderPO.getCreater());
                Map setMap=new HashMap();
                setMap.put("money",exitOrderPO.getExitCost());
                setMap.put("updateTime",new Date());
                ResponseVO resvo= this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,walletSearchMap,"version",new String[]{"money"});
                logger.info("reqeustWalletOrder->response->"+resvo);
                if(resvo.getReCode()!=1){
                    throw new GenException("钱包退款失败");

                }
            }
            Map map=new HashMap();
            map.put("doPayStatus",PayStatusEnum.PayStatus2.getCode());
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",map,"id",exitOrderPO.getOrderId());
            if(res.getReCode()!=1){
                throw new GenException("申请退款失败->更新订单表失败");
            }
            map=new HashMap();
            map.put("payStatus",PayStatusEnum.PayStatus2.getCode());
            map.put("shipStatus",ClientShipStatusEnum.ShipStatus4.getCode());
            res=this.commonUpdateBySingleSearchParam("ddw_order_view",map,"orderId",exitOrderPO.getOrderId());
            if(res.getReCode()!=1){
                throw new GenException("申请退款失败->更新订单视图表失败");

            }
            //ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_order",params,"id",orderid);
            intBaseExitCost=null;

        }
        return new ResponseVO(1,"成功",null);


    }
}
