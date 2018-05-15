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
    @Autowired
    private ReviewRealNameService reviewRealNameService;
    @Autowired
    private ReviewGoddessService reviewGoddessService;
    @Autowired
    private ReviewPracticeService reviewPracticeService;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeLiveRadio(ReviewCallBackBean rb)throws Exception{
        return liveRadioService.createLiveRadioRoom(rb.getBusinessCode(),rb.getStoreId());

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeRealName(ReviewCallBackBean rb)throws Exception{
        return reviewRealNameService.updateReviewRealName(rb.getBusinessCode());

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executeGoddess(ReviewCallBackBean rb)throws Exception{
        return reviewGoddessService.updateReviewGoddess(rb.getBusinessCode());

    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO executePractice(ReviewCallBackBean rb)throws Exception{
        return reviewPracticeService.updateReviewPractice(rb.getBusinessCode());

    }
}
