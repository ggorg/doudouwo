package com.ddw.controller;

import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.OrderService;
import com.ddw.util.Toolsddw;
import com.ddw.beans.MaterialDTO;
import com.ddw.beans.StorePO;
import com.ddw.servies.MaterialService;
import com.ddw.servies.StoreService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 原材料
 */
@RequestMapping("/manager/hq/material")
@Controller
public class MaterialController {

    private final Logger logger = Logger.getLogger(MaterialController.class);

    @Autowired
    private MaterialService materialService;

    @Autowired
    private StoreService storeService;

        @Autowired
        private OrderService orderService;

    @GetMapping("to-paging-{dmStatus}")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo,@PathVariable Integer dmStatus, Model model){
            try {
                model.addAttribute("om",orderService.getOrderMaterialByPayStatusAndShipStatus(ShipStatusEnum.ShipStatus0.getCode()));
                model.addAttribute("mPage",materialService.findPage(pageNo,dmStatus));
            }catch (Exception e){
                logger.error("MaterialController->toPage",e);
            }
            return "pages/manager/headquarters/list";
    }
    @GetMapping("to-show-to-store")
    public String toShowToStore(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("om",orderService.getOrderMaterialByPayStatusAndShipStatus(ShipStatusEnum.ShipStatus0.getCode()));
            model.addAttribute("mPage",materialService.findPage(pageNo,1));
        }catch (Exception e){
            logger.error("MaterialController->toShowToStore",e);

        }
        return "pages/manager/store/materialList";
    }
    @GetMapping("to-material-info")
    public String toMaterialInfo(Integer id,Model model){
        try {
            model.addAttribute("dm",this.materialService.getById(id));
            model.addAttribute("id", MyEncryptUtil.encry(""+id));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "pages/manager/store/materialInfo";
    }
    @PostMapping("do-add-material-to-shoppingcart")
    @ResponseBody
    public ResponseVO doAddMaterialToShoppingCart(String idStr,Integer num){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.materialService.addMaterialToCache(idStr,spo.getId(),num);
            }
        }catch (Exception e){
            logger.error("MaterialController->doAddMaterialToShoppingCart",e);
        }
        return new ResponseVO(-2,"购入失败",null);
    }
    @GetMapping("to-pcshoppingcart-list")
    public String toPcshoppingcartList(Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("mPage",this.materialService.getMaterialByCache(spo.getId()));

            }
        }catch (Exception e){
            logger.error("MaterialController->toPcshoppingcartList",e);
        }
        return "pages/manager/store/shoppingcart";
    }
    @GetMapping("to-update-material-num")
    public String toUpdatelMaterialNum(Integer mid,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("m",this.materialService.getMaterialCacheById(spo.getId(),mid));

            }
        }catch (Exception e){
            logger.error("MaterialController->toUpdatelMaterialNum",e);
        }
        return "pages/manager/store/updateMaterialNum";
    }
    @PostMapping("do-update-material-num")
    @ResponseBody
    public ResponseVO doUpdateMaterialNum(Integer mid,Integer num){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
              return this.materialService.updateMaterialNumCacheById(spo.getId(),mid,num);
            }
        }catch (Exception e){
            logger.error("MaterialController->doUpdateMaterialNum",e);
        }
        return new ResponseVO(-1,"修改失败",null);
    }
    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {
            model.addAttribute("dm",this.materialService.getById(id));
        }catch (Exception e){
            logger.error("MaterialController->toEdtitPage",e);
        }
        return "pages/manager/headquarters/edit";
    }
    @GetMapping("to-inbound")
    public String toInbound(Model model){
        return "pages/manager/headquarters/inBound";
    }
    @PostMapping("do-inbound")
    @ResponseBody
    public ResponseVO doInbound(String idStr,Integer num){
        try {
            Map map=(Map)Tools.getSession("user");
            return this.materialService.inbound(idStr,num,(Integer)map.get("id"));
        }catch (Exception e){
            logger.error("MaterialController->toInbound",e);
            return new ResponseVO(-1,"入库失败",null);
        }

    }
    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(MaterialDTO materialDTO){
        try {
            return this.materialService.save(materialDTO);
        }catch (Exception e){
            logger.error("MaterialController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdateStatus(String idStr,Integer status){
        try {
            return this.materialService.updateStatus(idStr,status);
        }catch (Exception e){
            logger.error("MaterialController->doUpdateStatus",e);
            return new ResponseVO(-1,"修改失败",null);
        }
    }
}
