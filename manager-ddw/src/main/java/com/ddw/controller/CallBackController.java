package com.ddw.controller;

import com.ddw.beans.IMCallBackDTO;
import com.ddw.beans.LiveRadioCallBackDTO;
import com.ddw.services.LiveRadioService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class CallBackController {
    private final Logger logger = Logger.getLogger(CallBackController.class);


    @Autowired
    private LiveRadioService liveRadioService;
    /**
     1	recv rtmp deleteStream	主播端主动断流
     2	recv rtmp closeStream	主播端主动断流
     3	recv() return 0	主播端主动断开 TCP 连接
     4	recv() return error	主播端 TCP 连接异常
     7	rtmp message large than 1M	收到流数据异常
     18	push url maybe invalid	推流鉴权失败，服务端禁止推流
     19	3rdparty auth failed	第三方鉴权失败，服务端禁止推流
     * @param dto
     * @return
             */
    @PostMapping("/live/execute")
    @ResponseBody
    public String liveExecute( @RequestBody LiveRadioCallBackDTO dto){
        try {
            System.out.println(dto);
            ResponseVO responseVO=this.liveRadioService.handleLiveRadioStatus(dto.getStream_id(),dto.getEvent_type());
            if(responseVO.getReCode()==1){
                return "{ \"code\":0 }";
            }
        }catch (Exception e){
            logger.error("liveExecute",e);
        }
        return "{ \"code\":-1 }";
    }

    @PostMapping("/im/execute")
    @ResponseBody
    public String imExecute(@RequestParam IMCallBackDTO dto, @RequestBody HttpEntity data){
        System.out.println(dto);
        System.out.println(data);
        return "{" +
                "    \"ActionStatus\": \"OK\"," +
                "    \"ErrorInfo\": \"\"," +
                "    \"ErrorCode\": 0 " +
                "}";
    }
}
