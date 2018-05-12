package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.services.ReviewPracticeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/reviewPractice")
public class PracticeController {

    private final Logger logger = Logger.getLogger(PracticeController.class);

    @Autowired
    private ReviewPracticeService peviewPracticeService;

    /**
     * 总店-代练认证列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {

            model.addAttribute("rPage",this.peviewPracticeService.findPracticePageByHq(pageNo));
        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewPage",e);
        }
        return "pages/manager/reviewPractice/list";

    }

    /**
     * 根据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.peviewPracticeService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            return "pages/manager/reviewPractice/reviewInfo";
        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewPractice/reviewInfo";
    }


}
