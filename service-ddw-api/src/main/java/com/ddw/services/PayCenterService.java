package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.ApiConstant;
import com.ddw.util.PayApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.services.FileService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PayCenterService extends BaseOrderService {
    private final Logger logger = Logger.getLogger(PayCenterService.class);



    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private DoubiClientService doubiClientService;
    @Autowired
    private ReviewPracticeService reviewPracticeService;

    @Autowired
    private UserGradeService userGradeService;

    @Autowired
    private FileService fileService;

    @Autowired
    private DDWGlobals ddwGlobals;

    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private BaseBiddingService baseBiddingService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO searchPayStatus(String token,PayStatusDTO dto)throws Exception{
        String orderNo=TokenUtil.getOrderNo(token);
        if(dto==null || StringUtils.isBlank(orderNo)){
            return new ResponseApiVO(-2,"参数异常",null);

        }

        String paystatus=null;
        for(int i=1;i<=3;i++){
            paystatus=(String)CacheUtil.get("pay","order-"+orderNo);
            if(paystatus==null){
                Thread.sleep(i*200);
                continue;
            }else{
                break;
            }
        }
        if(paystatus==null){
            Map map=new HashMap();
            if(CacheUtil.get("pay","pre-pay-"+orderNo) ==null){
                return new ResponseApiVO(-2,"抱歉，没有支付记录",null);

            }
            map.put("doCustomerUserId",TokenUtil.getUserId(token));
            map.put("id",OrderUtil.getOrderId(orderNo));
            Map voMap=this.commonObjectBySearchCondition("ddw_order",map);
            if(voMap==null || !voMap.containsKey("doPayStatus")){
                return new ResponseApiVO(-2,"支付记录不存在",null);

            }
            /*Date endTime=(Date)voMap.get("doEndTime");
            if(endTime.before(new Date())){
                return new ResponseApiVO(-3,"支付失败，已超时",null);
            }*/
            Integer doPayStatus=(Integer) voMap.get("doPayStatus");
            if(PayStatusEnum.PayStatus1.getCode().equals(doPayStatus)){
                return new ResponseApiVO(1,"支付成功",null);

            }else{
               Integer payType=(Integer) voMap.get("doPayType");
               boolean flag=false;
               if(PayTypeEnum.PayType1.getCode().equals(payType)){
                   logger.info("请求微信支付-》查看订单情况->"+dto);
                   RequestWeiXinOrderVO res= PayApiUtil.weiXinOrderQuery(orderNo);
                   logger.info("微信支付响应-》查看订单情况->"+res);
                   flag=res!=null && "SUCCESS".equals(res.getReturn_code()) && "SUCCESS".equals(res.getResult_code()) && "SUCCESS".equals(res.getTrade_state());

               }else if(PayTypeEnum.PayType2.getCode().equals(payType)){
                   logger.info("请求阿里支付-》查看订单情况->"+dto);
                   ResponseVO res= PayApiUtil.aliPayOrderQuery(orderNo);
                   logger.info("阿里支付响应-》查看订单情况->"+res);
                   flag=res!=null && (res.getReCode()==1 || res.getReCode().equals(1));
               }
               if(flag){
                   Map param=new HashMap();
                   param.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
                   ResponseVO orderRes=this.pulbicUpdateOrderPayStatus(PayStatusEnum.PayStatus1,orderNo);
                   if(orderRes.getReCode()!=1){
                       logger.info("更新订单表-》失败->"+orderRes);
                      // return new ResponseApiVO(-2,"支付失败",null);
                   }else{
                       logger.info("更新订单表-》成功->"+orderRes);
                   }
                   return new ResponseApiVO(1,"支付成功",null);

               }else{
                   handleReFund(orderNo);
                   return new ResponseApiVO(-2,"支付失败",null);

               }

            }

        }else if("success".equals(paystatus)){
            return new ResponseApiVO(1,"支付成功",null);

        }else if("fail".equals(paystatus)){
            handleReFund(orderNo);
            return new ResponseApiVO(-3,"支付失败",null);

        }else if("refund".equals(paystatus)){
            return new ResponseApiVO(-5,"支付失败，稍会自动退款",null);

        }
        return new ResponseApiVO(-4,"支付处理中，请稍等",null);

    }
    private void handleReFund(String orderNo)throws Exception{
        ResponseApiVO res=this.exitOrder(Arrays.asList(OrderUtil.getOrderId(orderNo)));
        if(res.getReCode()==1){
            logger.info("退款："+res);
            CacheUtil.put("pay","order-"+orderNo,"refund");
        }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO doubiPayOnMarket(String token,DoubiPayMarketDTO dto )throws Exception{
        if(dto==null || dto.getItems()==null|| dto.getItems().isEmpty()){
            return new ResponseApiVO(-2,"参数异常",null);
        }else{
            PayDTO payDTO=new PayDTO();
            Map m=new HashMap();
            List<DoubiPayMarketItemDTO> ds=dto.getItems();
            for(DoubiPayMarketItemDTO d:ds){
                if(d.getCode()==null || d.getCode()<=0){
                    return new ResponseApiVO(-2,"参数异常",null);
                }
                m.put(d.getCode(),d.getNum()==null || d.getNum()<=0?1:d.getNum());

            }
            payDTO.setOrderType(OrderTypeEnum.OrderType6.getCode());
            payDTO.setDouBiDtos(m);
            return prePay(token,PayTypeEnum.PayType4.getCode(),payDTO);
        }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO doubiPay(String token,DoubiPayDTO dto)throws Exception{
        PayDTO payDTO=new PayDTO();
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"参数异常",null);
        }
        PropertyUtils.copyProperties(payDTO,dto);
        int num=dto.getNum()==null || dto.getNum()<=0?1:dto.getNum();
        Integer[] codes=new Integer[num];
        for(int i=0;i<num;i++){
            codes[i]=dto.getCode();
        }
        payDTO.setCodes(codes);
        return prePay(token,PayTypeEnum.PayType4.getCode(),payDTO);
    }
    /**
     * 预支付
     * @param token
     * @param payType
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO prePay(String token, Integer payType, PayDTO dto)throws Exception{
        Integer cost= dto.getMoney();
        Integer orderType=dto.getOrderType();
        Integer[] codes=dto.getCodes();
        Integer couponCode=dto.getCouponCode();
        String groupId=dto.getGroupId();
        String tableNo=dto.getTableNo();
        if(StringUtils.isBlank(PayTypeEnum.getName(payType))){
            return new ResponseApiVO(-2,"请选择有效的支付方式",null);
        }
        if(StringUtils.isBlank(OrderTypeEnum.getName(orderType))){
            return new ResponseApiVO(-2,"请选择有效的订单类型",null);

        }
        if(!PayTypeEnum.PayType4.getCode().equals(payType)&& OrderTypeEnum.OrderType6.getCode().equals(orderType)){
            return new ResponseApiVO(-2,"抱歉，礼物只能逗币购买",null);

        }
        if((dto.getDouBiDtos()==null || dto.getDouBiDtos().isEmpty()) && (codes==null || codes.length==0)){
            return new ResponseApiVO(-2,"业务编号不能是空",null);

        }

        Integer userId=TokenUtil.getUserId(token);
        Integer gradeId=TokenUtil.getUseGrade(token);

        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),1));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoCustomerUserId(userId);

        orderPO.setDoCustomerStoreId(-1);
        orderPO.setDoPayType(payType);
        orderPO.setDoCustomerType(OrderCustomerTypeEnum.OrderCustomerType0.getCode());
        orderPO.setDoType(orderType);
        orderPO.setCreater(TokenUtil.getUserName(token));
        if(StringUtils.isNotBlank(tableNo)){
            orderPO.setDoExtendStr("桌号-"+tableNo);
        }
        Map<Integer,Map> buyInProMap=null;
        //GiftPO gift=null;
        Map voData=null;
        List<Map> insertList=null;
        PracticeOrderPO practiceOrderPO=null;
        Integer currentOrderId=null;
        Set<String> imgs=null;
        //定金
        if(OrderTypeEnum.OrderType4.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));


            String ub=userId+"-"+codes[0];
            String str=(String)CacheUtil.get("pay","bidding-earnest-pay-"+ub);
            if(str==null){
                throw new GenException("定金支付失败");

            }
            String strs[]=str.split("-");
            Integer earnest=Integer.parseInt(strs[0]);
            orderPO.setDoCost(earnest);
            orderPO.setDoOrigCost(earnest);
            orderPO.setDoOrigActCost(earnest);

       // 竞价金额
        }else if(OrderTypeEnum.OrderType5.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            Integer bidCost=0;
            Map bidMap=null;

            for(Integer code:codes){
                bidMap=this.baseBiddingService.getBiddingPay(userId,code);
                if(bidMap==null){
                    return new ResponseApiVO(-2,"竞价金额支付失败",null);

                }else if(CacheUtil.get("publicCache","bidding-finish-pay-"+code+"-"+ bidMap.get("goddessUserId"))!=null){
                    return new ResponseApiVO(-2,"竞价金额已支付",null);
                }else{
                    bidCost=bidCost+Integer.parseInt((String) bidMap.get("needPayPrice"));
                }

            }

            orderPO.setDoCost(bidCost);
            orderPO.setDoOrigCost(bidCost);
            orderPO.setDoOrigActCost(bidCost);


        }else if(OrderTypeEnum.OrderType9.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            Map renewMap=(Map)CacheUtil.get("pay","bidding-renew-"+userId+"-"+codes[0]);
            if(renewMap==null){
                return new ResponseApiVO(-2,"约玩续费失败",null);

            }
            Integer bidCost=(Integer) renewMap.get("price");
            orderPO.setDoCost(bidCost);
            orderPO.setDoOrigCost(bidCost);
            orderPO.setDoOrigActCost(bidCost);

            //计算充值
        }else if(OrderTypeEnum.OrderType3.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(-1);
            voData=rechargeService.getRechargeCost(codes[0]);
            orderPO.setDoCost((Integer) voData.get("discount"));
            orderPO.setDoOrigCost((Integer) voData.get("price"));
            orderPO.setDoOrigActCost((Integer) voData.get("discount"));
            if(orderPO.getDoCost()==null){
                return new ResponseApiVO(-2,"充值卷编号异常",null);
            }
        //计算逗币充值
        }else if(OrderTypeEnum.OrderType8.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(-1);
            Map map=doubiClientService.getObject(codes[0]);
            if(map==null){
                return new ResponseApiVO(-2,"充值卷编号异常",null);

            }
            Integer price=(Integer) map.get("price");
            Integer discount=(Integer) map.get("discount");
            Integer recCost=discount==null?price:discount;
            orderPO.setDoCost(recCost);
            orderPO.setDoOrigCost(price);
            orderPO.setDoOrigActCost(recCost);
            if(orderPO.getDoCost()==null){
                return new ResponseApiVO(-2,"充值卷编号异常",null);
             }
        //计算货品
        }else if(OrderTypeEnum.OrderType1.getCode().equals(orderType)){
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());

            buyInProMap=new HashMap();
            List<Integer> codesList=Arrays.asList(codes);
            Map search=new HashMap();
            search.put("storeId",TokenUtil.getStoreId(token));
            search.put("dghStatus",GoodsStatusEnum.goodsStatus1.getCode());
            search.put("id,in",codesList.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
            CommonSearchBean csb=new CommonSearchBean("ddw_goods_product",null,"t1.dghActivityPrice,t1.dghSalesPrice,t1.dghFormulaId,t1.dghDesc,t1.id,ct0.fileImgIcoPath headImg,t1.dghGoodsId gId,ct0.updateTime",null,null,search,new CommonChildBean("ddw_goods","id","dghGoodsId",null));
            List<Map> goodsPruductList=this.getCommonMapper().selectObjects(csb);
           // List<Map> goodsPruductList= this.commonObjectsBySearchCondition("ddw_goods_product",search);
            if(goodsPruductList==null || goodsPruductList.isEmpty()){
                return new ResponseApiVO(-2,"商品不存在",null);
            }
            Map<Integer,Map> handleMap=new HashMap();
            goodsPruductList.forEach(a->{handleMap.put((Integer) a.get("id"),a);});

            Map dataMap=null;
            Integer countPrice=0;
            Integer countOrigPrice=0;
            Integer countOrigActPrice=0;
            Map mVo=null;
            BigDecimal dicount=null;
            if(gradeId!=null){
                dicount=this.userGradeService.getDiscount(gradeId);
                orderPO.setDoDicount(dicount);
            }
            if(codesList.size()>1){
                imgs=new HashSet<>();
            }
            for(Integer code:codesList){
                if(!handleMap.containsKey(code)){
                    return new ResponseApiVO(-2,handleMap.get(code).get("dghDesc")+"可能已下架",null);
                }
                mVo=handleMap.get(code);
                countOrigPrice=countOrigPrice+(Integer)mVo.get("dghSalesPrice");
                if(buyInProMap.containsKey(code)){
                    dataMap=buyInProMap.get(code);
                    Integer sale=(Integer) dataMap.get("productUnitPrice");
                    countPrice=countPrice+sale;

                    dataMap.put("productCountPrice",(Integer)dataMap.get("productCountPrice")+sale);
                    dataMap.put("productBuyNumber",(Integer)dataMap.get("productBuyNumber")+1);
                }else{

                    dataMap=new HashMap();
                    dataMap.put("productId",code);

                    Integer sale=mVo.get("dghActivityPrice")==null?(Integer)mVo.get("dghSalesPrice"):(Integer)mVo.get("dghActivityPrice");

                    countPrice=countPrice+sale;
                    dataMap.put("productCountPrice",sale);
                    dataMap.put("productUnitPrice",sale);
                    dataMap.put("productBuyNumber",1);
                    dataMap.put("updateTime",new Date());
                    dataMap.put("createTime",new Date());
                    dataMap.put("productName",mVo.get("dghDesc"));
                    dataMap.put("headImg",mVo.get("headImg"));
                    dataMap.put("gid",mVo.get("gId"));
                    dataMap.put("currentUpdateTime",(Date)mVo.get("updateTime"));
                    if(imgs!=null)imgs.add(mVo.get("headImg").toString().replaceAll("(.*/rs/)(.*)",mainGlobals.getRsDir()+"$2"));
                    buyInProMap.put(code,dataMap);
                }

            }
            if(countPrice==null || countPrice<=0){
                return new ResponseApiVO(-2,"支付失败",null);
            }
            orderPO.setDoCost(countPrice);
            orderPO.setDoOrigActCost(countPrice);
            orderPO.setDoOrigCost(countOrigPrice);
            if(couponCode==null && dicount!=null){
                orderPO.setDoCost(dicount.multiply(BigDecimal.valueOf(countPrice)).intValue());
            }

            ResponseApiVO couponVo=this.executeCoupon(orderPO,couponCode,token);
            if(couponVo.getReCode()!=1){
                return couponVo;
            }


            if(StringUtils.isNotBlank(orderPO.getDoCouponNo()) && dicount!=null){
                orderPO.setDoCost(dicount!=null?dicount.multiply(BigDecimal.valueOf(orderPO.getDoCost())).intValue():orderPO.getDoCost());

            }



        //礼物
        }else if(OrderTypeEnum.OrderType6.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(-1);
            ResponseApiVO<WalletDoubiVO> res=this.walletService.getCoin(userId);
            if(res.getReCode()!=1){
                return new ResponseApiVO(-2,"支付失败",null);

            }

            WalletDoubiVO walletVo=res.getData();
            if(walletVo.getCoin()==null || walletVo.getCoin()<=0){
                return new ResponseApiVO(-2,"逗币不足，请充值",null);

            }
            Collection<Integer> codesList=null;
            Map<Integer,Integer> douBiMap=null;
            if(dto.getDouBiDtos()==null || dto.getCodes()!=null){
               codesList=Arrays.asList(dto.getCodes());
            }else{
                douBiMap=dto.getDouBiDtos();
                codesList=douBiMap.keySet();
            }
            Map search=new HashMap();
            search.put("id,in",codesList.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
            //  search.put("id",codes[0]);
            search.put("dgDisabled",DisabledEnum.disabled0.getCode());
            List<Map> giftList=this.commonObjectsBySearchCondition("ddw_gift",search);
            if(giftList==null){
                return new ResponseApiVO(-2,"礼物不存在",null);
            }
            buyInProMap=new HashMap();
            final Map<Integer,Map> buyInGoiftMap=new HashMap();
            giftList.forEach(a-> buyInGoiftMap.put((Integer) a.get("id"),a));
            Integer coutCost=0;
            Integer coutOrigCost=0;
            Integer actPrice=null;
            Integer price=null;
            Map insertM=null;
            Integer giftPrice=0;
            Integer num=null;
            if(codesList.size()>1){
                imgs=new HashSet<>();
            }
            for(Integer code:codesList){
                if(!buyInGoiftMap.containsKey(code)){
                    return new ResponseApiVO(-2,buyInGoiftMap.get(code).get("dgName")+"可能已下架",null);
                }

                actPrice=(Integer)buyInGoiftMap.get(code).get("dgActPrice");
                price=(Integer)buyInGoiftMap.get(code).get("dgPrice");
                giftPrice=actPrice!=null && actPrice>0?actPrice:price;
                if(buyInProMap.containsKey(code)){
                    insertM=buyInProMap.get(code);
                    Integer currentPrice=(Integer) insertM.get("giftPrice");
                    num=(Integer) insertM.get("num");
                    insertM.replace("giftPrice",currentPrice+giftPrice);
                    insertM.replace("num",num+1);
                    coutCost=coutCost+giftPrice;
                    coutOrigCost=coutOrigCost+price;
                }else{
                    num=douBiMap!=null?douBiMap.get(code):1;
                    insertM=new HashMap();
                    insertM.put("createTime",new Date());
                    insertM.put("updateTime",new Date());
                    insertM.put("giftId",code);
                    insertM.put("giftName",buyInGoiftMap.get(code).get("dgName").toString());
                    insertM.put("giftImg",buyInGoiftMap.get(code).get("dgImgPath").toString());
                    insertM.put("giftPrice",giftPrice*num);
                    insertM.put("userId",orderPO.getDoCustomerUserId());
                    insertM.put("num",num);
                    buyInProMap.put(code,insertM);
                    coutCost=coutCost+(giftPrice*num);
                    coutOrigCost=coutOrigCost+(price*num);
                    if(imgs!=null){
                        imgs.add(insertM.get("giftImg").toString().replaceAll("(.*/rs/)(.*)",mainGlobals.getRsDir()+"$2"));
                    }
                }


            }
            if(coutCost>walletVo.getCoin()){
                return new ResponseApiVO(-2,"逗币不足，请充值",null);
            }
            orderPO.setDoCost(coutCost);
            orderPO.setDoOrigActCost(coutCost);
            orderPO.setDoOrigCost(coutOrigCost);
            orderPO.setDoPayStatus(PayStatusEnum.PayStatus1.getCode());
            //buyInProMap=buyInGoiftMap;
        }else if(OrderTypeEnum.OrderType7.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());

            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            List<Integer> codesList=Arrays.asList(codes);
            Map search=new HashMap();
            search.put("dtDisabled",DisabledEnum.disabled0.getCode());
            search.put("id,in",codesList.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
            List<Map> tocketList=this.commonObjectsBySearchCondition("ddw_ticket",search);
            if(tocketList==null){
                return new ResponseApiVO(-2,"门票不存在",null);
            }
            final Map<Integer,Map> buyInTicketMap=new HashMap();
            for(Map vd:tocketList){
                buyInTicketMap.put((Integer) vd.get("id"),vd);
            }
            insertList=new ArrayList();
            Map orderTicket=null;
            Map ticketMap=null;
            Integer sumPrice=0;
            Integer origPrice=0;
            Integer price=0;
            for(Integer id:codesList){
                orderTicket=new HashMap();
                ticketMap=buyInTicketMap.get(id);
                price= ticketMap.get("dtActPrice")==null?(Integer)ticketMap.get("dtPrice"):(Integer)ticketMap.get("dtActPrice");
                sumPrice=sumPrice+price;
                origPrice=origPrice+(Integer)ticketMap.get("dtPrice");
                orderTicket.put("ticketId",id);
                orderTicket.put("storeId",TokenUtil.getStoreId(token));
                orderTicket.put("ticketName",ticketMap.get("dtName")+"【"+ticketMap.get("dtActiveTime")+"】");
                orderTicket.put("ticketPrice",price);
                orderTicket.put("ticketImgUrl",ticketMap.get("dtImgPath"));
                orderTicket.put("currentUpdateTime",ticketMap.get("updateTime"));
                insertList.add(orderTicket);
            }
            orderPO.setDoCost(sumPrice);
            orderPO.setDoOrigActCost(sumPrice);
            orderPO.setDoOrigCost(origPrice);
        }else if(OrderTypeEnum.OrderType10.getCode().equals(orderType)){
            orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus5.getCode());
            orderPO.setDoSellerId(TokenUtil.getStoreId(token));
            practiceOrderPO=(PracticeOrderPO)CacheUtil.get("pay","practice-order-"+userId);
            if(practiceOrderPO==null) {
                return new ResponseApiVO(-2, "代练订单支付失败", null);

            }else if(StringUtils.isNotBlank(practiceOrderPO.getOrderNo())){
                currentOrderId=practiceOrderPO.getOrderId();
                RequestWeiXinOrderVO v=PayApiUtil.weiXinOrderQuery(practiceOrderPO.getOrderNo());
                if("SUCCESS".equals(v.getReturn_code()) && "SUCCESS".equals(v.getResult_code()) && "SUCCESS".equals(v.getTrade_state())){
                    return new ResponseApiVO(-2, "请稍等，支付正在处理中", null);
                }else{
                    orderPO= this.getCacheOrder(practiceOrderPO.getOrderNo());
                }


            }else{
                orderPO.setDoCost(practiceOrderPO.getMoney());
                orderPO.setDoOrigActCost(practiceOrderPO.getMoney());
                orderPO.setDoOrigCost(practiceOrderPO.getMoney());

            }
        }
        if(PayTypeEnum.PayType5.getCode().equals(payType)){
            if(StringUtils.isBlank(TokenUtil.getPayCode(token))){
                return new ResponseApiVO(-2,"密码口令无效",null);
            }else{
                orderPO.setDoPayStatus(PayStatusEnum.PayStatus1.getCode());
            }
        }
        ResponseVO<Integer> insertResponseVO=null;
        if(currentOrderId==null){
            if(orderPO.getDoCost()==null || orderPO.getDoCost()<=0){
                return new ResponseApiVO(-2,"支付金额异常",null);
            }
            if(imgs!=null && imgs.size()>1){
                StringBuilder imgBuiler=new StringBuilder();
                imgBuiler.append(mainGlobals.getRsDir()).append("order/").append(orderPO.getDoOrderDate()).append(".jpg");
                fileService.mergeImages(imgs,imgBuiler.toString());
                orderPO.setDoMergePicPath(imgBuiler.toString().replaceAll("(.*)(/rs/.*)",ddwGlobals.getCallBackHost()+"$2"));
            }
            insertResponseVO=this.commonInsert("ddw_order",orderPO);
        }else{
            insertResponseVO=new ResponseVO<>(1,"成功",currentOrderId);
        }


        if(insertResponseVO.getReCode()==1){
            Object orderCacheData=orderType;
            String orderNo= OrderUtil.createOrderNo(orderPO.getDoOrderDate(),orderType,payType,insertResponseVO.getData());
            ResponseVO<Integer> resVo=null;
            if(orderType.equals(OrderTypeEnum.OrderType3.getCode())){
                Map m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dorCost",voData.get("price"));
                m.put("dorActCost",voData.get("discount"));
                m.put("rechargeId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_recharge",m);
            }if(orderType.equals(OrderTypeEnum.OrderType8.getCode())){
                Map m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dodCost",orderPO.getDoCost());
                m.put("doubiId",codes[0]);
                Map map=doubiClientService.getObject(codes[0]);
                m.put("dodDoubiNum",map.get("doubiNum"));
                resVo=this.commonInsertMap("ddw_order_doubi",m);
            }else if(orderType.equals(OrderTypeEnum.OrderType4.getCode()) ){
                String ub=userId+"-"+codes[0];
                String str=(String)CacheUtil.get("pay","bidding-earnest-pay-"+ub);

                if(str==null){
                    throw new GenException("定金支付超时");

                }
                String strs[]=str.split("-");
                Integer earnest=Integer.parseInt(strs[0]);
                Map m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dorCost",earnest);
                m.put("biddingId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
                orderCacheData=ub;
            }else if(orderType.equals(OrderTypeEnum.OrderType5.getCode())){
                Map payMap=null;
                Map m=null;
                List list=new ArrayList();
                String ub=null;
                for(Integer code:codes){
                    ub=userId+"-"+code;
                    payMap=this.baseBiddingService.getBiddingPay(userId,code);
                    if(payMap==null){
                        throw new GenException("竞价金额支付超时");
                    }
                    m=new HashMap();
                    m.put("orderId",insertResponseVO.getData());
                    m.put("orderNo",orderNo);
                    m.put("createTime",new Date());
                    m.put("updateTime",new Date());
                    m.put("creater",userId);
                    m.put("dorCost",payMap.get("needPayPrice"));
                    m.put("times",payMap.get("time"));
                    m.put("biddingId",code);
                    resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
                    if(resVo.getReCode()!=1){
                        throw new GenException("竞价金额支付失败");

                    }
                    list.add(ub);

                }
                orderCacheData=list;

            }else if(orderType.equals(OrderTypeEnum.OrderType9.getCode())){
                Map renewMap=(Map)CacheUtil.get("pay","bidding-renew-"+userId+"-"+codes[0]);
                Map  m=new HashMap();
                m.put("orderId",insertResponseVO.getData());
                m.put("orderNo",orderNo);
                m.put("createTime",new Date());
                m.put("updateTime",new Date());
                m.put("creater",userId);
                m.put("dorCost",renewMap.get("price"));
                m.put("times",renewMap.get("time"));
                m.put("biddingId",codes[0]);
                resVo=this.commonInsertMap("ddw_order_bidding_pay",m);
                if(resVo.getReCode()!=1){
                    throw new GenException("约玩续费失败");

                }
                orderCacheData=renewMap;
            }else if(OrderTypeEnum.OrderType1.getCode().equals(orderType)){
                if(buyInProMap==null){
                    throw new GenException("商品支付失败");
                }
                Collection collection=buyInProMap.values();
                Iterator<Map> iterator=collection.iterator();
                Map insertM=null;
                String headImg=null;
                Integer gid=null;
                Date currentUpdateTime=null;
                while(iterator.hasNext()){
                    insertM=iterator.next();
                    insertM.put("orderNo",orderNo);
                    insertM.put("orderId",insertResponseVO.getData());
                    headImg=(String)insertM.get("headImg");
                    gid=(Integer) insertM.get("gid");
                    currentUpdateTime=(Date) insertM.get("currentUpdateTime");
                    insertM.remove("headImg");
                    insertM.remove("gid");
                    insertM.remove("currentUpdateTime");
                    resVo=this.commonInsertMap("ddw_order_product",insertM);
                    if(resVo.getReCode()!=1){
                        throw new GenException("商品支付失败");

                    }
                    insertM.put("headImg",headImg);
                    insertM.put("gid",gid);
                    insertM.put("currentUpdateTime",currentUpdateTime);
                }
                CacheUtil.put("pay","goodsPru-order-"+orderNo,new ArrayList(collection));
            }else if(OrderTypeEnum.OrderType6.getCode().equals(orderType)){

                Integer goddessUserId=-1;
                if(StringUtils.isNotBlank(groupId)) {
                    LiveRadioPO po = this.liveRadioService.getLiveRadioByGroupId(groupId);
                    if (po == null) {
                        throw new GenException("群组号不存在");
                    }
                    goddessUserId=po.getUserid();
                }
                Map giftMap=null;
                Integer giftPrice=null;
                Integer price=null;
               // List giftList=new ArrayList();
                Set<Integer> keys=buyInProMap.keySet();
                Integer num=0;
                String giftImg=null;


                Map searchGift=new HashMap();
                searchGift.put("giftId,in",keys.toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
                searchGift.put("userId",userId);
                List<Map> giftPackList=this.commonObjectsBySearchCondition("ddw_packet_gift",searchGift);
                final Map<Integer,Map> giftPackMap=new HashMap();
                if(giftPackList!=null){

                    giftPackList.forEach(a->giftPackMap.put((Integer) a.get("giftId"),a));
                }
                for(Integer code:keys){
                    giftMap=buyInProMap.get(code);

                    giftPrice=(Integer)giftMap.get("giftPrice");
                    giftMap.put("orderNo",orderNo);
                    giftMap.put("orderId",insertResponseVO.getData());

                   // cacheMap.put("cost",actPrice!=null && actPrice>0?actPrice:price);
                   // cacheMap.put("headImg",giftMap.get("dgImgPath"));
                   // cacheMap.put("name",giftMap.get("dgName").toString());
                   // giftList.add(cacheMap);

                    num=(Integer) giftMap.get("num");
                    giftImg=(String) giftMap.get("giftImg");
                    if(StringUtils.isBlank(groupId)){
                        Map edit=new HashMap();
                        edit.put("currentNum",num);
                        edit.put("accumNum",num);
                        edit.put("updateTime",new Date());
                        if(giftPackMap.containsKey(code)){
                            Map giftpMap=giftPackMap.get(code);
                            Map search=new HashMap();
                            search.put("id",giftpMap.get("id"));
                            resVo=this.commonCalculateOptimisticLockUpdateByParam("ddw_packet_gift",edit,search,"version",new String[]{"currentNum","accumNum"});

                        }else{
                            edit.put("createTime",new Date());
                            edit.put("userId",userId);
                            edit.put("giftId",code);
                            resVo=this.commonInsertMap("ddw_packet_gift",edit);
                        }
                        if(resVo.getReCode()!=1){
                            throw new GenException("礼物支付失败");
                        }

                    }
                    giftMap.remove("giftImg");
                    resVo=this.commonInsertMap("ddw_order_gift",giftMap);
                    if(resVo.getReCode()!=1){
                        throw new GenException("礼物支付失败");
                    }
                    OrderViewPO po=new OrderViewPO();
                    po.setCreateTime(new Date());
                    po.setName(giftMap.get("giftName").toString());
                    po.setHeadImg(giftImg);
                    po.setNum(num);
                    po.setOrderId(OrderUtil.getOrderId(orderNo));
                    po.setOrderNo(orderNo);
                    po.setPrice(giftPrice);
                    po.setOrderType(OrderTypeEnum.OrderType6.getCode());

                    po.setUserId(userId);
                    po.setPayStatus(PayStatusEnum.PayStatus1.getCode());
                    po.setShipStatus(ShipStatusEnum.ShipStatus2.getCode());
                    po.setStoreId(orderPO.getDoSellerId());
                    this.orderViewService.saveOrderView(po);
                    if(goddessUserId>-1){
                        //ddw_give_gift_record
                        ResponseVO res=this.incomeService.commonIncome(goddessUserId,po.getPrice()*10,IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType6,orderNo);
                        if(res.getReCode()==1){
                            Map giveGift=new HashMap();
                            giveGift.put("userId",userId);
                            giveGift.put("acceptUserId",goddessUserId);
                            giveGift.put("num",num);
                            giveGift.put("giftId",code);
                            giveGift.put("incomeRecordId",res.getData());
                            giveGift.put("incomeType",IncomeTypeEnum.IncomeType1.getCode());
                            giveGift.put("createTime",new Date());
                            giveGift.put("updateTime",new Date());
                            this.commonInsertMap("ddw_give_gift_record",giveGift);

                        }
                        this.baseConsumeRankingListService.save(userId,goddessUserId,po.getPrice()*10,IncomeTypeEnum.IncomeType1);
                    }
                }

                Map setParams=new HashMap();
                setParams.put("coin",-(orderPO.getDoCost()));
                Map condition=new HashMap();
                condition.put("userId",TokenUtil.getUserId(token));
                ResponseVO wres=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,condition,"version",new String[]{"coin"});
                if(wres.getReCode()!=1){
                    throw new GenException("更新钱包逗币失败");
                }
                return new ResponseApiVO(1,"成功",null);
            }else if(OrderTypeEnum.OrderType7.getCode().equals(orderType)){
                Date currentUpdateTime=null;
                for(Map im:insertList){
                    im.put("orderId",insertResponseVO.getData());
                    im.put("orderNo",orderNo);
                    im.put("createTime",new Date());
                    im.put("updateTime",new Date());
                    currentUpdateTime=(Date)im.get("currentUpdateTime");
                    im.remove("currentUpdateTime");
                    resVo=this.commonInsertMap("ddw_order_ticket",im);
                    im.put("currentUpdateTime",currentUpdateTime);
                    if(resVo.getReCode()!=1){
                        throw new GenException("门票支付失败");
                    }

                }
                orderCacheData=insertList;
            }else if(OrderTypeEnum.OrderType10.getCode().equals(orderType)){
                Map setParams = new HashMap<>();
                setParams.put("orderId",insertResponseVO.getData());
                setParams.put("orderNo",orderNo);
                setParams.put("updateTime",new Date());
                Map searchCondition = new HashMap<>();
                searchCondition.put("id",codes[0]);
                resVo=this.commonUpdateByParams("ddw_practice_order",setParams,searchCondition);
                if(resVo.getReCode()!=1){
                    throw new GenException("代练支付失败");
                }
                if(StringUtils.isBlank(practiceOrderPO.getOrderNo())){
                    practiceOrderPO.setOrderNo(orderNo);
                    practiceOrderPO.setOrderId(insertResponseVO.getData());
                    CacheUtil.put("pay","practice-order-"+userId,practiceOrderPO);
                }
                orderCacheData=codes[0];
            }

            if(resVo.getReCode()==1){
                if(PayTypeEnum.PayType1.getCode().equals(payType)){
                    /*RequestWeiXinOrderVO vo=new RequestWeiXinOrderVO();
                    vo.setReturn_code("SUCCESS");
                    vo.setResult_code("SUCCESS");
                    vo.setPrepay_id(RandomStringUtils.randomAlphabetic(10));*/
                    RequestWeiXinOrderVO vo=PayApiUtil.requestWeiXinOrder("微信"+OrderTypeEnum.getName(orderType)+"-"+((double)orderPO.getDoCost()/100)+"元",orderNo,orderPO.getDoCost(), Tools.getIpAddr());
                    if(vo!=null && "SUCCESS".equals(vo.getReturn_code()) && "SUCCESS".equals(vo.getResult_code())){
                        TreeMap treeMap=new TreeMap();
                        treeMap.put("appid", ApiConstant.WEI_XIN_PAY_APP_ID);
                        treeMap.put("partnerid", ApiConstant.WEI_XIN_PAY_MCH_ID);
                        treeMap.put("prepayid",vo.getPrepay_id());
                        treeMap.put("package","Sign=WXPay");
                        treeMap.put("noncestr", RandomStringUtils.randomAlphanumeric(20)+"");
                        treeMap.put("timestamp",new Date().getTime()/1000+"");
                        Set<String> keys=treeMap.keySet();
                        StringBuilder builder=new StringBuilder();
                        for(String key:keys){
                            builder.append(key).append("=").append(treeMap.get(key)).append("&");
                        }
                        builder.append("key=").append(ApiConstant.WEI_XIN_PAY_KEY);
                       // builder.deleteCharAt(builder.length()-1);
                        treeMap.put("sign", DigestUtils.md5Hex(builder.toString()).toUpperCase());
                        treeMap.put("packages",treeMap.get("package"));
                        treeMap.remove("package");
                        PayCenterWeixinPayVO wxVo=new PayCenterWeixinPayVO();
                        PropertyUtils.copyProperties(wxVo,treeMap);
                        wxVo.setOrderNo(orderNo);
                        CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                        CacheUtil.put("pay","orderObject-"+orderNo,JSONObject.toJSONString(orderPO));
                        TokenUtil.putOrderNo(token,orderNo);
                        return new ResponseApiVO(1,"成功",wxVo);
                    }else{
                        throw new GenException(-2,"调用微信支付接口失败",vo);
                    }
                }if(PayTypeEnum.PayType6.getCode().equals(payType)){
                    /*RequestWeiXinOrderVO vo=new RequestWeiXinOrderVO();
                    vo.setReturn_code("SUCCESS");
                    vo.setResult_code("SUCCESS");
                    vo.setPrepay_id(RandomStringUtils.randomAlphabetic(10));*/
                    RequestWeiXinOrderVO vo=PayApiUtil.requestWeiXinOrderByJsapi("微信"+OrderTypeEnum.getName(orderType)+"-"+((double)orderPO.getDoCost()/100)+"元",orderNo,orderPO.getDoCost(), Tools.getIpAddr(),TokenUtil.getRealOpenId(token));
                    if(vo!=null && "SUCCESS".equals(vo.getReturn_code()) && "SUCCESS".equals(vo.getResult_code())){
                        TreeMap treeMap=new TreeMap();
                        treeMap.put("appId", ApiConstant.WEI_XIN_PUBLIC_APP_ID);
                        treeMap.put("package","prepay_id="+vo.getPrepay_id());
                        treeMap.put("nonceStr", RandomStringUtils.randomAlphanumeric(20)+"");
                        treeMap.put("timeStamp",new Date().getTime()/1000+"");
                        treeMap.put("signType","MD5");
                        Set<String> keys=treeMap.keySet();
                        StringBuilder builder=new StringBuilder();
                        for(String key:keys){
                            builder.append(key).append("=").append(treeMap.get(key)).append("&");
                        }
                        builder.append("key=").append(ApiConstant.WEI_XIN_PUBLIC_APP_ID);
                        // builder.deleteCharAt(builder.length()-1);
                        treeMap.put("paySign", DigestUtils.md5Hex(builder.toString()).toUpperCase());

                        treeMap.put("orderNo",orderNo);
                        CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                        CacheUtil.put("pay","orderObject-"+orderNo,JSONObject.toJSONString(orderPO));
                        TokenUtil.putOrderNo(token,orderNo);
                        return new ResponseApiVO(1,"成功",treeMap);
                    }else{
                        throw new GenException(-2,"调用微信jsapi支付接口失败",vo);
                    }
                }else if(PayTypeEnum.PayType2.getCode().equals(payType)){
                    String dcost=(double)orderPO.getDoCost()/100+"";
                    PayCenterAliPayVO alipayVo=new PayCenterAliPayVO();
                    RequestAliOrderVO rvo=PayApiUtil.requestAliPayOrder(OrderTypeEnum.getName(orderType),orderNo,dcost,Tools.getIpAddr());
                    if(rvo==null){
                        throw new GenException(-2,"调用支付宝接口失败",rvo);

                    }
                    PropertyUtils.copyProperties(alipayVo,rvo);
                    CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                    CacheUtil.put("pay","orderObject-"+orderNo,JSONObject.toJSONString(orderPO));
                    TokenUtil.putOrderNo(token,orderNo);
                    return new ResponseApiVO(1,"成功",alipayVo);
                    // PayApiUtil.requestAliPayOrder("充值","微信充值-"+dcost+"元",orderNo,dcost,Tools.getIpAddr());
                }else if(PayTypeEnum.PayType5.getCode().equals(payType)){

                    ResponseApiVO<WalletBalanceVO> responseApiVO= this.walletService.getBalance(userId);
                    if(responseApiVO.getReCode()!=1 ){
                        throw new GenException(-2,"钱包余额支付失败");
                    }else{
                        WalletBalanceVO walletBalanceVO=responseApiVO.getData();
                        if(walletBalanceVO.getMoney()==null || orderPO.getDoCost()>walletBalanceVO.getMoney()){
                            throw new GenException(-2,"钱包余额不足");

                        }else{
                            Map setMap=new HashMap();
                            setMap.put("money",-orderPO.getDoCost());
                            setMap.put("updateTime",new Date());
                            Map searchMap=new HashMap();
                            searchMap.put("userId",userId);
                            ResponseVO res=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money"});
                            if(res.getReCode()!=1){
                                throw new GenException(-2,"钱包余额不足");
                            }

                            CacheUtil.put("pay","pre-pay-"+orderNo,orderCacheData);
                            //CacheUtil.put("pay","orderObject-"+orderNo,JSONObject.toJSONString(orderPO));
                            this.pulbicUpdateOrderPayStatus(PayStatusEnum.PayStatus1,orderNo,orderPO);
                            TokenUtil.deletePayCode(token);
                            Map costMap=new HashMap();
                            costMap.put("payCost",orderPO.getDoCost());
                            return new ResponseApiVO(1,"钱包支付成功",costMap);
                        }
                    }


                }
            }else{
                throw new GenException("支付失败");

            }
        }
        return new ResponseApiVO(-2,"支付失败",null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO exitOrder(List<Integer> orderIds)throws Exception{
        ResponseVO vo=this.baseExitOrder(orderIds);
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }
    public ResponseApiVO executeCoupon(OrderPO po,Integer couponCode,String token)throws Exception{
        if(couponCode!=null && couponCode>0){
            ResponseVO<Map<String,Integer>> couponVo=this.handleCoupon(po.getDoCost(),couponCode,TokenUtil.getUserId(token),TokenUtil.getStoreId(token));
            if(couponVo.getReCode()!=1){
                return new ResponseApiVO(-2,couponVo.getReMsg(),null);
            }else{
                Map<String,Integer> data=couponVo.getData();
                po.setDoCouponNo(couponCode==null?null:couponCode+"");
                po.setDoCost(data.get("cost"));
                if(data.containsKey("proportion")){
                    po.setDoStoreProportion(data.get("proportion"));
                    po.setDoStoreProportionCost(data.get("proportionCost"));
                    po.setDoStoreProportionCouponCost(data.get("storeProportionCouponCost"));
                    po.setDoCouponCost(data.get("doCouponCost"));
                }

            }
        }
        return new ResponseApiVO(1,"成功",null);
    }
    public ResponseVO handleCoupon(Integer cost,Integer couponId,Integer userId,Integer storeId)throws Exception{

        CouponPO po=this.walletService.getCoupon(couponId,userId);
        if(po==null){
            return new ResponseVO(-2,"优惠卷不存在",null);
        }
        if(po.getStoreId()!=-1 && !storeId.equals(po.getStoreId())){
            return new ResponseVO(-2,"抱歉，不能使用别的店铺优惠卷",null);
        }
        Date currentDate=new Date();

        if(po.getDcStartTime().after(currentDate)){
            return new ResponseVO(-2,"优惠卷有效时间没开始",null);
        }
        if(po.getDcEndTime().before(currentDate)){
            return new ResponseVO(-2,"优惠卷有效时间已过期",null);
        }
        if(cost<po.getDcMinPrice()){
            return new ResponseVO(-2,"优惠卷消费额度不达标",null);
        }
        Integer newCost=0;
        Map couponMap=new HashMap();
        if(CouponTypeEnum.CouponType2.getCode().equals(po.getDcType())){
            newCost=(int)(cost*((float)po.getDcMoney()/100));
            couponMap.put("doCouponCost",cost-newCost);
        }else{
            newCost=cost-po.getDcMoney();
            couponMap.put("doCouponCost",po.getDcMoney());
        }

        couponMap.put("cost",newCost);
        if(po.getStoreId()==-1 && po.getStoreProportion()!=null && po.getStoreProportion()>0){
            Integer storeProportionCouponCost=BigDecimal.valueOf(cost-newCost).multiply(BigDecimal.valueOf(po.getStoreProportion()).divide(BigDecimal.valueOf(100))).intValue();
            couponMap.put("proportion",po.getStoreProportion());
            couponMap.put("proportionCost",cost+storeProportionCouponCost);
            couponMap.put("storeProportionCouponCost",storeProportionCouponCost);

        }

        return new ResponseVO(1,"成功",couponMap);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void handleOrderImg()throws Exception{

        StringBuilder imgBuiler=null;
        Map searchMap=new HashMap();
        searchMap.put("doType,in","(1,6)");

       List<Map> list=this.commonObjectsBySearchCondition("ddw_order",searchMap);
       if(list!=null){
           Set<String> s=null;
           for(Map orderM:list){
               List<Map> orderV=this.commonObjectsBySingleParam("ddw_order_view","orderId",orderM.get("id"));
               if(orderV!=null){
                   s=new HashSet<>();
                   imgBuiler=new StringBuilder();
                   for(Map vm:orderV){
                       if(vm.containsKey("headImg")){

                           s.add(vm.get("headImg").toString().replaceAll("(.*/rs/)(.*)",mainGlobals.getRsDir()+"$2"));
                       }
                   }
                   if(s!=null && s.size()>1){
                       imgBuiler.append(mainGlobals.getRsDir()).append("order/").append(orderM.get("doOrderDate")).append(".jpg");
                       System.out.println(s);
                       fileService.mergeImages(s,imgBuiler.toString());
                       Map upda=new HashMap();
                       upda.put("doMergePicPath",imgBuiler.toString().replaceAll("(.*)(/rs/.*)",ddwGlobals.getCallBackHost()+"$2"));
                       this.commonUpdateBySingleSearchParam("ddw_order",upda,"id",orderM.get("id"));
                   }
               }
           }
       }

    }
    public static void main(String[] args) {
        Integer[] id={1,2,3,4};
        System.out.println(Arrays.asList(id).toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
        System.out.println();
        new StringBuilder().append(1);
    }
}
