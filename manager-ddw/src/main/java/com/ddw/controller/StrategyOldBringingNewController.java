package com.ddw.controller;

import com.ddw.beans.StrategyOldBringingNewDTO;
import com.ddw.servies.*;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 老带新后台管理
 */
@RequestMapping("/manager/oldBringingNew")
@Controller
public class StrategyOldBringingNewController {
    private final Logger logger = Logger.getLogger(StrategyOldBringingNewController.class);
    @Autowired
    private CouponService couponService;
    @Autowired
    private StrategyOldBringingNewService strategyOldBringingNewService;
    @Autowired
    private GradeService gradeService;

    @GetMapping("to-paging")
    public String  toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try{
            model.addAttribute("gradePage",gradeService.findList(null));
            model.addAttribute("spage",this.strategyOldBringingNewService.findPage(pageNo));
        }catch (Exception e){
            logger.error("StrategyOldBringingNewController->toPaging->系统异常");
        }
    return "pages/manager/strategyOldBringingNew/oldBringingNewList";
    }


    @GetMapping("to-edit")
    public String  toEdit(Integer idStr, Model model){
        try{
            model.addAttribute("gradePage",gradeService.findList(null));
            model.addAttribute("couponList",this.couponService.getHeadOffice());
            model.addAttribute("strategy",this.strategyOldBringingNewService.getById(idStr));
        }catch (Exception e){
            logger.error("StrategyOldBringingNewController->toEdit->系统异常");
        }
        return "pages/manager/strategyOldBringingNew/oldBringingNewEdit";
    }

    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave(StrategyOldBringingNewDTO dto){
        try {
            return this.strategyOldBringingNewService.saveOrUpdate(dto);
        }catch (Exception e){
            logger.error("StrategyOldBringingNewController->doSave->系统异常",e);

        }
        return new ResponseVO(-1,"失败",null);
    }

}
