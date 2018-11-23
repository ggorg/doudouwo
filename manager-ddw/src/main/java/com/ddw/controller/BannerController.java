package com.ddw.controller;

import com.ddw.beans.BannerDTO;
import com.ddw.beans.StorePO;
import com.ddw.enums.BannerTypeEnum;
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
@RequestMapping("/manager/banner")
public class BannerController {
    private final Logger logger = Logger.getLogger(BannerController.class);

    @Autowired
    private BannerService bannerService;
    @Autowired
    private StoreService storeService;

    @GetMapping("to-paging")
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("bPage",bannerService.findList(spo.getId(),pageNo, BannerTypeEnum.type1));
        }catch (Exception e){
            logger.error("BannerController->list",e);
        }
        return "pages/manager/banner/list";
    }

    @GetMapping("to-edit")
    public String toEdit(){
        return "pages/manager/banner/edit";
    }

    @PostMapping("do-save")
    @ResponseBody
    public ResponseVO doSave(BannerDTO bannerDTO){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.bannerService.save(spo.getId(),bannerDTO);
        }catch (Exception e){
            logger.error("BannerController->doEdit",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @PostMapping("do-shop-banner-save")
    @ResponseBody
    public ResponseVO doShopSave(BannerDTO bannerDTO){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.bannerService.saveShopBanner(bannerDTO,spo.getId());
        }catch (Exception e){
            logger.error("BannerController->doShopSave",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @GetMapping("to-shop-banner-paging")
    public String shopBannerList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("bPage",bannerService.findList(spo.getId(),pageNo, BannerTypeEnum.type2));
        }catch (Exception e){
            logger.error("BannerController->shopBannerList",e);
        }
        return "pages/manager/banner/shopBannerList";
    }
    @GetMapping("to-shop-banner-edit")
    public String toShopBannerEdit(Integer id, Model model){
        try {
            if(id!=null && id>0){
                StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
                model.addAttribute("banner",bannerService.getByIdAndType(id,spo.getId(),BannerTypeEnum.type2));
            }

        }catch (Exception e){
            logger.error("BannerController->toShopBannerEdit",e);

        }
        return "pages/manager/banner/shopBannerEdit";
    }
    @PostMapping("do-update-banner-status")
    @ResponseBody
    public ResponseVO doBannerUpdate(Integer id,Integer status){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.bannerService.update(id,status,spo.getId());
        }catch (Exception e){
            logger.error("BannerController->doBannerUpdate",e);
            return new ResponseVO(-1,"操作失败",null);
        }

    }
}
