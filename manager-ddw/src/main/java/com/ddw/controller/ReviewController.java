package com.ddw.controller;


import com.ddw.beans.ReviewPO;
import com.ddw.beans.StorePO;
import com.ddw.enums.*;
import com.ddw.servies.OrderService;
import com.ddw.servies.ReviewService;
import com.ddw.servies.RoleService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager/review")
public class ReviewController {

    private final Logger logger = Logger.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/to-review-page")
    public String toReviewPage(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
       try {
           model.addAttribute("rPage",this.reviewService.findPage(pageNo));
       }catch (Exception e){
           logger.error("ReviewController->toReviewPage",e);
       }
       return "pages/manager/review/list";

    }
    /**
     * 门店-跟据ID审批情况
     * @return
     */
    @GetMapping("/to-review-info-by-id-html")
    public String  toReviewInfoByIdHtml(Integer id,Model model){
        try {

            model.addAttribute("review",this.reviewService.getReviewById(id));
            return "pages/manager/review/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewController->toReviewInfoByIdHtml",e);
        }

        return "pages/manager/review/reviewInfo";
    }
    /**
     * 门店-退还申请页面
     * @return
     */
    @GetMapping("/to-exit-back-html")
    public String  toExitBackHtml(String orderNo,Model model){
        try {
            String on= MyEncryptUtil.getRealValue(orderNo);
            if(StringUtils.isBlank(on)){
                return "redirect:/404";
            }
            ReviewPO rpo=this.reviewService.getReviewByBusinessCode(on);
           // List datas=this.reviewService.getReviewRecord(on);
            if(rpo==null || rpo.getDrReviewStatus()==null ||ReviewStatusEnum.ReviewStatus2.getCode().equals(rpo.getDrReviewStatus())){
                return "pages/manager/review/exitBackSubmit";
            }

        }catch (Exception e){
            logger.error("ReviewController->doApplyExitBack",e);

        }
        return "redirect:/404";

    }
    /**
     * 门店-审批情况
     * @return
     */
    @GetMapping("/to-review-info-html")
    public String  toReviewInfoHtml(String orderNo,Model model){
        try {
            String on= MyEncryptUtil.getRealValue(orderNo);
            if(StringUtils.isBlank(on)){
                return "redirect:/404";
            }

            model.addAttribute("review",this.reviewService.getReviewByBusinessCode(on));
            return "pages/manager/review/reviewInfo";


        }catch (Exception e){
            logger.error("ReviewController->doApplyExitBack",e);
        }

        return "pages/manager/review/reviewInfo";
    }
    /**
     * 门店-申请退还
     * @param orderNo
     * @return
     */
    @PostMapping("do-apply-exitback")
    @ResponseBody
    public ResponseVO doApplyExitBack(String orderNo,String drApplyDesc){
        try {
            String on= MyEncryptUtil.getRealValue(orderNo);
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo==null){
                return new ResponseVO(-2,"当前用户没有权限操作此能",null);
            }
            if(StringUtils.isBlank(on)){
                return new ResponseVO(-2,"订单号异常",null);
            }
            boolean flag=this.reviewService.hasReviewFromStore(on, ReviewBusinessTypeEnum.ReviewBusinessType1, ReviewBusinessStatusEnum.ReviewBusinessStatus4);
            if(flag){
                return new ResponseVO(-2,"抱歉，当前记录已被审核过",null);

            }else{
                ResponseVO res=this.reviewService.saveReviewFromStore(on,drApplyDesc,ReviewBusinessTypeEnum.ReviewBusinessType1, ReviewBusinessStatusEnum.ReviewBusinessStatus4, Toolsddw.getUserMap());
                if(res.getReCode()==1){
                    res=this.orderService.updateOrderStatus(PayStatusEnum.PayStatus1.getCode(), ShipStatusEnum.ShipStatus4.getCode(),spo.getId(),orderNo);
                    if(res.getReCode()==1){
                        return new ResponseVO(1,"提交申请成功",null);
                    }
                }
            }
        }catch (Exception e){
            logger.error("ReviewController->doApplyExitBack",e);
        }
        return new ResponseVO(-1,"提交申请失败",null);
    }

    /**
     * 总店-退还受理页面
     * @param orderNo
     * @param model
     * @return
     */
    @GetMapping("to-review-exitback-html")
    public String toReviewExitBackHtml(String orderNo, Model model){
       try {
           List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
           if(roleList!=null && roleList.size()>0){
               String on=MyEncryptUtil.getRealValue(orderNo);
               if(StringUtils.isNotBlank(on)){

                   model.addAttribute("review",this.reviewService.getReviewByBusinessCode(on));
               }

                return "pages/manager/review/reviewExitBack";
           }
       }catch (Exception e){
           logger.error("ReviewController->toReviewExitBackHtml",e);

       }
       return "redirect:/404";
    }

    /**
     * 总店-退还受理提交
     * @param orderNo
     * @param model
     * @return
     */
    @PostMapping("do-review-exitback")
    @ResponseBody
    public ResponseVO doReviewExitBack(Integer id,String orderNo,  Integer drReviewStatus, String drReviewDesc, Model model){
       try {
          if(StringUtils.isBlank(ReviewStatusEnum.getName(drReviewStatus))){
               return new ResponseVO(-2,"参数异常",null);
           }
           List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(), RoleTypeEnum.RoleType1_0.getCode());
           if(roleList!=null && roleList.size()>0){
               String on=MyEncryptUtil.getRealValue(orderNo);
               if(StringUtils.isNotBlank(on)){

                  ResponseVO res=this.reviewService.editReivewFromHq(id,on,drReviewDesc,ReviewStatusEnum.get(drReviewStatus),ReviewBusinessTypeEnum.ReviewBusinessType1,Toolsddw.getUserMap());
                   if(res.getReCode()==1){
                       return new ResponseVO(1,"提交审批成功",null);
                   }
               }
           }
       }catch (Exception e){
           logger.error("ReviewController->toReviewExitBackHtml",e);

       }
        return new ResponseVO(-1,"提交审批失败",null);

    }

}
