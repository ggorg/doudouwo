package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.GoddessAppointmentEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.IMApiUtil;
import com.gen.common.exception.GenException;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
    private GoddessService goddessService;
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

    private ResponseVO saveBidding(String token,String groupId)throws Exception{
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");
        Map insertMap=new HashMap();
        insertMap.put("userId",Integer.parseInt(useridStr));
        insertMap.put("createTime",new Date());
        insertMap.put("updateTime",new Date());
        insertMap.put("version",1);
        insertMap.put("groupId",groupId);
        insertMap.put("bidEndTime",DateUtils.addMinutes(new Date(),this.bidEndTimeMinute));
        return this.commonInsertMap("ddw_goddess_bidding",insertMap);
    }

    public ResponseApiVO getCurrentMaxPrice(String token)throws Exception{
        String groupId=TokenUtil.getGroupId(token);

        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请先选择一个直播房间",null);
        }

        if(!groupId.matches("^[0-9]+_[0-9]+_[0-9]{12}$")){
            return new ResponseApiVO(-2,"群组ID格式异常",null);

        }
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");

        GoddessPO gpo=this.goddessService.getAppointment(Integer.parseInt(useridStr));
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"约玩已关闭",null);

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
            vo.setPrice((double)bidPrice/100+"");
            return new ResponseApiVO(2,"目前还没人竞价,起投金额为："+vo.getPrice()+"元",vo);
        }
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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO putPrice(String token,BiddingDTO dto)throws Exception{
        String groupId=TokenUtil.getGroupId(token);

        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请先选择一个直播房间",null);
        }
        String useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");
        GoddessPO gpo=this.goddessService.getAppointment(Integer.parseInt(useridStr));
        if(GoddessAppointmentEnum.status0.getCode().equals(gpo.getAppointment())){
            return new ResponseApiVO(-3,"抱歉约玩已被关闭，没法竞价",null);

        }
        Map searchMap=new HashMap();
        //searchMap.put("bidEndTime,>=",new Date());
        searchMap.put("groupId",groupId);
        searchMap.put("bidEndTime,>=",new Date());
        Map bidMap=getCurrentBidMap(groupId);
        //List<Map> bidList=this.commonList("ddw_goddess_bidding","createTime desc",1,1,searchMap);
        Integer bidId=null;
        boolean flag=false;
        if(bidMap==null ){

            ResponseVO<Integer> res=this.saveBidding(token,groupId);
            if(res.getReCode()!=1){
                throw new GenException("创建竞价记录失败");
            }
            bidId=res.getData();
            flag=true;
           // return new ResponseApiVO(-2,"竞价时间已经结束，请",null);
        }else{
            Date currentDate=new Date();
            bidId=(Integer) bidMap.get("id");
            Date bidEndTime=(Date) bidMap.get("bidEndTime");
            Date endTime=(Date) bidMap.get("endTime");
            if(bidEndTime.after(currentDate) && endTime==null){
               Map earnestMap=new HashMap();
               earnestMap.put("creater",TokenUtil.getUserId(token));
               earnestMap.put("biddingId",bidId);
               Map mapPO=this.commonObjectBySearchCondition("ddw_order_bidding_earnest",earnestMap);
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
                ResponseVO<Integer> res=this.saveBidding(token,groupId);
                if(res.getReCode()!=1){
                    throw new GenException("创建竞价记录失败");
                }
                bidId=res.getData();
                flag=true;
            }


        }

        if(flag){
            if(gpo.getEarnest()==null){
                throw new GenException("定金为空");
            }
            BiddingEarnestVO be=new BiddingEarnestVO();
            be.setBidCode(MyEncryptUtil.encry(bidId+""));
            be.setPrice(gpo.getEarnest());
            return new ResponseApiVO(-2,"请先交定金",be);
        }

        if(dto.getPrice()==null ||  dto.getPrice()<=0 ){
            return new ResponseApiVO(-2,"请输入有效的金额",null);
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
                list.add(vo);
                cacheService.set("groupId-"+groupId,list);
            }
        }else{
            vo=new BiddingVO();
            vo.setPrice(dto.getPrice()+"");
            vo.setOpenId(TokenUtil.getUserObject(token).toString());
            vo.setUserName(TokenUtil.getUserName(token));
            list.add(vo);
            list.remove("handling");
            cacheService.set("groupId-"+groupId,list);
        }
        IMApiUtil.sendGroupMsg(groupId,new ResponseVO(1,"成功",vo));
        //liveRadioService.getLiveRadioByIdAndStoreId(TokenUtil.getStoreId(token))
       // this.commonSingleFieldBySingleSearchParam()
          return new ResponseApiVO(1,"成功",null);
    }

    public static void main(String[] args) {
        long l=(DateUtils.addMinutes(new Date(),150).getTime()-System.currentTimeMillis());
        System.out.println(l);

        //System.out.println((doubble)l/1000);
        BigDecimal v= BigDecimal.valueOf(l).divide(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_DOWN);
        long part=(long)v.doubleValue();
        BigDecimal point=v.subtract(BigDecimal.valueOf(part)).multiply(BigDecimal.valueOf(60));
        System.out.println(part+"分"+point.intValue()+"秒");
    }
}
