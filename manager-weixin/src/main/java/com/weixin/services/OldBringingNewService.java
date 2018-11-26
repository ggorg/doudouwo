package com.weixin.services;


import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import com.weixin.dao.OldBringingNewMapper;
import com.weixin.entity.OldBringingNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 老带新Service
 * @author Jacky
 *
 */
@Service
public class OldBringingNewService extends CommonService{
    @Autowired
    private OldBringingNewMapper oldBringingNewMapper;

    /**
     * 插入老带新关系
     * @param oldOpenid
     * @param openid
     * @return
     */
    public ResponseVO save(String oldOpenid,String openid){
        ResponseVO vo=new ResponseVO();
        if(oldOpenid.equals(openid)){
            return new ResponseVO(-2,"自己不允许绑定自己",null);
        }
        int count = oldBringingNewMapper.findCount(openid);
        if(count > 0){
           return new ResponseVO(-1,"已被绑定,不允许重复绑定",null);
        }else{
            OldBringingNew oldBringingNew = new OldBringingNew();
            oldBringingNew.setOldOpenid(oldOpenid);
            oldBringingNew.setNewOpenid(openid);
            oldBringingNewMapper.insert(oldBringingNew);
            return new ResponseVO(1,"绑定成功",null);
        }
    }

    /**
     * 模糊查询openid
     * @param openid 截取的后六位的openid
     * @return
     */
    public String getOpenid(String openid){
        return oldBringingNewMapper.getOpenid(openid);
    }
}
