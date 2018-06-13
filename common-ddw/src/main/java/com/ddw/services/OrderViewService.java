package com.ddw.services;

import com.ddw.beans.OrderViewPO;
import com.ddw.enums.ShipStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class OrderViewService extends CommonService{
    //@Async
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrderView(OrderViewPO po)throws Exception{
        return this.commonInsert("ddw_order_view",po);
    }
    //@Async
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateOrderView(String orderNo, Integer shipStatus)throws Exception{
        if(StringUtils.isBlank(ShipStatusEnum.getName(shipStatus))){
            return new ResponseVO(-2,"发货状态异常",null);
        }
        Map map=new HashMap();
        map.put("shipStatus",shipStatus);
        return this.commonUpdateBySingleSearchParam("ddw_order_view",map,"orderNo",orderNo);
    }

}
