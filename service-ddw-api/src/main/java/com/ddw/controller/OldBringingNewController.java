package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.ddw.util.CommonUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "老带新",description = "老带新",tags = {"老带新"})
@RestController
@RequestMapping("/ddwapp/oldBringingNew")
public class OldBringingNewController {
    private final Logger logger = Logger.getLogger(OldBringingNewController.class);
    private final String WXAPPID = "wx77fedfebe36bb337";
    private final String OAUTHMAINURL = "http://doudouwo.cn/weixin/oauth";
//
//    @Autowired
//    private OldBringingNewService oldBringingNewService;
//

//    @ApiOperation(value = "添加老带新",produces = MediaType.APPLICATION_JSON_VALUE)
//    @GetMapping("/add")
//    @ResponseBody
//    public ResponseVO add(@RequestBody @ApiParam(name="args",value="此接口用在微信网页授权事件获取到双方unionid后调用绑定",required=true)OldBringingNewDTO args){
//        try {
//            return this.oldBringingNewService.save(args);
//        }catch (Exception e){
//            logger.error("OldBringingNewController-add-》添加老带新-》系统异常",e);
//        }
//        return new ResponseVO(-1,"失败",null);
//
//    }

    @Token
    @ApiOperation(value = "老带新分享链接")
    @PostMapping("/shareUrl/{token}")
    public ResponseVO shareUrl(@PathVariable String token){
        try {
            JSONObject json = new JSONObject();
            String openid = TokenUtil.getUserObject(token).toString();
            String shareUrl = CommonUtil.createOauthUrl(WXAPPID,OAUTHMAINURL,"obn",openid.substring(openid.length()-6,openid.length()));
            json.put("shareUrl",shareUrl);
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("OldBringingNewController->shareUrl",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

}
