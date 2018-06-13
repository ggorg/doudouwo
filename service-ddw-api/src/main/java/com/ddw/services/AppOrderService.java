package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import org.apache.commons.beanutils.PropertyUtils;
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

    public ResponseApiVO getOrderList(String token,OrderViewDTO dto)throws Exception{
        if(dto.getType()!=null && StringUtils.isBlank(AppOrderTypeEnum.getName(dto.getType()))){
            return new ResponseApiVO(-2,"订单类型错误",null);
        }
        Page p=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Map map=new HashMap();
        map.put("userId", TokenUtil.getUserId(token));
        map.put("payStatus,>=", PayStatusEnum.PayStatus1.getCode());
        map.put("orderType,in",dto.getType()==null?AppOrderTypeEnum.OrderType3.getName():AppOrderTypeEnum.getName(dto.getType()));
        if(dto.getShipStatus()!=null && StringUtils.isNotBlank(ShipStatusEnum.getName(dto.getShipStatus()))){
            map.put("shipStatus", dto.getShipStatus());
        }
        List<Map> orderList=this.commonList("ddw_order_view","createTime desc",p.getStartRow(),p.getEndRow(),map);
        OrderViewVO orderViewVO=null;
        List dataList=new ArrayList();
        for(Map m:orderList){
            orderViewVO=new OrderViewVO();
            PropertyUtils.copyProperties(orderViewVO,m);
            orderViewVO.setOrderTypeName(OrderTypeEnum.getName(orderViewVO.getOrderType()));
            orderViewVO.setShipStatusName(ClientShipStatusEnum.getName(orderViewVO.getShipStatus()));
            dataList.add(orderViewVO);

        }
        if(orderList==null && orderList.isEmpty()){
            return new ResponseApiVO(2,"没有订单数据",new ListVO(new ArrayList()));
        }else{
            return new ResponseApiVO(1,"成功",new ListVO(dataList));
        }
    }
}
