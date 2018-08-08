package com.ddw.controller;

import com.ddw.beans.StorePO;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.RoleTypeEnum;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager/client/order")
public class ClientOrderController {
    private final Logger logger = Logger.getLogger(ClientOrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    @GetMapping("to-goods-list")
    public String goodsList(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("oPage",orderService.findGoodsOrderByStore(spo.getId(),pageNo));
            }
        }catch (Exception e){
            logger.error("ClientOrderController->goodsList->商品订单列表-》异常",e);
        }
        return "pages/manager/store/goods/goodsOrderList";
    }
    @GetMapping("to-ticket-list")
    public String ticketList(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("oPage",orderService.findOrder(spo.getId(),pageNo,OrderTypeEnum.OrderType7));
            }
        }catch (Exception e){
            logger.error("ClientOrderController->ticketList->门票订单列表-》异常",e);
        }
        return "pages/manager/ticket/ticketOrderList";
    }

    /**
     * 门店-接受单子
     * @param orderNo
     * @return
     */
    @PostMapping("do-accept-order")
    @ResponseBody
    public ResponseVO toAcceptOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){

                return this.orderService.updateClientOrderStatus(null, ShipStatusEnum.ShipStatus1.getCode(),spo.getId(),orderNo);

            }

        }catch (Exception e){
            logger.error("ClientOrderController->toAcceptOrder",e);
        }
        return new ResponseVO(-1,"接受订单失败",null);
    }
    /**
     * 门店-确认发货
     * @param orderNo
     * @return
     */
    @PostMapping("do-makesure-sendgoods")
    @ResponseBody
    public ResponseVO doMakesureSendGoods(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.orderService.updateClientOrderStatus(null, ShipStatusEnum.ShipStatus5.getCode(),spo.getId(),orderNo);

            }

        }catch (Exception e){
            logger.error("ClientOrderController->doMakesureSendGoods",e);
        }
        return new ResponseVO(-1,"接受订单失败",null);
    }
    @GetMapping("to-order-info")
    public String toOrderInfo(String orderNo, Model model){
        commonOrderInfo(orderNo,model,OrderTypeEnum.OrderType1);
        return "pages/manager/store/goods/goodsOrderInfo";
    }
    @GetMapping("to-ticket-order-info")
    public String toTicketOrderInfo(String orderNo, Model model){
        commonOrderInfo(orderNo,model,OrderTypeEnum.OrderType7);
        return "pages/manager/ticket/ticketOrderInfo";
    }
    private void commonOrderInfo(String orderNo,Model model,OrderTypeEnum orderType){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                String orderno= MyEncryptUtil.getRealValue(orderNo);
                if(orderno!=null){
                    model.addAllAttributes(this.orderService.getOrderByStoreAndOrderNo(orderno, orderType));
                }
            }

        }catch (Exception e){
            logger.error("ClientOrderController->commonOrderInfo",e);

        }
    }
}
