package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.controller.AppOrderController;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Page;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class AppOrderService extends CommonService {
    private final Logger logger = Logger.getLogger(AppOrderService.class);

    public ResponseApiVO getExitOrderList(String token,PageNoDTO dto)throws Exception{

        Page p=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Map searchMap=new HashMap();
        searchMap.put("creater",TokenUtil.getUserId(token));
        CommonChildBean cb=new CommonChildBean("ddw_order_view","orderId","orderId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_exit_order","t1.createTime desc","DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') refundTime,t1.exitCost price,ct0.name,right(ct0.orderNo,16) orderNo,ct0.headImg imgUrl",p.getStartRow(),p.getEndRow(),searchMap,cb);
        List<Map> orderList=this.getCommonMapper().selectObjects(csb);

        if(orderList==null || orderList.isEmpty()){
            return new ResponseApiVO(2,"没有退款数据",new ListVO(new ArrayList()));
        }else{
            return new ResponseApiVO(1,"成功",new ListVO(orderList));
        }
    }
    public ResponseApiVO getOrderList(String token,OrderViewDTO dto)throws Exception{
        if(dto.getType()!=null && StringUtils.isBlank(AppOrderTypeEnum.getName(dto.getType()))){
            return new ResponseApiVO(-2,"订单类型错误",null);
        }
        Map map=new HashMap();
        map.put("doCustomerUserId", TokenUtil.getUserId(token));
        map.put("doPayStatus,>=", PayStatusEnum.PayStatus1.getCode());
        map.put("doType,in",AppOrderTypeEnum.OrderType3.getName());
        if(dto.getShipStatus()!=null && StringUtils.isNotBlank(ShipStatusEnum.getName(dto.getShipStatus()))){
            map.put("doShipStatus", dto.getShipStatus());
        }

        List<Map> omainrderList=this.commonList("ddw_order","createTime desc",dto.getPageNo()==null?1:dto.getPageNo(),10,map);

        if(omainrderList==null || omainrderList.isEmpty()){
            return new ResponseApiVO(2,"没有订单数据",new ListVO(new ArrayList()));
        }
        final Map<Integer,Map> orderIdMap=new HashMap();
        omainrderList.forEach(a->orderIdMap.put((Integer)a.get("id"),a));
        map=new HashMap();
        map.put("orderId,in",orderIdMap.keySet().toString().replaceFirst("(\\[)(.+)(\\])","($2)"));
        List<Map> orderList=this.commonList("ddw_order_view","createTime desc",null,null,map);
        OrderViewVO orderViewVO=null;
        List dataList=new ArrayList();
        Map<Integer,OrderViewVO> handleMap=new HashMap();
        Integer orderId=null;
        String doMergePicPath=null;
        for(Map m:orderList){
            orderId=(Integer)m.get("orderId");
            if(!OrderTypeEnum.OrderType7.getCode().equals(m.get("orderType")) && handleMap.containsKey(orderId)){
                OrderViewVO ov=handleMap.get(orderId);
                ov.setName(ov.getName()+" "+m.get("name"));
                ov.setDesc(ov.getDesc()+" "+m.get("name")+" *"+m.get("num"));
                //ov.setPrice(orderIdMap.get(orderId));
                if( orderIdMap.get(orderId).containsKey("doMergePicPath")){
                    doMergePicPath=(String) orderIdMap.get(orderId) .get("doMergePicPath");
                    if(StringUtils.isNotBlank(doMergePicPath)){
                        ov.setHeadImg(doMergePicPath);
                    }
                }

                ov.setType(1);
                ov.setId(-orderId);
            }else{
                orderViewVO=new OrderViewVO();
                PropertyUtils.copyProperties(orderViewVO,m);
                if(OrderTypeEnum.OrderType7.getCode().equals(orderViewVO.getOrderType())){
                    if(ClientShipStatusEnum.ShipStatus0.getCode().equals(orderViewVO.getShipStatus())){
                        orderViewVO.setShipStatusName("未使用");
                    }else{
                        orderViewVO.setShipStatusName("已使用");
                    }
                    orderViewVO.setPrice((Integer) m.get("price"));
                }else{
                    orderViewVO.setShipStatusName(ClientShipStatusEnum.getName(orderViewVO.getShipStatus()));
                    orderViewVO.setPrice((Integer) orderIdMap.get(orderId).get("doCost"));
                }
                orderViewVO.setOrderTypeName(OrderTypeEnum.getName(orderViewVO.getOrderType()));
                orderViewVO.setOrderNo(orderViewVO.getOrderNo().substring(16));
                orderViewVO.setDesc(orderViewVO.getName()+" *"+orderViewVO.getNum());
                handleMap.put(orderId,orderViewVO);
                dataList.add(orderViewVO);
            }


        }
        if(orderList==null || orderList.isEmpty()){
            return new ResponseApiVO(2,"没有订单数据",new ListVO(new ArrayList()));
        }else{
            orderIdMap.clear();
           // handleMap.clear();handleMap=null;
            return new ResponseApiVO(1,"成功",new ListVO(dataList));
        }
    }


}
