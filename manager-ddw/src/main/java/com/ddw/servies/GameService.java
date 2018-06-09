package com.ddw.servies;

import com.gen.common.beans.GamePO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/16.
 */
@Service
@Transactional(readOnly = true)
public class GameService extends CommonService {

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
