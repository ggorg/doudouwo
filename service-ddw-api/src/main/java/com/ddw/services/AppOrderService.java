package com.ddw.services;

import com.ddw.beans.ListVO;
import com.ddw.beans.PageNoDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.AppOrderTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AppOrderService extends CommonService {

    public ResponseApiVO getOrderList(PageNoDTO dto, String token, Integer orderType)throws Exception{
        if(orderType!=null && StringUtils.isBlank(AppOrderTypeEnum.getName(orderType))){
            return new ResponseApiVO(-2,"订单类型错误",null);
        }
        Page p=new Page(dto.getPageNo(),10);
        Map map=new HashMap();
        map.put("doCustomerUserId", TokenUtil.getUserId(token));
        map.put("doPayStatus", PayStatusEnum.PayStatus1.getCode());
        List<Map> orderList=this.commonList("ddw_order","updateTime desc",p.getStartRow(),p.getEndRow(),map);
        if(orderList==null && orderList.isEmpty()){
            return new ResponseApiVO(2,"没有订单数据",new ListVO(new ArrayList()));
        }else{
            Integer ot=null;
            for(Map o:orderList){
                ot=(Integer) o.get("doPayType");

            }
        }
        return null;

    }
}
