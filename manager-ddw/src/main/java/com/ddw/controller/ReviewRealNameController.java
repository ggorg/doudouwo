package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.enums.RoleTypeEnum;
import com.ddw.servies.ReviewRealNameService;
import com.ddw.servies.RoleService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager/reviewRealName")
public class ReviewRealNameController {

    private final Logger logger = Logger.getLogger(ReviewRealNameController.class);

    @Autowired
    private ReviewRealNameService reviewRealNameService;

    @Autowired
    private RoleService roleService;

    /**
     * 总店-会员实名认证列表
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/to-review-page")
    public String toReviewPageByHq(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
       try {

           model.addAttribute("rPage",this.reviewRealNameService.findMaterialPageByHq(pageNo));
       }catch (Exception e){
           logger.error("ReviewRealNameController->toReviewPage",e);
       }
       return "pages/manager/reviewRealName/list";

    }

    /**
     * 门店-跟据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {
            ReviewPO reviewPO = this.reviewRealNameService.getReviewById(id);
            model.addAttribute("review",reviewPO);
            if (reviewPO != null) {
                model.addAttribute("reviewRealName",this.reviewRealNameService.getReviewRealNameByCode(reviewPO.getDrBusinessCode()));
            }
            return "pages/manager/reviewRealName/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewRealNameController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/reviewRealName/reviewInfo";
    }
    /**
     * 审批情况
     * @return
     */
    @GetMapping("/to-review-info-html")
    public String  toReviewInfoHtml(String businessCode,Model model){
        try {
            String on= MyEncryptUtil.getRealValue(businessCode);
            if(StringUtils.isBlank(on)){
                return "redirect:/404";
            }

            model.addAttribute("review",this.reviewRealNameService.getReviewByBusinessCode(on));
            return "pages/manager/reviewRealName/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewRealNameController->doApplyExitBack",e);
        }

        return "pages/manager/reviewRealName/reviewInfo";
    }

    /**
     * 总店-前往审批页面
     * @param businessCode
     * @param model
     * @return
     */
    @GetMapping("to-review-by-hq-html")
    public String toReviewByHqHtml(String businessCode, Model model){
       try {
           List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
           if(roleList!=null && roleList.size()>0){
               String on=MyEncryptUtil.getRealValue(businessCode);
               if(StringUtils.isNotBlank(on)){

                   model.addAttribute("review",this.reviewRealNameService.getReviewByBusinessCode(on));
               }

                return "pages/manager/review/reviewByHq";
           }
       }catch (Exception e){
           logger.error("ReviewRealNameController->toReviewByHqHtml",e);

       }
       return "redirect:/404";
    }

    /**
     * 总店-审批提交
     * @param businessCode
     * @param model
     * @return
     */
    @PostMapping("do-review-by-hq")
    @ResponseBody
    public ResponseVO doReviewByHq(Integer id,String businessCode,  Integer drReviewStatus, String drReviewDesc, Model model){
       try {
          if(StringUtils.isBlank(ReviewStatusEnum.getName(drReviewStatus))){
               return new ResponseVO(-2,"参数异常",null);
           }
           List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
           if(roleList!=null && roleList.size()>0){
               String on=MyEncryptUtil.getRealValue(businessCode);
               if(StringUtils.isNotBlank(on)){

                  ResponseVO res=this.reviewRealNameService.editReivew(id,on,drReviewDesc,ReviewStatusEnum.get(drReviewStatus),ReviewBusinessTypeEnum.ReviewBusinessType1,Toolsddw.getUserMap());
                   if(res.getReCode()==1){
                       return new ResponseVO(1,"提交审批成功",null);
                   }
               }
           }
       }catch (Exception e){
           logger.error("ReviewRealNameController->toReviewExitBackHtml",e);

       }
        return new ResponseVO(-1,"提交审批失败",null);

    }


}
