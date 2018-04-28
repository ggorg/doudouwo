package com.ddw.controller;


import com.ddw.beans.StorePO;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.services.LiveRadioService;
import com.ddw.servies.StoreService;
import com.ddw.util.Toolsddw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager/liveradio")
public class LiveRadioController {

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/to-paging-by-store")
    public String toPagingByStore(@RequestParam(defaultValue = "1") Integer pageNo,Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("page",this.liveRadioService.findPage(pageNo,spo.getId()));

        }catch (Exception e){
            //logger.error("MaterialController->toPage",e);
        }
        return "pages/manager/store/liveRadioList";
    }
}
