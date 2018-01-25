package weixin.services;

import com.gen.framework.common.beans.CommonChildBean;
import com.gen.framework.common.beans.CommonCountBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.dao.CommonMapper;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.Page;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TyUserService extends CommonService {

    @Autowired
    private CommonMapper commonMapper;


    @Autowired
    private CacheManager cacheManager;

    public Page getUserPage(Integer pageNum,String tuTelphone) throws Exception {
        Page page=new Page(pageNum,10);
        Map condition=new HashMap();
        if(StringUtils.isNotBlank(tuTelphone)){
            condition.put("tuTelphone",tuTelphone);
        }
        CommonSearchBean csb=new CommonSearchBean("ty_user","createTime desc","t1.*,ct0.nickname,ct0.headimgurl",page.getStartRow(),page.getEndRow(),condition,new CommonChildBean("weixin_user","openid","tuOpenId",null));

        CommonCountBean ccb=new CommonCountBean();
        PropertyUtils.copyProperties(ccb,csb);
        long n= this.commonMapper.selectCount(ccb);
        if(n>0){
            page.setTotal(n);
            page.setResult(this.commonMapper.selectObjects(csb));
        }


        return page;
    }
    public List getAllUser(){
        return this.commonList("ty_user",null,null,null,null);
    }
}
