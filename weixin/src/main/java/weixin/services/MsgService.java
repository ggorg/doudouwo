package weixin.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.dao.MsgMapper;
import com.ty.entity.Msg;
import com.ty.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 消息管理
 * 
 * @author Jacky
 * 
 */
@Service
public class MsgService {

	@Autowired
	private WeixinInterfaceService weixinInterfaceService;
	@Autowired
	private MsgMapper msgMapper;

	/** 消息入库 */
	public int save(Msg msg) {
		return msgMapper.save(msg);
	}

    /**
     * 根据appid查询关键字回复
     * @param pageNum 页数
     * @param msg 消息实体类
     * @return
     */
    public Page findList(Integer pageNum, Msg msg){
        Page<Msg>page = new Page<Msg>(pageNum,15);
        List<Msg> list = msgMapper.findList(page,msg);
        int total = msgMapper.findListCount(msg);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 根据id返回回复bean
     * @param id
     * @return
     */
    public Msg selectById(String id){
        return msgMapper.selectById(id);
    }
    /**
     * 将公众号回复的信息入库
     * @param appid
     * @param openid
     * @param content
     */
    public void saveMsg(String appid,String openid, String content){
    	Msg msg = new Msg();
    	msg.setAppid(appid);
    	msg.setOpenid(openid);
    	msg.setContent(content);
    	msg.setOfficial("1");
    	msg.setMsgtype("1");
    	//回复信息成功则入库
		msgMapper.save(msg);
    }

    /**
     * 添加或删除回复消息
     * @param msg
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseVO saveOrUpdate(Msg msg){
        ResponseVO vo=new ResponseVO();
        int ret = 0;
        Msg m = msgMapper.selectById(msg.getId());
        if(m!=null){
            ret = msgMapper.update(msg);
            vo.setReMsg("修改成功");
        }else{
            ret = msgMapper.save(msg);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 收藏与取消收藏
     * @param id
     */
    public void collect(String id){
        Msg msg = msgMapper.selectById(id);
        if(msg.getCollect().equals("0")){
            msg.setCollect("1");
        }else{
            msg.setCollect("0");
        }
        msgMapper.update(msg);
    }

    /**
     * 删除五天前状态为未收藏消息
     * @return
     */
    public int deleteUnCollect(){
    	return msgMapper.deleteUnCollect();
    }

    /**
     * 快捷回复
     * @param msg
     * @return
     */
    public ResponseVO sendMsg(Msg msg){
        String content = CustomMessage.TextMsg(msg.getOpenid(),msg.getContent());
        JSONObject json = weixinInterfaceService.sendMessage(msg.getAppid(),content);
        if(json.containsKey("errcode") && json.getInteger("errcode") == 0){
            return new ResponseVO(1,"成功",null);
        }else {
            return new ResponseVO(-1,json.getString("errmsg"),null);
        }
    }
}
