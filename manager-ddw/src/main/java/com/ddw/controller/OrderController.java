package com.ddw.controller;

import com.ddw.Toolsddw;
import com.ddw.beans.StorePO;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manager/order")
public class OrderController {
    private final Logger logger = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private StoreService storeService;

    @PostMapping("do-prestore-order")
    @ResponseBody
    public ResponseVO doPrestoreOrder(Integer[] mid){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.orderService.prestoreOrder(Toolsddw.getCurrentUserId(),spo.getId(),mid);
        }catch (Exception e){
            logger.error("OrderController->doPrestoreOrder",e);
        }
        return new ResponseVO(-1,"订购失败",null);
    }
}
