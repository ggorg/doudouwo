package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.services.ReviewGoddessService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/reviewGoddess")
public class GoddessController {

    private final Logger logger = Logger.getLogger(GoddessController.class);

    @Autowired
    private ReviewGoddessService reviewGoddessService;

    /**
     * 总店-女神认证列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {

            model.addAttribute("rPage",this.reviewGoddessService.findGoddessPageByHq(pageNo));
        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewPage",e);
        }
        return "pages/manager/reviewGoddess/list";

    }

    /**
     * 根据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.reviewGoddessService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            return "pages/manager/reviewGoddess/reviewInfo";
        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewGoddess/reviewInfo";
    }


}
