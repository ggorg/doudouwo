package com.ddw.controller;


import com.ddw.beans.StorePO;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.services.LiveRadioService;
import com.ddw.servies.StoreService;
import com.ddw.util.LiveRadioApiUtil;
import com.ddw.util.Toolsddw;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/liveradio")
public class LiveRadioController {
    private final Logger logger = Logger.getLogger(LiveRadioController.class);

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
            logger.error("LiveRadioController->toPagingByStore",e);
        }
        return "pages/manager/store/liveRadioList";
    }
    @PostMapping("do-create-live-radio")
    @ResponseBody
    public ResponseVO doCreateLiveRadio(String liveRadioBusinessCode){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            return this.liveRadioService.createLiveRadioRoom(liveRadioBusinessCode,spo.getId());

        }catch (Exception e){
            logger.error("LiveRadioController->doCreateLiveRadio",e);
        }
        return new ResponseVO(-1,"创建直播失败",null);

    }
    @PostMapping("do-close-live-radio")
    @ResponseBody
    public ResponseVO doCloseLiveRadio(String streamId){
        try {
            String si= MyEncryptUtil.getRealValue(streamId);
            if(StringUtils.isBlank(si)){
                return new ResponseVO(-2,"参数异常",null);
            }
            boolean flag=LiveRadioApiUtil.closeLoveRadio(si);
            if(flag){
                return new ResponseVO(1,"禁用直播成功",null);

            }else{
                CacheUtil.delete("publicCache","closeCmd-"+streamId);
            }
        }catch (Exception e){
            logger.error("LiveRadioController->doCreateLiveRadio",e);

        }
        return new ResponseVO(-1,"禁用直播失败",null);

    }

}
