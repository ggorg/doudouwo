package com.ddw.controller;


import com.ddw.beans.GoddessDTO;
import com.ddw.beans.ReviewPO;
import com.ddw.beans.StorePO;
import com.ddw.services.ReviewGoddessService;
import com.ddw.servies.GoddessService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/goddess")
public class GoddessController {

    private final Logger logger = Logger.getLogger(GoddessController.class);

    @Autowired
    private ReviewGoddessService reviewGoddessService;
    @Autowired
    private GoddessService goddessService;
    @Autowired
    private StoreService storeService;

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

    @GetMapping("list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("goddessPage",goddessService.findPage(pageNo,spo.getId()));
        }catch (Exception e){
            logger.error("GoddessController->list",e);
        }
        return "pages/manager/goddess/list";
    }

    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {
            if(id != null && id >0) {
                model.addAttribute("goddess", goddessService.getById(id));
            }
        }catch (Exception e){
            logger.error("GoddessController->toEdtitPage",e);
        }
        return "pages/manager/goddess/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(GoddessDTO goddessDTO){
        try {
            return this.goddessService.saveOrUpdate(goddessDTO);
        }catch (Exception e){
            logger.error("GoddessController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
