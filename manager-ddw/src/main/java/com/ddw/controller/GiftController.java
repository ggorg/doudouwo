package com.ddw.controller;

import com.ddw.beans.GiftDTO;

import com.ddw.servies.GiftService;

import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/gift")
public class GiftController {

    private final Logger logger = Logger.getLogger(GiftController.class);

    @Autowired
    private GiftService giftService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",giftService.findPage(pageNo));
        }catch (Exception e){
            logger.error("GiftController->toPaging",e);
        }
        return "pages/manager/gift/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.giftService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("GiftController->toEdtitPage",e);
        }
        return "pages/manager/gift/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(GiftDTO dto){
        try {
            return this.giftService.save(dto);
        }catch (Exception e){
            logger.error("GiftController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

}
