package com.ddw.controller;

import com.ddw.beans.MaterialDTO;
import com.ddw.beans.StoreDTO;
import com.ddw.servies.StoreService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/manager/store")
public class StoreController {

    private final Logger logger = Logger.getLogger(StoreController.class);

    @Autowired
    private StoreService storeService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",storeService.findPage(pageNo));
        }catch (Exception e){
            logger.error("StoreController->toPaging",e);
        }
        return "pages/manager/store/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(String idStr,Model model){
        try {

            String id= MyEncryptUtil.getRealValue(idStr);
            if(StringUtils.isNotBlank(id)){
                model.addAttribute("ds",this.storeService.getById(Integer.parseInt(id)));
            }

        }catch (Exception e){
            logger.error("StoreController->toEdtitPage",e);
        }
        return "pages/manager/store/edit";
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(StoreDTO storeDTO){
        try {
            return this.storeService.save(storeDTO);
        }catch (Exception e){
            logger.error("StoreController->doEditPage",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }
    @GetMapping("to-relate-sysuser")
    public String toRelateSysUser(String idStr, Model model){
        try {

            model.addAttribute("userList",this.storeService.getRelateSysUsers(idStr));


        }catch (Exception e){
            logger.error("StoreController->toRelate",e);

        }
        return "pages/manager/store/relateSysUser";
    }
    @PostMapping("do-relate-sysuser")
    @ResponseBody
    public ResponseVO doRelateSysUser(String idStr,Integer[] sysusers){
        try {
            return this.storeService.saveRelateSysUsers(idStr,sysusers);
        }catch (Exception e){
            logger.error("StoreController->doRelateSysUser",e);
            return new ResponseVO(-1,"关联用户失败",null);
        }
    }
}
