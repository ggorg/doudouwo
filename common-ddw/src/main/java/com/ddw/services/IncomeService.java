package com.ddw.services;

import com.ddw.dao.BiddingCommonMapper;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BiddingCommonMapper biddingCommonMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO handleGoddessIncome(Integer bidId)throws Exception{

        List<Map> list=this.biddingCommonMapper.getBiddingNoIncome(bidId);
        if(list!=null && list.size()>0){
            Date endTime=null;
            for(Map m:list){


                if(!m.containsKey("incomeId") && m.get("incomeId")==null){
                    if(bidId!=null){
                        endTime=(Date)m.get("endTime");
                        if(endTime.before(new Date())){
                            continue;
                        }
                    }
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
            if(StringUtils.isNotBlank(orderNo)){
                insert.put("orderId", OrderUtil.getOrderId(orderNo));
                insert.put("orderNo", orderNo);
            }

            res = this.commonInsertMap("ddw_income_record", insert);
            if (res.getReCode() != 1) {
                throw new GenException("更新收益钱包失败");

            }
        }
        return new ResponseVO(1,"成功",res.getData());
    }

    public static void main(String[] args) {
        System.out.println((int)(111*0.6));
    }
}
