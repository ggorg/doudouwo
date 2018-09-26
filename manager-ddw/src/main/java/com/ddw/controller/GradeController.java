package com.ddw.controller;

import com.ddw.beans.GradeDTO;
import com.ddw.beans.GradePO;
import com.ddw.servies.GradeService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Jacky on 2018/4/20.
 */
@Controller
@RequestMapping("/manager/user/grade")
public class GradeController {
    private final Logger logger = Logger.getLogger(GradeController.class);

    @Autowired
    private GradeService gradeService;

    @GetMapping("list")
    public String list(String gradeName,Model model){
        try {
            model.addAttribute("gradeList",gradeService.findList(gradeName));
        }catch (Exception e){
            logger.error("GradeController->list",e);
        }
        return "pages/manager/grade/list";
    }

    @GetMapping("to-edit")
    public String toEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                GradePO gradePO = gradeService.selectById(id);
                model.addAttribute("gradePO", gradePO);
            }
        }catch (Exception e){
            logger.error("GradeController->update",e);
        }
        return "pages/manager/grade/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEdit(GradeDTO gradeDTO){
        try {
            return this.gradeService.saveOrUpdate(gradeDTO);
        }catch (Exception e){
            logger.error("GradeController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(String id){
        try {
            return this.gradeService.delete(id);
        }catch (Exception e){
            logger.error("GradeController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
