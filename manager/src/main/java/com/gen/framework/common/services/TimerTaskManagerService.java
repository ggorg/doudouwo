package com.gen.framework.common.services;

import com.gen.framework.common.util.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TimerTaskManagerService extends CommonService {
    public Page getTimerTaskPage(Integer pageNum)throws Exception{
        return this.commonPage("basetimetask","updateTime desc",pageNum,10,new HashMap<>());
    }
}
