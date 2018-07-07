package com.ddw.services;

import com.ddw.beans.vo.GameVO;
import com.gen.common.beans.GamePO;
import com.gen.common.beans.RankPO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
                List<Map> rankList = super.commonObjectsBySearchCondition("ddw_rank",searchCondition);
                List<RankPO> rankPOList = new ArrayList<>();
                for (Map rankMap:rankList) {
                    RankPO rankPO = new RankPO();
                    PropertyUtils.copyProperties(rankPO,rankMap);
                    rankPOList.add(rankPO);
                }
                gameVO.setRankList(rankPOList);
                gameVOList.add(gameVO);
            }
            CacheUtil.put("publicCache","gameList",gameVOList);
            return gameVOList;
        }else{
            return (List<GameVO>)CacheUtil.get("publicCache","gameList");
        }
    }
    public Page findList()throws Exception{
        return super.commonPage("ddw_game","createTime desc",1,999,new HashMap());
    }

    public GamePO selectById(String id){
        try {
            return super.commonObjectBySingleParam("ddw_game","id",id,GamePO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(GamePO gamePO)throws Exception{
        if(gamePO.getId() > 0 ){
            Map updatePoMap= BeanToMapUtil.beanToMap(gamePO);
            return super.commonUpdateBySingleSearchParam("ddw_game",updatePoMap,"id",gamePO.getId());
        }else{
            gamePO.setCreateTime(new Date());
            return super.commonInsert("ddw_game",gamePO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(String id){
        ResponseVO vo=new ResponseVO();
        int n = super.commonDelete("ddw_game","id",id);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }
}
