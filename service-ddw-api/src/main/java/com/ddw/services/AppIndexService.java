package com.ddw.services;

import com.ddw.beans.AppIndexGoddessVO;
import com.ddw.beans.AppIndexPracticeVO;
import com.ddw.beans.AppIndexVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AppIndexService {
    private final Logger logger = Logger.getLogger(AppIndexService.class);
    @Autowired
    private TicketService ticketService;
    @Autowired
    private ReviewGoddessService reviewGoddessService;
    @Autowired
    private ReviewPracticeService reviewPracticeService;


    public ResponseApiVO toIndex(String token)throws Exception{
        List<Map> obj=(List)CacheUtil.get("stores","store");
        if(obj==null || obj.isEmpty()){
            return new ResponseApiVO(-2,"请先加载门店列表",null);
        }
        Integer storeId=TokenUtil.getStoreId(token);
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择门店",null);
        }
        AppIndexVO appIndexVO = new AppIndexVO();
        Object obGoddess = CacheUtil.get("publicCache","appIndexGoddess"+storeId);
        if(obGoddess == null){
            ResponseVO goddessList = reviewGoddessService.goddessNoAttentionList(token,1,4);
            appIndexVO.setGoddessList((List<AppIndexGoddessVO>)goddessList.getData());
            CacheUtil.put("publicCache","appIndexGoddess"+storeId,appIndexVO.getGoddessList());
        }else{
            appIndexVO.setGoddessList((List<AppIndexGoddessVO>)CacheUtil.get("publicCache","appIndexGoddess"+storeId));
        }
        Object obPractice = CacheUtil.get("publicCache","appIndexPractice"+storeId);
        if(obPractice == null){
            ResponseVO practiceList = reviewPracticeService.practiceNoAttentionList(token,1,4);
            appIndexVO.setPracticeList((List<AppIndexPracticeVO>)practiceList.getData());
            CacheUtil.put("publicCache","appIndexPractice"+storeId,appIndexVO.getPracticeList());
        }else{
            appIndexVO.setPracticeList((List<AppIndexPracticeVO>)CacheUtil.get("publicCache","appIndexPractice"+storeId));
        }
        appIndexVO.setTicketList(ticketService.getTicketList());
        return new ResponseApiVO(1,"成功",appIndexVO);

    }

}
