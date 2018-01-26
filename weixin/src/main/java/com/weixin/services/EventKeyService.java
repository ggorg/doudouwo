package com.weixin.services;


import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.dao.EventKeyMapper;
import com.weixin.entity.EventKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信事件处理-关键字回复Service
 * @author Jacky
 *
 */
@Service
public class EventKeyService {
    @Autowired
    private EventKeyMapper eventKeyMapper;

    /**
     * 根据appid查询关键字回复
     * @param pageNum 页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum, String appid){
        Page<EventKey>page = new Page<EventKey>(pageNum,15);
        List<EventKey> list = eventKeyMapper.findList(page,appid);
        int total = eventKeyMapper.findListCount(appid);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 不分页查询关键字回复
     * @param appid
     * @return
     */
    public List<EventKey> findList(String appid){ return eventKeyMapper.findListAll(appid); }


    /**
     * 根据主键查询关键字回复
     * @param id 主键
     * @return
     */
    public EventKey selectById(int id){
        return eventKeyMapper.selectById(id);
    }

    /**
     * 插入或更新关键字回复
     * @param eventRule
     * @return
     */
    public ResponseVO saveOrUpdate(EventKey eventRule){
        ResponseVO vo=new ResponseVO();
        EventKey pw = eventKeyMapper.selectById(eventRule.getId());
        if(pw!=null){
            eventKeyMapper.update(eventRule);
            vo.setReMsg("修改成功");
        }else{
            eventKeyMapper.insert(eventRule);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除关键字回复
     * @param id 主键
     * @return
     */
    public ResponseVO delete(Integer id){
        ResponseVO vo=new ResponseVO();
        int res = eventKeyMapper.delete(id);
        if(res>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }

}
