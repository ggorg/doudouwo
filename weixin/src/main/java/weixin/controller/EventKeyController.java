package weixin.controller;


import com.gen.common.services.CacheService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weixin.entity.EventKey;
import weixin.entity.Pubweixin;
import weixin.services.EventKeyService;
import weixin.services.EventRuleService;
import weixin.services.PubWeixinService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 微信事件处理-回复规则
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/event/keyword")
public class EventKeyController {
    private static final Logger logger = Logger.getLogger("EventKeyController");
    @Autowired
    private EventKeyService eventKeyService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private EventRuleService eventRuleService;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
        List<Pubweixin>pubweixinList = pubWeixinService.findPubweixinAll();
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
        Page<EventKey> eventKeyPage =  eventKeyService.findList(pageNo,appid);
        model.addAttribute("eventKeyPage",eventKeyPage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/eventKey";
    }

    @GetMapping("edit")
    public String edit(Integer id,String appid,Model model){
        try {
            if(id != null && id >0){
                EventKey eventKey = eventKeyService.selectById(id);
                appid = eventKey.getAppid();
                model.addAttribute("eventKeyObject",eventKey);
            }
            model.addAttribute("appid",appid);
            model.addAttribute("eventRuleList",eventRuleService.findListAll(appid));
        }catch (Exception e){
            logger.error("EventKeyController->edit->系统异常",e);

        }
        return "pages/manager/weixin/eventKeyEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(EventKey eventKey, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return eventKeyService.saveOrUpdate(eventKey);
        } catch (Exception e) {
            logger.error("EventKeyController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.eventKeyService.delete(id);
        } catch (Exception e) {
            logger.error("EventKeyController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
}
