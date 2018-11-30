package com.ddw.controller;

import com.ddw.beans.GradeDTO;
import com.ddw.beans.GradePO;
import com.ddw.servies.GoddessGradeService;
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
@RequestMapping("/manager/goddess/grade")
public class GoddessGradeController {
    private final Logger logger = Logger.getLogger(GoddessGradeController.class);

    @Autowired
    private GoddessGradeService goddessGradeService;

    @GetMapping("list")
    public String list(String gradeName,Model model){
        try {
            model.addAttribute("gradeList",goddessGradeService.findList(gradeName));
        }catch (Exception e){
            logger.error("GoddessGradeController->list",e);
        }
        return "pages/manager/grade/goddessList";
    }

    @GetMapping("to-edit")
    public String toEdit(String id,Model model){
        try {
            if(StringUtils.isNotBlank(id)) {
                GradePO gradePO = goddessGradeService.selectById(id);
                model.addAttribute("gradePO", gradePO);
            }
        }catch (Exception e){
            logger.error("GoddessGradeController->update",e);
        }
        return "pages/manager/grade/goddessEdit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEdit(GradeDTO gradeDTO){
        try {
            return this.goddessGradeService.saveOrUpdate(gradeDTO);
        }catch (Exception e){
            logger.error("GoddessGradeController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("delete")
    @ResponseBody
    public ResponseVO delete(String id){
        try {
            return this.goddessGradeService.delete(id);
        }catch (Exception e){
            logger.error("GoddessGradeController->delete",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
