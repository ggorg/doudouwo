package com.ddw.services;

import com.ddw.beans.GoddessDTO;
import com.ddw.beans.GoddessPO;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 女神
 * Created by Jacky on 2018/4/16.
 */
@Service
public class GoddessService extends CommonService {
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(GoddessDTO goddessDTO)throws Exception{
        GoddessPO goddessPO = new GoddessPO();
        PropertyUtils.copyProperties(goddessPO,goddessDTO);
        goddessPO.setCreateTime(new Date());
        goddessPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_goddess",goddessPO);
    }

    public GoddessPO query(int userId)throws Exception {
        return  this.commonObjectBySingleParam("ddw_goddess","userId",userId,new GoddessPO().getClass());
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(GoddessDTO goddessDTO)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("tableNo",goddessDTO.getTableNo());
        setParams.put("createTime",new Date());
        GoddessPO goddessPO = new GoddessPO();
        PropertyUtils.copyProperties(goddessPO,goddessDTO);
        goddessPO.setCreateTime(new Date());
        return this.commonUpdateBySingleSearchParam("ddw_goddess",setParams,"userId",goddessDTO.getUserId());
    }
}
