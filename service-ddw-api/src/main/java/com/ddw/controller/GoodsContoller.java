package com.ddw.controller;

import com.ddw.beans.BiddingDTO;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.ListVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.GoodsTypeEnum;
import com.ddw.services.GoodsClientService;
import com.ddw.token.Token;
import com.gen.common.dict.DictionaryUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/goods")
@Api(description="商品",tags = "商品")
public class GoodsContoller {
    private final Logger logger = Logger.getLogger(GoodsContoller.class);

    @Autowired
    private GoodsClientService goodsClientService;

    @Token
    @ApiOperation(value = "商品列表")
    @PostMapping("/index/{token}")
    public ResponseApiVO<ListVO> toGoodsIndex(@PathVariable String token){
        try {
            return this.goodsClientService.index(token);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsIndex-商列表-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }
    @Token
    @ApiOperation(value = "商品详情")
    @PostMapping("/info/{token}")
    public ResponseApiVO<ListVO> toGoodsInfo(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)CodeDTO args){
        try {
            return this.goodsClientService.getInfo(token,args);
        }catch (Exception e){
            logger.error("GoodsContoller->toGoodsInfo-商品详情-》异常",e);
        }
        return new ResponseApiVO(-1,"失败",null);
    }
}
