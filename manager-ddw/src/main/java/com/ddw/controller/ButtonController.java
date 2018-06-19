package com.ddw.controller;

import com.ddw.beans.ButtonDTO;
import com.ddw.beans.ButtonPO;
import com.ddw.servies.ButtonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jacky on 2018/6/17.
 */
@Controller
@RequestMapping("/manager/button")
public class ButtonController {
    private final Logger logger = Logger.getLogger(ButtonController.class);
    @Autowired
    private ButtonService buttonService;

    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",buttonService.findPage(pageNo));
        }catch (Exception e){
            logger.error("ButtonController->toPaging",e);
        }
        return "pages/manager/button/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                ButtonPO buttonPO = buttonService.query(Integer.valueOf(id));
                model.addAttribute("buttonPO", buttonPO);
            }
        }catch (Exception e){
            logger.error("ButtonController->toEdtitPage",e);
        }
        return "pages/manager/button/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(ButtonDTO dto){
        try {
            return this.buttonService.saveOrUpdate(dto);
        }catch (Exception e){
            logger.error("ButtonController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(Integer id){
        try {
            return this.buttonService.delete(id);
        }catch (Exception e){
            logger.error("ButtonController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
