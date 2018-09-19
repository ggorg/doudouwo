package com.ddw.services;

import com.ddw.beans.AppIndexVO;
import com.ddw.beans.PageDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.vo.AppIndexBannerVO;
import com.ddw.beans.vo.AppIndexButtonVO;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.beans.vo.AppIndexPracticeVO;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
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
    @Autowired
    private BannerService bannerService;
    @Autowired
    private ButtonService buttonService;


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
        Object obGoddess = CacheUtil.get("publicCache","appIndexGoddess");
        PageDTO page = new PageDTO();
        page.setPageNum(1);
        page.setPageSize(4);
        if(obGoddess == null){
            //查询所有门店女神,直播优先,列表不包括当前查询会员id
            appIndexVO.setGoddessList(reviewGoddessService.goddessList(TokenUtil.getUserId(token),page,null));
            if(appIndexVO.getGoddessList().size()>0) {
                CacheUtil.put("publicCache", "appIndexGoddess", appIndexVO.getGoddessList());
            }
        }else{
            appIndexVO.setGoddessList((List<AppIndexGoddessVO>)CacheUtil.get("publicCache","appIndexGoddess"));
        }
        Object obPractice = CacheUtil.get("publicCache","appIndexPractice"+storeId);
        if(obPractice == null){
            appIndexVO.setPracticeList(reviewPracticeService.practiceList(token,page,1,null));
            if(appIndexVO.getPracticeList().size()>0){
                CacheUtil.put("publicCache","appIndexPractice"+storeId,appIndexVO.getPracticeList());
            }
        }else{
            appIndexVO.setPracticeList((List<AppIndexPracticeVO>)CacheUtil.get("publicCache","appIndexPractice"+storeId));
        }
        Object obBanner = CacheUtil.get("publicCache","appIndexBanner"+storeId);
        if(obBanner == null){
            List<AppIndexBannerVO> appIndexBannerList = bannerService.getBannerList(storeId);
            appIndexVO.setBannerList(appIndexBannerList);
            CacheUtil.put("publicCache","appIndexBanner"+storeId,appIndexBannerList);
        }else{
            appIndexVO.setBannerList((List<AppIndexBannerVO>)CacheUtil.get("publicCache","appIndexBanner"+storeId));
        }
        Object obButton = CacheUtil.get("publicCache","appIndexButton");
        if(obButton == null){
            List<AppIndexButtonVO> appIndexButtonList = buttonService.getButtonList();
            appIndexVO.setButtonList(appIndexButtonList);
            CacheUtil.put("publicCache","appIndexButton"+storeId,appIndexButtonList);
        }else{
            appIndexVO.setButtonList((List<AppIndexButtonVO>)CacheUtil.get("publicCache","appIndexButton"));
        }
        appIndexVO.setTicketList(ticketService.getTicketList());
        return new ResponseApiVO(1,"成功",appIndexVO);

    }

}
