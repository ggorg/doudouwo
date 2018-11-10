package com.ddw.controller;

import com.ddw.beans.OldBringingNewDTO;
import com.ddw.services.OldBringingNewService;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(value = "老带新",description = "老带新",tags = {"老带新"})
@RestController
@RequestMapping("/ddwapp/oldBringingNew")
public class OldBringingNewController {
    private final Logger logger = Logger.getLogger(OldBringingNewController.class);

    @Autowired
    private OldBringingNewService oldBringingNewService;


    @ApiOperation(value = "添加老带新",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/add")
    @ResponseBody
    public ResponseVO add(@RequestBody @ApiParam(name="args",value="此接口用在微信网页授权事件获取到双方unionid后调用绑定",required=true)OldBringingNewDTO args){
        try {
            return this.oldBringingNewService.save(args);
        }catch (Exception e){
            logger.error("OldBringingNewController-add-》添加老带新-》系统异常",e);
        }
        return new ResponseVO(-1,"失败",null);

    }

}
