package com.ddw.controller;

import com.ddw.beans.OrderPO;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.RoleTypeEnum;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.RoleService;
import com.ddw.util.Toolsddw;
import com.ddw.beans.StorePO;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private RoleService roleService;

    @PostMapping("do-prestore-order")
    @ResponseBody
    public ResponseVO doPrestoreOrder(Integer[] mid){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.orderService.prestoreOrder(Toolsddw.getUserMap(),spo.getId(),mid);
        }catch (Exception e){
            logger.error("MaterialOrderController->doPrestoreOrder",e);
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
            logger.error("MaterialOrderController->toOrderInfo",e);

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
                    model.addAllAttributes(this.orderService.getOrderByStoreAndOrderNo(orderno, OrderTypeEnum.OrderType2));
                }
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->toOrderInfo",e);

        }
        return "pages/manager/store/orderinfo";
    }


    /**
     * 跟据门店打开订单
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("to-order-by-store")
    public String toOrderPageByStore(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){

                model.addAttribute("oPage",this.orderService.findOrderByStore(spo.getId(),pageNo));
            }
        }catch (Exception e){
            logger.error("MaterialOrderController->toOrderByStore",e);

        }
        return "pages/manager/store/orderList";
    }

    /**
     * 门店删除订单
     * @param orderNo
     * @return
     */
    @PostMapping("do-delete-order")
    @ResponseBody
    public ResponseVO doDeleteOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               return  this.orderService.deleteOrder(spo.getId(),orderNo);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->doDeleteOrder",e);
        }
        return new ResponseVO(-1,"删除订单失败",null);

    }

    /**
     * 门店取消订单
     * @param orderNo
     * @return
     */
    @PostMapping("do-cancel-order")
    public ResponseVO doCancelOrder(String orderNo){

        return new ResponseVO(-1,"取消订单失败",null);
    }

    /**
     * 门店确认签收
     * @param orderNo
     * @return
     */
    @PostMapping("do-makesure-order")
    @ResponseBody
    public ResponseVO doMakesureOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return  this.orderService.makeSureOrderByStore(spo.getId(),orderNo);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->doMakesureOrder",e);
        }
        return new ResponseVO(-1,"确认签收失败",null);
    }

    /**
     * 门店-填写寄件信息页面
     * @param orderNo
     * @return
     */
    @GetMapping("to-order-exit-back-edit-mail-html")
    public String toOrderExitBackEditMailHtml(String orderNo){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return "pages/manager/store/orderExitBackEditMail";
            }
        }catch (Exception e){
            logger.error("MaterialOrderController->toOrderExitBackEditMailHtml",e);

        }
        return "redirect:/404";
    }

    /**
     * 门店-填写寄件
     * @param orderNo
     * @return
     */
    @PostMapping("do-order-exit-back-edit-mail")
    @ResponseBody
    public ResponseVO doOrderExitBackEditMail(String orderNo,String doExitTrackingNumber,String doExitExpressName){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               return this.orderService.storeEditMailInfo(orderNo, doExitTrackingNumber, doExitExpressName);
            }
        }catch (Exception e){
            logger.error("MaterialOrderController->doOrderExitBackEditMail",e);

        }
        return new ResponseVO(-2,"提交失败",null);
    }
    /**
     * 提交订单
     * @param orderNo
     * @return
     */
    @PostMapping("do-submit-order")
    @ResponseBody
    public ResponseVO doSubmitOrder(String orderNo){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return  this.orderService.submitOrder(spo.getId(), orderNo);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->doSubmitOrder",e);
        }
        return new ResponseVO(-1,"订单提交失败",null);
    }

    /**
     * 总店-接受单子
     * @param orderNo
     * @return
     */
    @PostMapping("do-accept-order")
    @ResponseBody
    public ResponseVO toAcceptOrder(String orderNo){
        try{
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                return this.orderService.updateOrderStatus(null,ShipStatusEnum.ShipStatus1.getCode(),null,orderNo);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->toAcceptOrder",e);
        }
        return new ResponseVO(-1,"接受订单失败",null);
    }

    /**
     * 总店-确认完成订单
     * @param orderNo
     * @return
     */
    @PostMapping("do-finish-order")
    @ResponseBody
    public ResponseVO toFinishOrder(String orderNo){
        try{
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                return this.orderService.updateOrderStatus(null,ShipStatusEnum.ShipStatus5.getCode(),null,orderNo);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->toFinishOrder",e);
        }
        return new ResponseVO(-1,"订单完成失败",null);
    }

    /**
     * 总店-展示材料订单列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("to-material-order-page-by-hq")
    public String toMaterialOrderPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                model.addAttribute("oPage",this.orderService.findOrderByHq(pageNo));
            }


        }catch (Exception e){
            logger.error("MaterialOrderController->toOrderByStore",e);

        }
        return "pages/manager/headquarters/orderList";
    }

    /**
     * 总店-打开材料订单详情
     * @param orderNo
     * @param model
     * @return
     */
    @GetMapping("to-material-order-info-by-hq")
    public String toMaterialOrderInfoByHq(String orderNo, Model model){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                String orderno=MyEncryptUtil.getRealValue(orderNo);
                if(orderno!=null){
                    Map map=this.orderService.getOrderByStoreAndOrderNo(orderno,OrderTypeEnum.OrderType2);
                    OrderPO op=(OrderPO)map.get("order");
                    map.put("store",this.storeService.getBeanById(op.getDoCustomerStoreId()));
                    model.addAllAttributes(map);

                }
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->toMaterialOrderInfoByHq",e);

        }
        return "pages/manager/headquarters/orderinfo";
    }

    /**
     * 总店-打开确认发货页面
     * @param orderNo
     * @param model
     * @return
     */
    @GetMapping("to-makesure-sendgoods-by-hq")
    public String toMakeSureSendGoodsByHq(String orderNo, Model model){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                this.toMaterialOrderInfoByHq(orderNo,model);
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->toMakeSureMaterialOrderInfoByHq",e);

        }
        return "pages/manager/headquarters/orderMakeSureSendGoods";
    }
    /**
     * 总店-确认发货提交
     * @param orderNo
     * @param model
     * @return
     */
    @PostMapping("do-makesure-sendgoods-by-hq")
    @ResponseBody
    public ResponseVO doMakeSureSendGoodsByHq(String orderNo,String doExpressName,String doTrackingNumber, Model model){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
               return this.orderService.makerSureSendGoods(orderNo,doTrackingNumber,doExpressName,Toolsddw.getCurrentUserId());
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->doMakeSureSendGoodsByHq",e);

        }
        return new ResponseVO(-1,"确认发货失败",null);
    }

    /**
     * 总店确认签收
     * @param orderNo
     * @return
     */
    @PostMapping("do-makesure-order-by-hq")
    @ResponseBody
    public ResponseVO doMakesureOrderByHq(String orderNo){
        try{
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
            if(roleList!=null && roleList.size()>0){
                return  this.orderService.makeSureAcceptByHq(orderNo,Toolsddw.getCurrentUserId());
            }

        }catch (Exception e){
            logger.error("MaterialOrderController->doMakesureOrderByHq",e);
        }
        return new ResponseVO(-1,"签收失败",null);
    }

}
