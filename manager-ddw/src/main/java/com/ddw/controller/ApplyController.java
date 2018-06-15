package com.ddw.controller;

import com.ddw.beans.BannerDTO;
import com.ddw.beans.StorePO;
import com.ddw.servies.BannerService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jacky on 2018/4/20.
 */
@Controller
@RequestMapping("/manager/apply")
public class ApplyController {
    private final Logger logger = Logger.getLogger(ApplyController.class);

    @Autowired
    private BannerService bannerService;
    @Autowired
    private StoreService storeService;

    @GetMapping("banner/to-paging")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("bPage",bannerService.findList(spo.getId(),pageNo));
        }catch (Exception e){
            logger.error("ApplyController->list",e);
        }
        return "pages/manager/banner/list";
    }

    @GetMapping("banner/to-edit")
    public String toEdit(){
        return "pages/manager/banner/edit";
    }

    @PostMapping("banner/do-save")
    @ResponseBody
    public ResponseVO doSave(BannerDTO bannerDTO){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.bannerService.save(spo.getId(),bannerDTO);
        }catch (Exception e){
            logger.error("ApplyController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
}
