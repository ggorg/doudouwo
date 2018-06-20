package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.services.ReviewBannerService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/reviewBanner")
public class ReviewBannerController {

    private final Logger logger = Logger.getLogger(ReviewBannerController.class);

    @Autowired
    private ReviewBannerService reviewBannerService;

    /**
     * 总店-banner列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
       try {
           Page page = this.reviewBannerService.findBannerPageByHq(pageNo);
           model.addAttribute("rPage",this.reviewBannerService.findBannerPageByHq(pageNo));
       }catch (Exception e){
           logger.error("ReviewBannerController->toReviewPage",e);
       }
       return "pages/manager/reviewBanner/list";

    }

    /**
     * 根据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.reviewBannerService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            if (reviewPO != null) {
                model.addAttribute("reviewBanner",this.reviewBannerService.getReviewBannerByCode(reviewPO.getDrBusinessCode()));
            }
            return "pages/manager/reviewBanner/reviewInfo";
        }catch (Exception e){
            logger.error("ReviewBannerController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewBanner/reviewInfo";
    }

    @PostMapping("do-updateEnable")
    @ResponseBody
    public ResponseVO doUpdateEnable(String drBusinessCode,Integer enable){
        try {
            return this.reviewBannerService.updateEnable(drBusinessCode,enable);

        }catch (Exception e){
            logger.error("ReviewBannerController->doUpdateEnable",e);
        }
        return new ResponseVO(-1,"操作失败",null);

    }

}
