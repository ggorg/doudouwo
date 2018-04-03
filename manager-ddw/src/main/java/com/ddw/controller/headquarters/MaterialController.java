package com.ddw.controller.headquarters;

import com.ddw.beans.headquarters.MaterialDTO;
import com.ddw.servies.headquarters.MaterialService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/hq/material")
public class MaterialController {
    private final Logger logger = Logger.getLogger(MaterialController.class);

    @Autowired
    private MaterialService materialService;

    @GetMapping("to-paging")
    public String toPaging(Integer pageNo, Model model){
        try {
            model.addAttribute("page",materialService.findPage(pageNo));
        }catch (Exception e){
            logger.error("MaterialController->toPage",e);
        }
        return "pages/headquarters/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(){
        try {

        }catch (Exception e){

        }
        return "pages/headquarters/edit";
    }
    @PostMapping("do-edit")
    public String doEditPage(MaterialDTO materialDTO){
        
    return null;
    }
}
