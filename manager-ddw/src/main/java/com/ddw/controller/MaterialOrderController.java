package com.ddw.controller;

import com.ddw.enums.ShipStatusEnum;
import com.ddw.util.Toolsddw;
import com.ddw.beans.StorePO;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 材料订单
 */
@Controller
@RequestMapping("/manager/order")
public class MaterialOrderController {
    private final Logger logger = Logger.getLogger(MaterialOrderController.class);

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

    @GetMapping("to-ordercache-info")
    public String toOrderCacheInfo(String orderNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                String orderno=MyEncryptUtil.getRealValue(orderNo);
                if(orderno!=null){
                    model.addAllAttributes(this.orderService.getOrderCacheByStoreAndOrderNo(spo.getId(),orderno));
                }
            }

        }catch (Exception e){
            logger.error("OrderController->toOrderInfo",e);

        }
        return "pages/manager/store/orderInfosubmit";
    }

    @GetMapping("to-order-info")
    public String toOrderInfo(String orderNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                String orderno=MyEncryptUtil.getRealValue(orderNo);
                if(orderno!=null){
                    model.addAllAttributes(this.orderService.getOrderByStoreAndOrderNo(orderno));
                }
            }

        }catch (Exception e){
            logger.error("OrderController->toOrderInfo",e);

        }
        return "pages/manager/store/orderinfo";
    }
    @GetMapping("to-material-order-page-by-hq")
    public String toMaterialOrderPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {

            model.addAttribute("oPage",this.orderService.findOrderByHq(pageNo));

        }catch (Exception e){
            logger.error("OrderController->toOrderByStore",e);

        }
        return "pages/manager/headquarters/orderlist";
    }

    @GetMapping("to-material-order-info-by-hq")
    public String toMaterialOrderInfoByHq(String orderNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                String orderno=MyEncryptUtil.getRealValue(orderNo);
                if(orderno!=null){
                    model.addAllAttributes(this.orderService.getOrderByStoreAndOrderNo(orderno));
                }
            }

        }catch (Exception e){
            logger.error("OrderController->toOrderInfo",e);

        }
        return "pages/manager/store/orderinfo";
    }
    @GetMapping("to-order-by-store")
    public String toOrderPageByStore(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){

                model.addAttribute("oPage",this.orderService.findOrderByStore(spo.getId(),pageNo));
            }
        }catch (Exception e){
            logger.error("OrderController->toOrderByStore",e);

        }
        return "pages/manager/store/orderList";
    }
    @PostMapping("do-delete-order")
    @ResponseBody
    public ResponseVO doDeleteOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               return  this.orderService.deleteOrder(spo.getId(),orderNo);
            }

        }catch (Exception e){
            logger.error("OrderController->doDeleteOrder",e);
        }
        return new ResponseVO(-1,"删除订单失败",null);

    }
    @PostMapping("do-cancel-order")
    public ResponseVO doCancelOrder(String orderNo){

        return new ResponseVO(-1,"取消订单失败",null);
    }
    @PostMapping("do-makesure-order")
    public ResponseVO doMakesureOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return  this.orderService.updateOrderStatus(null, ShipStatusEnum.ShipStatus3.getCode(),spo.getId(),orderNo);
            }

        }catch (Exception e){
            logger.error("OrderController->doDeleteOrder",e);
        }
        return new ResponseVO(-1,"删除订单失败",null);
    }
    @PostMapping("do-exchange-order")
    public ResponseVO doExchangeOrder(String orderNo){
        return null;
    }
    @PostMapping("do-submit-order")
    @ResponseBody
    public ResponseVO doSubmitOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return  this.orderService.submitOrder(spo.getId(), orderNo);
            }

        }catch (Exception e){
            logger.error("OrderController->doSubmitOrder",e);
        }
        return new ResponseVO(-1,"订单提交失败",null);
    }

}
