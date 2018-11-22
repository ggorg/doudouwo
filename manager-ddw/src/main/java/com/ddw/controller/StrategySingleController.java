package com.ddw.controller;

import com.ddw.beans.StrategyDTO;
import com.ddw.servies.CouponService;
import com.ddw.servies.GradeService;
import com.ddw.servies.StrategySingleService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jacky on 2018/5/29.
 */
@Controller
@RequestMapping("/manager/user/strategySingle")
public class StrategySingleController {
    private final Logger logger = Logger.getLogger(StrategySingleController.class);
    @Autowired
    private StrategySingleService strategySingleService;
    @Autowired
    private GradeService gradeService;
    @Autowired
    private CouponService couponService;


    @GetMapping("list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("gradePage",gradeService.findList(null));
            model.addAttribute("strategyPage",strategySingleService.findPage(pageNo));
        }catch (Exception e){
            logger.error("StrategySingleController->list",e);
        }
        return "pages/manager/strategySingle/list";
    }

    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {
            model.addAttribute("gradePage",gradeService.findList(null));
            model.addAttribute("couponList",this.couponService.getHeadOffice());
            model.addAttribute("strategy", strategySingleService.getById(id));
        }catch (Exception e){
            logger.error("StrategySingleController->toEdtitPage",e);
        }
        return "pages/manager/strategySingle/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(StrategyDTO strategyDTO){
        try {
            return this.strategySingleService.saveOrUpdate(strategyDTO);
        }catch (Exception e){
            logger.error("StrategySingleController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(int id){
        try {
            return this.strategySingleService.delete(id);
        }catch (Exception e){
            logger.error("StrategySingleController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
