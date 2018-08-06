package com.ddw.servies;

import com.ddw.beans.GoddessDTO;
import com.ddw.beans.GoddessPO;
import com.ddw.beans.GoddessVO;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
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
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess","createTime desc","t1.id,t1.userId,t1.storeId," +
                "t1.appointment,t1.tableNo,t1.bidPrice,t1.earnest,t1.createTime,t1.updateTime,ct0.userName,ct0.nickName,ct0.idcardFrontUrl ",0,99999,searchCondition,new CommonChildBean("ddw_userinfo","id","userId",conditon));
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
        GoddessPO goddessPO = new GoddessPO();
        if(goddessDTO.getId() > 0){
            GoddessVO oldGoddessVo=this.getById(goddessDTO.getId());
            PropertyUtils.copyProperties(goddessPO,oldGoddessVo);
            PropertyUtils.copyProperties(goddessPO,goddessDTO);
            goddessPO.setUpdateTime(new Date());
            Map updatePoMap= BeanToMapUtil.beanToMap(goddessPO);

            CacheUtil.delete("publicCache","goddess-"+oldGoddessVo.getStoreId()+"-"+oldGoddessVo.getUserId());

            return super.commonUpdateBySingleSearchParam("ddw_goddess",updatePoMap,"id",goddessPO.getId());
        }else{
            goddessPO.setCreateTime(new Date());
            goddessPO.setUpdateTime(new Date());
            return this.commonInsert("ddw_goddess",goddessPO);
        }
    }
}
