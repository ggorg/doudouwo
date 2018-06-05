package com.ddw.controller;

import com.ddw.beans.ListVO;
import com.ddw.beans.PageNoDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.TicketVO;
import com.ddw.services.GiftService;
import com.ddw.services.TicketService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/ticket")
@Api(description="门票",tags = "门票")
public class TicketController {
    private final Logger logger = Logger.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @PostMapping("/list/{token}")
    @ApiOperation(value = "列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO<ListVO<TicketVO>> list(@PathVariable String token){

       try{
           return this.ticketService.getAllTicket();
       }catch (Exception e){
           logger.error("TicketController->list-》门票列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
       }

    }

}
