package com.weixin.services;

import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.dao.EventRuleMapper;
import com.weixin.entity.EventRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 微信事件处理-回复规则Service
 * @author Jacky
 *
 */
@Service
public class EventRuleService {
    @Autowired
    private EventRuleMapper eventRuleMapper;

    /**
     * 根据appid查询回复规则
     * @param pageNum 页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum,String appid){
        Page<EventRule>page = new Page<EventRule>(pageNum,15);
        List<EventRule> list = eventRuleMapper.findList(page,appid);
        int total = eventRuleMapper.findListCount(appid);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 不分页查询回复规则
     * @param appid
     * @return
     */
    public List<EventRule> findListAll(String appid){
        return eventRuleMapper.findListAll(appid);
    }

    /**
     * 不分页查询回复规则,查询事件类型为,1 关注时自动回复（唯一）,2 消息自动回复(唯一）的list
     * @param appid
     * @return
     */
    public List<EventRule> findList(String appid){ return eventRuleMapper.findListAll(appid); }


    /**
     * 根据主键查询回复规则
     * @param id 主键
     * @return
     */
    public EventRule selectById(int id){
        return eventRuleMapper.selectById(id);
    }

    /**
     * 查询事件类型,1 关注时自动回复（唯一）
     * @param appid
     * @return
     */
    public EventRule getSubscribe(String appid){
        return eventRuleMapper.getSubscribe(appid);
    }

    /**
     * 查询事件类型2 消息自动回复(唯一）
     * @param appid
     * @return
     */
    public EventRule getAutoreply(String appid){
        return eventRuleMapper.getAutoreply(appid);
    }

    /**
     * 插入或更新回复规则
     * @param eventRule
     * @return
     */
    public ResponseVO saveOrUpdate(EventRule eventRule){
        ResponseVO vo=new ResponseVO();
        EventRule pw = eventRuleMapper.selectById(eventRule.getId());
        if(pw!=null){
            eventRuleMapper.update(eventRule);
            vo.setReMsg("修改成功");
        }else{
            eventRuleMapper.insert(eventRule);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除回复规则
     * @param id 主键
     * @return
     */
    public ResponseVO delete(Integer id){
        ResponseVO vo=new ResponseVO();
        int res = eventRuleMapper.delete(id);
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
