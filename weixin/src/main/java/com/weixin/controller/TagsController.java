package com.weixin.controller;

import com.alibaba.fastjson.JSONObject;

import com.gen.common.services.CacheService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.entity.Pubweixin;
import com.weixin.entity.Tags;
import com.weixin.services.PubWeixinService;
import com.weixin.services.TagsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 微信用户标签管理
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/tags")
public class TagsController {
    private static final Logger logger = Logger.getLogger("TagsController");
    @Autowired
    private TagsService tagsService;
    @Autowired
    private PubWeixinService pubWeixinService;
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
        Tags tags = new Tags();
        tags.setAppid(appid);
        Page<Tags> tagsPage =  tagsService.findList(pageNo,tags);
        model.addAttribute("tagsPage",tagsPage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/tags";
    }

    @GetMapping("edit")
    public String edit(Integer id,String appid,Model model){
        try {
            Tags tags = new Tags();
            if(id != null && id >0){
                Tags t = new Tags();
                t.setAppid(appid);
                t.setId(id);
                tags = tagsService.selectById(t);
                appid = tags.getAppid();
            }else{
                tags.setId(-1);
            }
            model.addAttribute("tagsObject",tags);
            model.addAttribute("appid",appid);
        }catch (Exception e){
            logger.error("TagsController->edit->系统异常",e);

        }
        return "pages/manager/weixin/tagsEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Tags tags, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            if(tags.getId()== -1){
                return tagsService.save(tags);
            }else{
                return tagsService.update(tags);
            }
        } catch (Exception e) {
            logger.error("TagsController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Tags tags) {
        try {
            return this.tagsService.delete(tags);
        } catch (Exception e) {
            logger.error("TagsController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }

    /**
     * 更新微信用户标签
     * @param appid 应用id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="alltags", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getAllTags(String appid) {
        JSONObject result;
        if (appid != null) {
            result = new JSONObject();
            JSONObject  jsonObject = tagsService.getAllTags(appid);
            result = jsonObject;
        } else {
            result = new JSONObject();
            result.put("retCode", -1);
            result.put("retMsg", "参数有误");
        }
        return result.toString();
    }
}
