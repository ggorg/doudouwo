package com.ddw.services;

import com.ddw.enums.IncomeTypeEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class BaseConsumeRankingListService extends CommonService {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void save(Integer userId,Integer incomeUserId,Integer price,IncomeTypeEnum incomeType)throws Exception{
        Map searchMap=new HashMap();
        searchMap.put("consumeUserId",userId);
        searchMap.put("incomeUserId",incomeUserId);
        searchMap.put("type",incomeType.getCode());
        List list=this.commonObjectsBySearchCondition("ddw_consume_ranking_list",searchMap);
        if(list==null || list.isEmpty()){
            searchMap.put("consumePrice",price);
            searchMap.put("updateTime",new Date());
            searchMap.put("createTime",new Date());
            searchMap.put("version",1);
            ResponseVO res=this.commonInsertMap("ddw_consume_ranking_list",searchMap);
            if(res.getReCode()!=1){
                throw new GenException("保存排行榜失败");
            }
        }else{
            Map setParam=new HashMap();
            setParam.put("consumePrice",price);
            setParam.put("updateTime",new Date());
            ResponseVO res=this.commonCalculateOptimisticLockUpdateByParam("ddw_consume_ranking_list",setParam,searchMap,"version",new String[]{"consumePrice"});
            if(res.getReCode()!=1){
                throw new GenException("保存排行榜失败");
            }
            CacheUtil.delete("publicCacheTimer","ranking-list-"+incomeType.getCode()+"-"+incomeUserId);
            CacheUtil.delete("publicCacheTimer","ranking-list-"+incomeType.getCode()+"--1");

        }
    }

    @Async
    public List resetCacheRankingCList(Integer userId, IncomeTypeEnum incomeTypeEnum){

        Map searchMap=new HashMap();
        if(userId>0){
            searchMap.put("incomeUserId",userId);
        }
        searchMap.put("type",incomeTypeEnum.getCode());

        Page page=new Page(1,10);
/*        CommonSearchBean csb=new CommonSearchBean("ddw_consume_ranking_list","consumePrice desc","t1.consumePrice,ct0.nickName,ct0.headImgUrl",page.getStartRow(),page.getEndRow(),searchMap,
                new CommonChildBean("ddw_userinfo","id","consumeUserId",null)
        );*/
        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo","consumePrice desc","ct0.consumePrice,t1.nickName,t1.headImgUrl,ct1.level,ct1.gradeName ",page.getStartRow(),page.getEndRow(),null,
                new CommonChildBean("ddw_consume_ranking_list","consumeUserId","id",searchMap),
                new CommonChildBean("ddw_grade","id","gradeId",null)
        );
        List voList=this.getCommonMapper().selectObjects(csb);
        if(voList!=null){
            CacheUtil.put("publicCacheTimer","ranking-list-"+incomeTypeEnum.getCode()+"-"+userId,voList);
        }else{
            voList=new ArrayList();
        }
        return voList;
    }
}
