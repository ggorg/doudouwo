package com.ddw.controller;

import com.ddw.beans.MaterialDTO;
import com.ddw.servies.MaterialService;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 原材料
 */
@RequestMapping("/manager/hq/material")
@Controller
public class MaterialController {

    private final Logger logger = Logger.getLogger(MaterialController.class);

    @Autowired
    private MaterialService materialService;

    @GetMapping("to-paging-{dmStatus}")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo,@PathVariable Integer dmStatus, Model model){
        try {
            model.addAttribute("mPage",materialService.findPage(pageNo,dmStatus));
        }catch (Exception e){
            logger.error("MaterialController->toPage",e);
        }
        return "pages/manager/headquarters/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {
            model.addAttribute("dm",this.materialService.getById(id));
        }catch (Exception e){
            logger.error("MaterialController->toEdtitPage",e);
        }
        return "pages/manager/headquarters/edit";
    }
    @GetMapping("to-inbound")
    public String toInbound(Model model){
        return "pages/manager/headquarters/inBound";
    }
    @PostMapping("do-inbound")
    @ResponseBody
    public ResponseVO doInbound(String idStr,Integer num){
        try {
            Map map=(Map)Tools.getSession("user");
            return this.materialService.inbound(idStr,num,(Integer)map.get("id"));
        }catch (Exception e){
            logger.error("MaterialController->toInbound",e);
            return new ResponseVO(-1,"入库失败",null);
        }

    }
    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(MaterialDTO materialDTO){
        try {
            return this.materialService.save(materialDTO);
        }catch (Exception e){
            logger.error("MaterialController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
    @PostMapping("do-update-status")
    @ResponseBody
    public ResponseVO doUpdateStatus(String idStr,Integer status){
        try {
            return this.materialService.updateStatus(idStr,status);
        }catch (Exception e){
            logger.error("MaterialController->doUpdateStatus",e);
            return new ResponseVO(-1,"修改失败",null);
        }
    }
}
