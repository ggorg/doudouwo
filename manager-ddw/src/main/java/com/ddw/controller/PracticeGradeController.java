package com.ddw.controller;

import com.ddw.beans.GradeDTO;
import com.ddw.beans.GradePO;
import com.ddw.servies.PracticeGradeService;
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
@RequestMapping("/manager/practice/grade")
public class PracticeGradeController {
    private final Logger logger = Logger.getLogger(PracticeGradeController.class);

    @Autowired
    private PracticeGradeService practiceGradeService;

    @GetMapping("list")
    public String list(String gradeName,Model model){
        try {
            model.addAttribute("gradeList",practiceGradeService.findList(gradeName));
        }catch (Exception e){
            logger.error("PracticeGradeController->list",e);
        }
        return "pages/manager/grade/practiceList";
    }

    @GetMapping("to-edit")
    public String toEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                GradePO gradePO = practiceGradeService.selectById(id);
                model.addAttribute("gradePO", gradePO);
            }
        }catch (Exception e){
            logger.error("PracticeGradeController->update",e);
        }
        return "pages/manager/grade/practiceEdit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEdit(GradeDTO gradeDTO){
        try {
            return this.practiceGradeService.saveOrUpdate(gradeDTO);
        }catch (Exception e){
            logger.error("PracticeGradeController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(String id){
        try {
            return this.practiceGradeService.delete(id);
        }catch (Exception e){
            logger.error("PracticeGradeController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
