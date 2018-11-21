package com.ddw.services;

import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class IncomeService extends CommonService{

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO handleGoddessIncome(Integer bidId)throws Exception{
        Map godessBidMap=new HashMap();
        godessBidMap.put("id",bidId);
        CommonSearchBean csb=new CommonSearchBean("ddw_order_bidding_pay",null,"t1.orderNo,t1.dorCost,ct0.userId,ct1.id incomeId",null,null,new HashMap(),
                new CommonChildBean("ddw_goddess_bidding","luckyDogUserId","creater",godessBidMap),
                new CommonChildBean("ddw_income_record","orderId","orderId",null));
        csb.setJointName("left");
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            for(Map m:list){
                if(!m.containsKey("incomeId") && m.get("incomeId")==null){
                    String orderNo=m.get("orderNo").toString();
                    this.commonIncome((Integer)m.get("userId"),(Integer) m.get("dorCost"),IncomeTypeEnum.IncomeType1,OrderTypeEnum.getOrderTypeEnum(OrderUtil.getOrderType(orderNo)),orderNo);
                }

            }
        }
        return new ResponseVO(1,"成功",null);

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO commonIncome(Integer goddessUserId, Integer cost, IncomeTypeEnum incomeType, OrderTypeEnum orderType,String orderNo)throws Exception{
       Integer income=(int)(cost*0.6);
       if(income<=0){
           return new ResponseVO(1,"成功",null);

       }
       Map setmap=new HashMap();
        String[] strs=null;
       if(IncomeTypeEnum.IncomeType1.getCode().equals(incomeType.getCode())){
           setmap.put("goddessIncome",income);
           strs= new String[]{"goddessIncome"};
       }else {
           setmap.put("practiceIncome",income);
           strs=new String[]{"practiceIncome"};

       }
       Map searchMap=new HashMap();
       searchMap.put("userId",goddessUserId);
        ResponseVO res=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setmap,searchMap,"version",strs);
        if(res.getReCode()!=1){
            throw new  GenException("更新收益钱包失败");
        }else {
            Map insert = new HashMap();
            insert.put("diMoney", income);
            insert.put("diType", incomeType.getCode());
            insert.put("createTime", new Date());
            insert.put("userId", goddessUserId);
            insert.put("orderType", orderType.getCode());
            insert.put("orderId", OrderUtil.getOrderId(orderNo));
            insert.put("orderNo", orderNo);
            res = this.commonInsertMap("ddw_income_record", insert);
            if (res.getReCode() != 1) {
                throw new GenException("更新收益钱包失败");

            }
        }
        return new ResponseVO(1,"成功",null);
    }

    public static void main(String[] args) {
        System.out.println((int)(111*0.6));
    }
}
