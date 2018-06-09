package com.ddw.services;

import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.OrderUtil;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class IncomeService extends CommonService{

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO commonIncome(Integer goddessUserId, Integer cost, IncomeTypeEnum incomeType, OrderTypeEnum orderType,String orderNo)throws Exception{
       Integer income=(int)(cost*0.6);
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
