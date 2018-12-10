package com.ddw.servies;

import com.ddw.beans.GoddessDTO;
import com.ddw.beans.GoddessPO;
import com.ddw.beans.GoddessVO;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/29.
 */
@Service
@Transactional(readOnly = true)
public class GoddessService extends CommonService {
    public Page findPage(Integer pageNo,Integer storeId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("storeId",storeId);
        Map conditon=new HashMap();
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess","t1.createTime desc","t1.id,t1.userId,t1.storeId," +
                "t1.appointment,t1.tableNo,t1.bidPrice,t1.earnest,t1.createTime,t1.updateTime,ct0.userName,ct0.nickName,ct0.idcardFrontUrl ",null,null,searchCondition,new CommonChildBean("ddw_userinfo","id","userId",conditon));
        Page page = this.commonPage(pageNo,10,csb);
        return page;
    }

    public GoddessVO getById(Integer id)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("id",id);
        Map conditon=new HashMap();
        GoddessVO goddessVO = new GoddessVO();
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess",null,"t1.id,t1.userId,t1.storeId," +
                "t1.appointment,t1.tableNo,t1.bidPrice,t1.earnest,t1.createTime,t1.updateTime,ct0.userName,ct0.nickName,ct0.idcardFrontUrl ",0,1,searchCondition,new CommonChildBean("ddw_userinfo","id","userId",conditon));
        List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            PropertyUtils.copyProperties(goddessVO,list.get(0));
        }
        return goddessVO;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(GoddessDTO goddessDTO)throws Exception{
        if(goddessDTO.getId() > 0){
            GoddessVO oldGoddessVo=this.getById(goddessDTO.getId());
            Map updatePoMap= new HashMap<>();
            updatePoMap.put("earnest",goddessDTO.getEarnest());
            updatePoMap.put("bidPrice",goddessDTO.getBidPrice());
            updatePoMap.put("appointment",goddessDTO.getAppointment());
            updatePoMap.put("tableNo",goddessDTO.getTableNo());
            updatePoMap.put("updateTime",new Date());
            CacheUtil.delete("publicCache","goddess-"+oldGoddessVo.getStoreId()+"-"+oldGoddessVo.getUserId());
            return super.commonUpdateBySingleSearchParam("ddw_goddess",updatePoMap,"id",goddessDTO.getId());
        }else{
            GoddessPO goddessPO = new GoddessPO();
            goddessPO.setCreateTime(new Date());
            goddessPO.setUpdateTime(new Date());
            return this.commonInsert("ddw_goddess",goddessPO);
        }
    }
}
