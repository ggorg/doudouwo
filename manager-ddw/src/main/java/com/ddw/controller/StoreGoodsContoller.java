package com.ddw.controller;

import com.ddw.beans.GoodsEditDTO;
import com.ddw.beans.StorePO;
import com.ddw.services.StoreGoodsTypeService;
import com.ddw.servies.StoreGoodsService;
import com.ddw.servies.StoreFormulaService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 门店商品
 */
@RequestMapping("/manager/goods")
@Controller
public class StoreGoodsContoller {

    private final Logger logger = Logger.getLogger(StoreGoodsContoller.class);

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreGoodsService storeGoodsService;

    @Autowired
    private StoreFormulaService storeFormulaService;

    @Autowired
    private StoreGoodsTypeService storeGoodsTypeService;

    @GetMapping("/to-paging")
    public String  toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("gPage",storeGoodsService.findPage(pageNo,null,spo.getId()));

            }
        }catch (Exception e){
            logger.error("StoreGoodsContoller->toPaging->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsList";
    }

    @GetMapping("to-edit")
    public String  toEdit(String idStr, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("formulaList",this.storeFormulaService.getAllByStore(spo.getId()));
                model.addAttribute("goods",this.storeGoodsService.getGoods(idStr));
                model.addAttribute("goodsType",this.storeGoodsTypeService.getGoodsType(spo.getId()));
            }
        }catch (Exception e){
            logger.error("StoreGoodsContoller->toEdit->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsEdit";
    }

    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave(GoodsEditDTO dto, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               return this.storeGoodsService.saveGoods(dto,spo.getId());
            }
        }catch (Exception e){
            logger.error("StoreGoodsContoller->doSave->系统异常",e);
        }
        return new ResponseVO(-1,"提交失败",null);
    }
    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdateStatus(String idStr,Integer status){
        try {
            return this.storeGoodsService.updateStatus(idStr,status);
        }catch (Exception e){
            logger.error("StoreGoodsContoller->doUpdateStatus",e);
            return new ResponseVO(-1,"操作失败",null);
        }
    }

}
