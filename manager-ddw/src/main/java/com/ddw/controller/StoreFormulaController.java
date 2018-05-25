package com.ddw.controller;

import com.ddw.beans.StorePO;
import com.ddw.servies.StoreFormulaService;
import com.ddw.servies.StoreMaterialService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

}
