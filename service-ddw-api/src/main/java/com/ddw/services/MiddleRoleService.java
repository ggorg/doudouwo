package com.ddw.services;

import com.ddw.beans.MiddleRoleDTO;
import com.ddw.beans.MiddleRolePO;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 会员角色关系（用于app根据用户角色显示不同功能菜单）
 * Created by Jacky on 2018/4/16.
 */
@Service
public class MiddleRoleService extends CommonService {
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(MiddleRoleDTO middleRoleDTO)throws Exception{
        MiddleRolePO middleRolePO = new MiddleRolePO();
        PropertyUtils.copyProperties(middleRolePO,middleRoleDTO);
        middleRolePO.setCreateTime(new Date());
        middleRolePO.setUpdateTime(new Date());
        return this.commonInsert("ddw_middle_role",middleRolePO);
    }

    public Map query(String id)throws Exception {
        return this.commonObjectBySingleParam("ddw_middle_role", "id", id);
    }


}
