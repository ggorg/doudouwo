package com.weixin.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.util.CustomMessage;
import com.weixin.dao.PushMapper;
import com.weixin.entity.Push;
import com.weixin.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 推送策略
 * @author Jacky
 *
 */
@Service
public class PushService {
    @Autowired
    private PushMapper pushMapper;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private WeixinUserService weixinUserService;

    /**
     * 根据appid查询推送策略
     * @param pageNum 页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum,String appid){
        Page<Push>page = new Page<Push>(pageNum,15);
        List<Push> list = pushMapper.findList(page,appid);
        int total = pushMapper.findListCount(appid);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 不分页查询推送策略
     * @param appid
     * @return
     */
    public List<Push> findList(String appid){ return pushMapper.findListAll(appid); }


    /**
     * 根据主键查询推送策略
     * @param id 主键
     * @return
     */
    public Push selectById(int id){
        return pushMapper.selectById(id);
    }

    /**
     * 插入或更新推送策略
     * @param push
     * @return
     */
    public ResponseVO saveOrUpdate(Push push){
        ResponseVO vo=new ResponseVO();
        Push pw = pushMapper.selectById(push.getId());
        if(pw!=null){
            pushMapper.update(push);
            vo.setReMsg("修改成功");
        }else{
            pushMapper.insert(push);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除推送策略
     * @param id 主键
     * @return
     */
    public ResponseVO delete(Integer id){
        ResponseVO vo=new ResponseVO();
        int res = pushMapper.delete(id);
        if(res>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }

    /**
     * 定时调用执行推送策略
     * @return
     */
    public void pushTask(){
        ResponseVO vo=new ResponseVO();
        List<Push>pushList = pushMapper.findPushList();
        for(Push push:pushList){
            int push_type = push.getPush_type();
            UserInfo userInfo = new UserInfo();
            userInfo.setAppid(push.getAppid());
            userInfo.setTagid_list(String.valueOf(push.getTag_id()));
            //推送文字
            if(push_type == 1){
                List<UserInfo>userInfoList =  weixinUserService.findUserAll(userInfo);
                List<String>openids = new ArrayList<String>();
                for(UserInfo ui : userInfoList){
                    openids.add(ui.getOpenid());
                }
                for (String openid:openids){
                    String content = CustomMessage.TextMsg(openid,push.getPush_content());
                    JSONObject json = weixinInterfaceService.sendMessage(push.getAppid(),content);
                }

                //推送图文
            }else if(push_type == 2){
                List<UserInfo>userInfoList =  weixinUserService.findUserAll(userInfo);
                List<String>openids = new ArrayList<String>();
                for(UserInfo ui : userInfoList){
                    openids.add(ui.getOpenid());
                }
                for (String openid:openids){
                    messageService.sendMessage(openid,push.getPush_messageid());
                }

                //推送模板消息
            }else if(push_type ==3){

            }
            push.setPush_state(1);
            pushMapper.update(push);
        }
    }
}
