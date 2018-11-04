package com.ddw.controller;

import com.ddw.beans.ActivityDTO;
import com.ddw.beans.StorePO;
import com.ddw.beans.TicketDTO;
import com.ddw.enums.ActivityTypeEnum;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.ActivityService;
import com.ddw.servies.OrderService;
import com.ddw.servies.StoreService;
import com.ddw.servies.TicketService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/manager/activity")
public class ActivityController {

    private final Logger logger = Logger.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;



    @Autowired
    private StoreService storeService;




    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            model.addAttribute("sPage",activityService.findPage(pageNo,spo==null?-1:spo.getId()));
        }catch (Exception e){
            logger.error("ActivityController->toPaging",e);
        }
        return "pages/manager/activity/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Integer dtType,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                Map data=this.activityService.getById(Integer.parseInt(id));
                model.addAttribute("ds",data);
                dtType=(Integer) data.get("dtType");

            }
            if(ActivityTypeEnum.type1.getCode().equals(dtType)){
                return "pages/manager/activity/editOutside";
            }else if(ActivityTypeEnum.type2.getCode().equals(dtType)){
                return "pages/manager/activity/editInside";

            }else{
                return "pages/manager/activity/editContext";
            }
        }catch (Exception e){
            logger.error("ActivityController->toEdtitPage",e);
        }
        return "redirect:/404";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(ActivityDTO dto){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());

            return this.activityService.save(dto,spo==null?-1:spo.getId());
        }catch (Exception e){
            logger.error("ActivityController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }


    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdate(String idStr,Integer status){
        try {
            return this.activityService.update(idStr,status);
        }catch (Exception e){
            logger.error("ActivityController->doUpdate",e);
            return new ResponseVO(-1,"操作失败",null);
        }

    }

}
