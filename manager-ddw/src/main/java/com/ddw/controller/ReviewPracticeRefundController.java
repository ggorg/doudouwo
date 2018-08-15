package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.services.ReviewPracticeRefundService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/reviewPracticeRefund")
public class ReviewPracticeRefundController {

    private final Logger logger = Logger.getLogger(ReviewPracticeRefundController.class);

    @Autowired
    private ReviewPracticeRefundService reviewPracticeRefundService;

    /**
     * 代练订单退款申请列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPage(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
       try {
           model.addAttribute("refundPage",this.reviewPracticeRefundService.findRefundPageByHq(pageNo));
       }catch (Exception e){
           logger.error("ReviewPracticeRefundController->toReviewPage",e);
       }
       return "pages/manager/reviewPracticeRefund/list";

    }

    /**
     * 根据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.reviewPracticeRefundService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            if (reviewPO != null) {
                model.addAttribute("reviewPracticeRefund",this.reviewPracticeRefundService.getReviewRefundByCode(reviewPO.getDrBusinessCode()));
            }
            return "pages/manager/reviewPracticeRefund/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewPracticeRefundController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewPracticeRefund/reviewInfo";
    }

}
