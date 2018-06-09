package com.gen.framework.services;

import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;


@Service
@Transactional(readOnly = true)
public class TimerTaskManagerService extends CommonService {
    public Page getTimerTaskPage(Integer pageNum)throws Exception{
        return this.commonPage("basetimetask","updateTime desc",pageNum,10,new HashMap());
    }
}
