package com.ddw.services;

import com.ddw.beans.RankPO;
import com.ddw.beans.vo.GameVO;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacky on 2018/7/6.
 */
@Service
@Transactional(readOnly = true)
public class GameService extends CommonService{

    public List<GameVO> getGameList()throws Exception{
        Object obGameList = CacheUtil.get("publicCache","gameList");
        if(obGameList == null){
            List<GameVO> gameVOList= new ArrayList<GameVO>();
            List<Map> gameList = super.commonObjectsBySearchCondition("ddw_game",new HashMap<>());
            for (Map gameMap:gameList) {
                GameVO gameVO = new GameVO();
                PropertyUtils.copyProperties(gameVO,gameMap);
                Map searchCondition = new HashMap<>();
                searchCondition.put("gameId",gameMap.get("id"));
                List<Map> rankList = super.commonList("ddw_rank","sort",1,9999,searchCondition);
                List<RankPO> rankPOList = new ArrayList<>();
                for (Map rankMap:rankList) {
                    int star = Integer.valueOf(rankMap.get("star").toString());
                    for(int i=1;i<star+1;i++){
                        RankPO rankPO = new RankPO();
                        PropertyUtils.copyProperties(rankPO,rankMap);
                        rankPO.setRank(rankPO.getRank()+" "+ i+"星");
                        rankPO.setStar(i);
                        rankPOList.add(rankPO);
                    }
                }
                gameVO.setRankList(rankPOList);
                gameVOList.add(gameVO);
            }
            if(gameVOList.size()>0){
                CacheUtil.put("publicCache","gameList",gameVOList);
            }
            return gameVOList;
        }else{
            return (List<GameVO>)CacheUtil.get("publicCache","gameList");
        }
    }
}
