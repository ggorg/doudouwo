package com.ddw.controller;

import com.ddw.beans.StorePO;
import com.ddw.beans.TableDTO;
import com.ddw.beans.TicketDTO;
import com.ddw.enums.ShipStatusEnum;
import com.ddw.servies.GoodFriendPlayTableService;
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

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/manager/table")
public class GoodFriendPlayTableController {

    private final Logger logger = Logger.getLogger(GoodFriendPlayTableController.class);

    @Autowired
    private GoodFriendPlayTableService goodFriendPlayTableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreService storeService;


    @GetMapping("to-paging")
    public String toPaging(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("sPage",goodFriendPlayTableService.findPage(pageNo));
        }catch (Exception e){
            logger.error("GoodFriendPlayTableController->toPaging",e);
        }
        return "pages/manager/store/table/list";
    }
    @GetMapping("to-edit")
    public String toEdtitPage(Integer id,Model model){
        try {


            model.addAttribute("ds",this.goodFriendPlayTableService.getById(id));


        }catch (Exception e){
            logger.error("GoodFriendPlayTableController->toEdtitPage",e);
        }
        return "pages/manager/store/table/edit";
    }

    @GetMapping("to-loadQrCode")
    public void toLoadQrCode(Integer id, HttpServletResponse res){
        try {

            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
           this.goodFriendPlayTableService.loadQrCode(id,spo,res);
        }catch (Exception e){
            logger.error("GoodFriendPlayTableController->to-loadQrCode",e);
        }
    }

    @PostMapping("do-edit")
    @ResponseBody
    public ResponseVO doEditPage(TableDTO dto){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
                return this.goodFriendPlayTableService.save(dto,spo.getId());
            }
        }catch (Exception e){
            logger.error("GoodFriendPlayTableController->doEditPage",e);

        }
        return new ResponseVO(-1,"提交失败",null);

    }

    @PostMapping("do-delete")
    @ResponseBody
    public ResponseVO doDelete(Integer id){
        try {
            StorePO spo=this.storeService.getStoreBySysUserid(Toolsddw.getCurrentUserId());
            if(spo!=null){
               return this.goodFriendPlayTableService.deleteByID(id,spo.getId());
            }
        }catch (Exception e){
            logger.error("GoodFriendPlayTableController->doDelete",e);
        }
        return new ResponseVO(-1,"删除失败",null);

    }

}
