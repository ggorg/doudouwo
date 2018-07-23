package com.ddw.controller;

import com.ddw.beans.GoodsEditDTO;
import com.ddw.beans.GoodsPageModelDTO;
import com.ddw.beans.StorePO;
import com.ddw.services.StoreGoodsPlateService;
import com.ddw.servies.StoreFormulaService;
import com.ddw.servies.StoreGoodsPageModelService;
import com.ddw.servies.StoreGoodsService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 门店商品页面模板
 */
@RequestMapping("/manager/goods/pagemodel")
@Controller
public class StoreGoodsPageModelContoller {

    private final Logger logger = Logger.getLogger(StoreGoodsPageModelContoller.class);

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private StoreGoodsPageModelService storeGoodsPageModelService;



    @GetMapping("/to-paging")
    public String  toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("p",this.storeGoodsPageModelService.findPage(pageNo,spo.getId()));
            }
        }catch (Exception e){
            logger.error("StoreGoodsPageModelContoller->toPaging->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsPageModelList";
    }

    @GetMapping("to-edit")
    public String  toEdit(String idStr, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("goodsList", storeGoodsService.getGoodsList(spo.getId()));
                model.addAttribute("pm", storeGoodsPageModelService.get(idStr));

            }
        }catch (Exception e){
            logger.error("StoreGoodsPageModelContoller->toEdit->系统异常",e);
        }
        return "pages/manager/store/goods/storeGoodsPageModelEdit";
    }

    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave( GoodsPageModelDTO dto, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.storeGoodsPageModelService.save(dto,spo.getId());
              // return this.storeGoodsService.saveGoods(dto,spo.getId());
            }
        }catch (Exception e){
            logger.error("StoreGoodsPageModelContoller->doSave->系统异常",e);
        }
        return new ResponseVO(-1,"提交失败",null);
    }
    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdateStatus(String idStr,Integer status){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.storeGoodsPageModelService.updateStatus(idStr,status,spo.getId());
            }

        }catch (Exception e){
            logger.error("StoreGoodsPageModelContoller->doUpdateStatus",e);
        }
        return new ResponseVO(-1,"操作失败",null);

    }

}
