package com.ddw.controller;

import com.ddw.beans.DoubiDTO;
import com.ddw.beans.StorePO;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.DouBiService;
import com.ddw.servies.OrderService;
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
@RequestMapping("/manager/doubi")
public class DoubiController {

    private final Logger logger = Logger.getLogger(DoubiController.class);

    @Autowired
    private DouBiService douBiService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",douBiService.findPage(pageNo));
        }catch (Exception e){
            logger.error("doubiController->toPaging",e);
        }
        return "pages/manager/headquarters/doubi/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.douBiService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("doubiController->toEdtitPage",e);
        }
        return "pages/manager/headquarters/doubi/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(DoubiDTO dto){
        try {
            return this.douBiService.save(dto);
        }catch (Exception e){
            logger.error("doubiController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("do-makesure")
    @ResponseBody
    public ResponseVO doMakeSure(String orderStr){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.orderService.updateOrderStatus(null, ShipStatusEnum.ShipStatus5.getCode(),spo.getId(),orderStr);

            }
        }catch (Exception e){
            logger.error("doubiController->makesure",e);
        }
        return new ResponseVO(-1,"操作失败",null);

    }
    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdate(String idStr,Integer status){
        try {

            return this.douBiService.update(idStr,status);
        }catch (Exception e){
            logger.error("doubiController->doUpdate",e);
            return new ResponseVO(-1,"操作失败",null);
        }

    }
}
