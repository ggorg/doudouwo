package weixin.controller;


import com.gen.common.services.CacheService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import weixin.entity.Msg;
import weixin.entity.Pubweixin;
import weixin.services.MsgService;
import weixin.services.PubWeixinService;

import java.util.List;

/**
 * 微信消息查询接口
 */
@Controller
@RequestMapping(value = "weixin/msg")
public class WeixinMsgController {
    private static final Logger logger = Logger.getLogger("WeixinMsgController");
    @Autowired
    private MsgService msgService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = {"list", ""})
    public String list(Msg msg, BindingResult bindingResult, @RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin> pubweixinList = pubWeixinService.findPubweixinAll();
        if(appid == null){
            appid = (String) cacheService.get("appid");
            if(appid == null || appid.equals("")){
                if(pubweixinList.size()>0){
                    appid = pubweixinList.get(0).getAppid();
                }
            }
        }else{
            cacheService.set("appid",appid);
        }
        msg.setAppid(appid);
        Page<Msg> page = msgService.findList(pageNo,msg);
        model.addAttribute("msg", msg);
        model.addAttribute("msgPage", page);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/msg";
    }

    /**
     * 打开快捷回复
     * @param id
     * @param appid
     * @param model
     * @return
     */
    @GetMapping("reply")
    public String reply(String id,String appid,Model model) {
        try {
            Msg msg = msgService.selectById(id);
            model.addAttribute("id", id);
            model.addAttribute("appid", msg.getAppid());
        } catch (Exception e) {
            logger.error("WeixinMsgController->reply->系统异常", e);
        }
        return "pages/manager/weixin/msgReply";
    }

    @RequestMapping(value = "collect")
    @ResponseBody
    public ResponseVO collect(String id) {
        try {
            this.msgService.collect(id);
            return new ResponseVO(1,"成功",null);
        } catch (Exception e) {
            logger.error("WeixinMsgController->collect->系统异常",e);
            return new ResponseVO(-1,"失败",null);
        }
    }

    /**
     *
     * @param msg 回复内容
     * @param id 针对回复的消息id
     * @return
     */
    @RequestMapping(value = "sendMsg")
    @ResponseBody
    public ResponseVO sendMsg(Msg msg,String id) {
        try {
            // 更新回复状态
            Msg userMsg = msgService.selectById(id);
            userMsg.setId(id);
            userMsg.setReply("1");
            msgService.saveOrUpdate(userMsg);

            //官方回复信息入库
            msg.setId("");
            msg.setOpenid(userMsg.getOpenid());
            msg.setOfficial("1");
            msg.setMsgtype("1");
            msgService.saveOrUpdate(msg);
            //调用接口回复信息
            return msgService.sendMsg(msg);
        } catch (Exception e) {
            logger.error("WeixinMsgController->sendMsg->系统异常",e);
            return new ResponseVO(-1,"失败",null);
        }
    }

}
