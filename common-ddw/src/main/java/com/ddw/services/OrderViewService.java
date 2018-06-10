package com.ddw.services;

import com.ddw.beans.OrderViewPO;
import com.ddw.enums.ShipStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
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
    public ResponseVO saveOrderView(String orderNo, ShipStatusEnum shipStatus)throws Exception{
        Map map=new HashMap();
        map.put("shipStatus",shipStatus.getCode());
        return this.commonUpdateBySingleSearchParam("ddw_order_view",map,"orderNo",orderNo);
    }

}
