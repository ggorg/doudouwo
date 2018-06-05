package com.ddw.controller;

import com.ddw.beans.GiftDTO;
import com.ddw.beans.TicketDTO;
import com.ddw.servies.GiftService;
import com.ddw.servies.TicketService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/ticket")
public class TicketController {

    private final Logger logger = Logger.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",ticketService.findPage(pageNo));
        }catch (Exception e){
            logger.error("TicketController->toPaging",e);
        }
        return "pages/manager/headquarters/ticket/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.ticketService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("TicketController->toEdtitPage",e);
        }
        return "pages/manager/headquarters/ticket/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(TicketDTO dto){
        try {
            return this.ticketService.save(dto);
        }catch (Exception e){
            logger.error("TicketController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdate(String idStr,Integer status){
        try {
            return this.ticketService.update(idStr,status);
        }catch (Exception e){
            logger.error("TicketController->doUpdate",e);
            return new ResponseVO(-1,"操作失败",null);
        }

    }

}
