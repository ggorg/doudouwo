package com.ddw.controller;

import com.ddw.util.Toolsddw;
import com.ddw.beans.StorePO;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            return this.orderService.prestoreOrder(Toolsddw.getUserMap(),spo.getId(),mid);
        }catch (Exception e){
            logger.error("OrderController->doPrestoreOrder",e);
        }
        return new ResponseVO(-1,"订购失败",null);
    }
    @GetMapping("to-order-info")
    public String toOrderInfo(String orderNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               model.addAllAttributes(this.orderService.getOrderByStoreAndOrderNo(spo.getId(),orderNo));
            }

        }catch (Exception e){
            logger.error("OrderController->toOrderInfo",e);

        }
        return "pages/manager/order/orderinfo";
    }

    @GetMapping("to-order-by-store")
    public String toOrderByStore(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){

                model.addAttribute("oPage",this.orderService.findOrderByStore(spo.getId(),pageNo));
            }
        }catch (Exception e){
            logger.error("OrderController->toOrderByStore",e);

        }
        return "pages/manager/order/orderlist";
    }
}
