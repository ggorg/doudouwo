package com.weixin.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.common.util.CommonUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.config.WXGlobals;
import com.weixin.core.beans.message.resp.Article;
import com.weixin.core.beans.message.resp.NewsMessage;
import com.weixin.dao.MessageMapper;
import com.weixin.entity.Message;
import com.weixin.util.CustomMessage;
import com.weixin.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 图文消息
 */
@Service
public class MessageService{
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    @Autowired
    private MessageMapper messageMapper;
	@Autowired
	private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private WXGlobals WXGlobals;
    
    /**
     * 添加图文消息
     * @param message
     * @return
     */
    @Transactional(readOnly = false)
	public ResponseVO saveOrUpdate(Message message){
		ResponseVO vo=new ResponseVO();
    	int ret = 0;
    	Message m = messageMapper.selectById(message.getId());
    	if(m!=null){
    		ret = messageMapper.update(message);
			vo.setReMsg("修改成功");
		}else{
			ret = messageMapper.insert(message);
			vo.setReMsg("创建成功");
		}
		vo.setReCode(1);
    	return vo;
    }
    /**
     * 删除图文消息
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO delete(Integer id){
		ResponseVO vo=new ResponseVO();
        List<Message> messageList = messageMapper.findListById(id);
        if (messageList.size() > 0) {
            vo.setReCode(-1);
            vo.setReMsg("包含子图文,删除失败");
        }else {
            int res = messageMapper.delete(id);
            if (res > 0) {
                vo.setReCode(1);
                vo.setReMsg("删除成功");
            } else {
                vo.setReCode(-2);
                vo.setReMsg("删除失败");
            }
        }
    	return vo;
    }
    
    /**
     * 分页查询图文消息列表
     * @param pageNum 当前页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum,String appid){
		Page<Message> page = new Page<Message>(pageNum,10);
		List<Message> list = messageMapper.findList(page,appid);
        list = this.handleMessage(list);
		int total = messageMapper.findListCount(appid);
		page.setResult(list);
		page.setTotal(total);
		return page;
	}

    /**
     * 查询所有图文
     * @param appid
     * @return
     */
    public List<Message> findListAll(String appid){
        return messageMapper.findListAll(appid);
    }

    /**
     * 图文结构整理
     * @param messageList
     * @return
     */
    public ArrayList handleMessage(List<Message> messageList){
        CopyOnWriteArrayList<Message> linkeMessageList=new CopyOnWriteArrayList(messageList);
        ArrayList<Message> topMessage = new ArrayList<Message>();
        for(Message message:linkeMessageList){
            if(message.getParent_id() == -1){
                topMessage.add(message);
                linkeMessageList.remove(message);
                for(Message childMessage:linkeMessageList){
                    if(childMessage.getParent_id() == message.getId()){
                        topMessage.add(childMessage);
                        linkeMessageList.remove(message);
                    }
                }
            }
        }
        return topMessage;
    }

    /**
     * 根据id返回图文消息
     * @param id 主键id
     * @return
     */
    public Message selectById(Integer id){ return messageMapper.selectById(id); }

    /**
     * 发送图文信息
     * @param openid 用户openid
     * @param id
     */
    public ResponseVO sendMessage(String openid,Integer id){
        Message message = messageMapper.selectById(id);
        List<Message>messageList = messageMapper.findListById(id);
        List<Article> list = new ArrayList<Article>();
        messageList.add(0,message);
        for(Message m:messageList){
            Article article = new Article();
            article.setTitle(m.getTitle());
            article.setDescription(m.getDescription());
            article.setUrl(m.getUrl());
            article.setPicUrl(m.getPicurl());
            list.add(article);
        }
        String content = CustomMessage.NewsMsg(openid,list);
        weixinInterfaceService.sendMessage(message.getAppid(),content);
        return new ResponseVO(1,"成功",null);
    }

    /**
     * 返回xml格式发送图文的方法
     * @param fromUserName 用户openid
     * @param toUserName 应用appid
     * @param id 图文id
     * @return
     */
    public String sendArticle(String fromUserName, String toUserName,Integer id) {
        String respMessage = null;
        // 创建图文消息
        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
        newsMessage.setFuncFlag(0);
        Message message = messageMapper.selectById(id);
        List<Message>messageList = messageMapper.findListById(id);
        List<Article> articleList = new ArrayList<Article>();
        messageList.add(0,message);
        for(Message m:messageList){
            Article article = new Article();
            article.setTitle(m.getTitle());
            article.setDescription(m.getDescription());
            article.setUrl(m.getUrl());
            article.setPicUrl(m.getPicurl());
            articleList.add(article);
        }
        newsMessage.setArticleCount(articleList.size());
        newsMessage.setArticles(articleList);
        respMessage = MessageUtil.newsMessageToXml(newsMessage);
        return respMessage;
    }

    /**
     * 批量推送图文接口
     * @param map key<openid>:value<appid>
     * @param title 图文标题
     * @param description 图文描述
     * @param picurl 图片url
     * @param page state参数
     * @param param state参数
     * @return
     */
    public ResponseVO sendMessage(Map<String, String> map, String title, String description, String picurl, String page, String param){
        try {
            for (String key : map.keySet()) {
                String appid = map.get(key);
                JSONObject state = new JSONObject();
                state.put("appid",appid);
                state.put("page",page);
                state.put("param",param);
                StringBuffer oauthUrl = new StringBuffer();
                oauthUrl = oauthUrl.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(appid);
                oauthUrl = oauthUrl.append("&redirect_uri=").append(URLEncoder.encode(WXGlobals.getRedirectUri(),"utf-8"));
                oauthUrl = oauthUrl.append("&response_type=code&scope=snsapi_userinfo&state=");
                oauthUrl = oauthUrl.append(CommonUtil.base32Encode(state.toString())).append("#wechat_redirect");

                List<Article> list = new ArrayList<Article>();
                Article article = new Article();
                article.setTitle(title);
                article.setDescription(description);
                article.setUrl(oauthUrl.toString());
                article.setPicUrl(picurl);
                list.add(article);
                String content = CustomMessage.NewsMsg(key,list);
                weixinInterfaceService.sendMessage(appid,content);
            }
            return new ResponseVO(1,"成功",null);
        } catch (Exception e) {
            logger.error("MessageService->sendMessage->系统异常",e);
            return new ResponseVO(-1,"失败",null);
        }
    }

}
