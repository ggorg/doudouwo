package com.ddw.controller;

import com.ddw.beans.StoreFormulaDTO;
import com.ddw.beans.StorePO;
import com.ddw.servies.StoreFormulaService;
import com.ddw.servies.StoreMaterialService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 门店商口配方
 */
@RequestMapping("/manager/formula")
@Controller
public class StoreFormulaController {
    private final Logger logger = Logger.getLogger(StoreFormulaController.class);

    @Autowired
    private StoreFormulaService storeFormulaService;

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreMaterialService storeMaterialService;

    @GetMapping("to-paging")
    public String  toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("smpage",this.storeFormulaService.findPage(pageNo,spo.getId()));
            }
        }catch (Exception e){
            logger.error("StoreFormulaController->toPaging->系统异常");
        }
    return "pages/manager/store/storeFormulaList";
    }


    @GetMapping("to-edit")
    public String  toEdit(String idStr, Model model){
        try{
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                model.addAttribute("formula",this.storeFormulaService.getFormula(idStr));
                model.addAttribute("materialList",this.storeMaterialService.getAll(spo.getId()));
            }
        }catch (Exception e){
            logger.error("StoreFormulaController->toEdit->系统异常");
        }
        return "pages/manager/store/storeFormulaEdit";
    }
    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave(StoreFormulaDTO dto){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.storeFormulaService.saveFormula(dto,spo.getId());
            }
        }catch (Exception e){
            logger.error("StoreFormulaController->doSave->系统异常",e);

        }
        return new ResponseVO(-1,"失败",null);
    }

}
