package com.ddw.services;

import com.ddw.beans.ListVO;
import com.ddw.beans.PageNoDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ActiveCenterService extends CommonService {

    public ResponseApiVO getList(String token, PageNoDTO dto)throws Exception{
        Integer storeId= TokenUtil.getStoreId(token);
        Map searchM=new HashMap();
        searchM.put("storeId",storeId);

        List list=this.commonList("ddw_activity","createTime desc","t1.dtTitle title,DATE_FORMAT(t1.activeTime,'%Y-%m-%d %H:%i:%S') activeTime,DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') createTime,t1.dtImgPath imgUrl,t1.dtTargetPath jumpUrl",dto.getPageNo()==null?1:dto.getPageNo(),10,searchM);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO(new ArrayList()));
        }
        return new ResponseApiVO(1,"成功",new ListVO(list));

    }
}
