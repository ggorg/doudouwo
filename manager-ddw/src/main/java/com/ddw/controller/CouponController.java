package com.ddw.controller;

import com.ddw.beans.CouponDTO;
import com.ddw.beans.StorePO;
import com.ddw.servies.CouponService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/coupon")
public class CouponController {

    private final Logger logger = Logger.getLogger(CouponController.class);

    @Autowired
    private CouponService couponService;

    @Autowired
    private StoreService storeService;

    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",couponService.findPage(pageNo));
        }catch (Exception e){
            logger.error("couponController->toPaging",e);
        }
        return "pages/manager/coupon/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.couponService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("couponController->toEdtitPage",e);
        }
        return "pages/manager/coupon/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(CouponDTO dto){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.couponService.save(dto,spo.getId());

            }

        }catch (Exception e){
            logger.error("couponController->doEditPage",e);
        }
        return new ResponseVO(-1,"提交失败",null);

    }



}
