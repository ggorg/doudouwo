package com.gen.common.services;

import com.gen.common.beans.CommonMessageBean;
import com.gen.common.enums.MessageStatusEnum;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class MessageService extends CommonService  {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveMsg(CommonMessageBean cmb){
        return this.commonInsert("base_message",cmb);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateStatus(Integer id, MessageStatusEnum statusEnum){
        Map map=new HashMap();
        map.put("status",statusEnum.getCode());
        return this.commonUpdateBySingleSearchParam("base_message",map,"id",id);
    }
}
