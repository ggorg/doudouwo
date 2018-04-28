package com.ddw.controller;

import com.ddw.beans.IMCallBackDTO;
import com.ddw.beans.LiveRadioCallBackDTO;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class CallBackController {

    /**
     1	recv rtmp deleteStream	主播端主动断流
     2	recv rtmp closeStream	主播端主动断流
     3	recv() return 0	主播端主动断开 TCP 连接
     4	recv() return error	主播端 TCP 连接异常
     7	rtmp message large than 1M	收到流数据异常
     18	push url maybe invalid	推流鉴权失败，服务端禁止推流
     19	3rdparty auth failed	第三方鉴权失败，服务端禁止推流
     * @param liveRadioCallBackDTO
     * @return
     */
    @PostMapping("/live/execute")
    @ResponseBody
    public String liveExecute( @RequestBody LiveRadioCallBackDTO liveRadioCallBackDTO){
        System.out.println(liveRadioCallBackDTO);
        return "{ \"code\":0 }";
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
