package com.ddw.controller;

import com.ddw.beans.StrategyDTO;
import com.ddw.servies.GradeService;
import com.ddw.servies.StrategyCumulationService;
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
@RequestMapping("/manager/user/strategyCumulation")
public class StrategyCumulationController {
    private final Logger logger = Logger.getLogger(StrategyCumulationController.class);
    @Autowired
    private StrategyCumulationService strategyCumulationService;
    @Autowired
    private GradeService gradeService;


    @GetMapping("list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("gradePage",gradeService.findList(null));
            model.addAttribute("strategyPage",strategyCumulationService.findPage(pageNo));
        }catch (Exception e){
            logger.error("StrategyCumulationController->list",e);
        }
        return "pages/manager/strategyCumulation/list";
    }

    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {
            model.addAttribute("gradePage",gradeService.findList(null));
            if(id != null && id >0) {
                model.addAttribute("strategy", strategyCumulationService.getById(id));
            }
        }catch (Exception e){
            logger.error("StrategyCumulationController->toEdtitPage",e);
        }
        return "pages/manager/strategyCumulation/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(StrategyDTO strategyDTO){
        try {
            return this.strategyCumulationService.saveOrUpdate(strategyDTO);
        }catch (Exception e){
            logger.error("StrategyCumulationController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(int id){
        try {
            return this.strategyCumulationService.delete(id);
        }catch (Exception e){
            logger.error("StrategyCumulationController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
