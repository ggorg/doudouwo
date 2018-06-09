package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
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
        ResponseVO goddessList = reviewGoddessService.goddessList(token,1,4);
        List<AppIndexGoddessVO> appIndexGoddessList = new ArrayList<AppIndexGoddessVO>();
        if (goddessList.getData() != null) {
            for(UserInfoVO userInfoVO:(List<UserInfoVO>)goddessList.getData()){
                AppIndexGoddessVO appIndexGoddessVO = new AppIndexGoddessVO();
                PropertyUtils.copyProperties(appIndexGoddessVO,userInfoVO);
                appIndexGoddessList.add(appIndexGoddessVO);
            }
        }
        ResponseVO practiceList = reviewPracticeService.practiceList(token,1,4);
        List<AppIndexDaiLianVO> appIndexDaiLianVOList = new ArrayList<AppIndexDaiLianVO>();
        if(practiceList.getData() != null){
            for(UserInfoVO userInfoVO:(List<UserInfoVO>)practiceList.getData()){
                AppIndexDaiLianVO appIndexDaiLianVO = new AppIndexDaiLianVO();
                PropertyUtils.copyProperties(appIndexDaiLianVO,userInfoVO);
                appIndexDaiLianVOList.add(appIndexDaiLianVO);
            }
        }
        appIndexVO.setGoddessList(appIndexGoddessList);
        appIndexVO.setDaiLianList(appIndexDaiLianVOList);
        Map map=new HashMap();
        map.put("ticketList",ticketService.getTicketList());
        return new ResponseApiVO(1,"成功",appIndexVO);

    }

}
