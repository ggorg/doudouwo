package com.ddw.servies;

import com.ddw.enums.RoleTypeEnum;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends CommonService{
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO modifyRoleType(String ridStr, String roleType)throws Exception {
        if(StringUtils.isBlank(ridStr)){
            return new ResponseVO(-2,"参数异常",null);

        }
        String ridS=MyEncryptUtil.getRealValue(ridStr);
        if(StringUtils.isBlank(ridS)){
            return new ResponseVO(-2,"参数异常",null);

        }
        Map setParams=new HashMap();
        setParams.put("rType",roleType);
        ResponseVO res=this.commonUpdateBySingleSearchParam("baserole",setParams,"id",Integer.parseInt(ridS));
        if(res.getReCode()==1){
            CacheUtil.deleteByStartWith("commonCache","roleByUserId-");
        }
        return res;

    }
    @Cacheable(value="commonCache",key = "'roleByUserId-'+#userid")
    public List getRoleByUserId(Integer userid,String roleType)throws Exception {
        Map condition=new HashMap();
        condition.put("uid",userid);
        Map childCondition=new HashMap();
        childCondition.put("rType", roleType);
        CommonSearchBean csb=new CommonSearchBean("baseuserrole",null,"ct0.*",null,null,condition,
                new CommonChildBean("baserole","id","rId",childCondition),
                new CommonChildBean("baseuser","id","uId",null));
        List list=this.getCommonMapper().selectObjects(csb);
        return list;
    }
}
