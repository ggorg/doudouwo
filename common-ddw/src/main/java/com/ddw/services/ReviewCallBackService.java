package com.ddw.services;

import com.ddw.beans.ReviewCallBackBean;
import com.gen.common.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批回调类
 */
@Service
public class ReviewCallBackService {

    @Autowired
    private LiveRadioService liveRadioService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeLiveRadio(ReviewCallBackBean rb)throws Exception{
        return liveRadioService.createLiveRadioRoom(rb.getBusinessCode(),rb.getStoreId());

    }
}
