package com.weixin.controller;


import com.gen.common.config.MainGlobals;
import com.gen.common.services.CacheService;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.weixin.entity.Message;
import com.weixin.entity.Pubweixin;
import com.weixin.services.MessageService;
import com.weixin.services.PubWeixinService;
import com.weixin.services.WeixinInterfaceService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 图文设置
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/message")
public class MessageController {
    private static final Logger logger = Logger.getLogger("MessageController");
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = {"list", ""})
    public String list(@RequestParam(defaultValue = "1") Integer pageNo, String appid, Model model) {
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
        Page<Message> messagePage =  messageService.findList(pageNo,appid);
        model.addAttribute("messagePage",messagePage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/message";
    }

    @GetMapping("edit")
    public String edit(Integer id,Integer parent_id,String appid,Model model){
        try {
            if(id != null && id >0){
                model.addAttribute("messageObject",this.messageService.selectById(id));
            }else if(parent_id != null && parent_id != -1){
                Message parent_message = this.messageService.selectById(parent_id);
                Message message = new Message();
                message.setParent_id(parent_id);
                appid = parent_message.getAppid();
                message.setAppid(appid);
                model.addAttribute("messageObject",message);
            }else if(parent_id == -1){
                Message message = new Message();
                message.setParent_id(parent_id);
            }
            model.addAttribute("appid",appid);
        }catch (Exception e){
            logger.error("MessageController->edit->系统异常",e);
        }
        return "pages/manager/weixin/messageEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Message message) {
        try {
            return messageService.saveOrUpdate(message);
        } catch (Exception e) {
            logger.error("MessageController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id) {
        try {
            return this.messageService.delete(id);
        } catch (Exception e) {
            logger.error("MessageController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }

    //文件上传相关代码
    @RequestMapping(value = "upload")
    @ResponseBody
    public ResponseVO upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            new ResponseVO(-1,"文件为空",null);
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 时间戳文件名
        fileName = new Date().getTime() + suffixName;
        FileInfoVo flag= UploadFileMoveUtil.move(file,mainGlobals.getRsDir(), fileName);
        if(flag!=null){
            return new ResponseVO(1,"上传成功",fileName);
        }else{
            return new ResponseVO(-2,"上传失败",null);
        }
    }

}
