package com.ddw.services;

import com.ddw.beans.AppIndexVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.vo.*;
import com.ddw.dao.GoddessMapper;
import com.ddw.enums.LiveStatusEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.Distance;
import com.ddw.util.IMApiUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private GoddessMapper goddessMapper;

    @Autowired
    private BasePhotoService basePhotoService;


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
        if(obGoddess == null){
            //查询所有门店女神,直播优先,列表不包括当前查询会员id
            Page page=new Page<>(1,4);
            Integer userId=TokenUtil.getUserId(token);
            List<LiveRadioListVO> lists=this.goddessMapper.liveGoddess(page.getStartRow(),page.getEndRow(),null,userId);
            final List groupIds=new ArrayList();
            lists.forEach(a->{
                if(LiveStatusEnum.liveStatus1.getCode().equals(a.getLiveRadioFlag())){
                    groupIds.add(a.getGroupId());
                }
            });
            Map map= IMApiUtil.getMemberNum(groupIds);
            for(LiveRadioListVO o:lists){
                // o.setAge("20岁");
                if(LiveStatusEnum.liveStatus1.getCode().equals(o.getLiveRadioFlag())){
                    o.setViewingNum((Integer) map.get(o.getGroupId()));
                }else{
                    o.setViewingNum(0);
                }
                o.setBackImgUrl(basePhotoService.getPhotograph(o.getUserId()));
                o.setHeadImgUrl(o.getBackImgUrl());
            }
            appIndexVO.setGoddessList(lists);
            if(appIndexVO.getGoddessList().size()>0) {
                CacheUtil.put("publicCache", "appIndexGoddess", appIndexVO.getGoddessList());
            }
        }else{
            appIndexVO.setGoddessList((List<LiveRadioListVO>)CacheUtil.get("publicCache","appIndexGoddess"));
        }
        Object obPractice = CacheUtil.get("publicCache","appIndexPractice"+storeId);
        if(obPractice == null){
            appIndexVO.setPracticeList(reviewPracticeService.practiceList(token,1,4,1,null));
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
