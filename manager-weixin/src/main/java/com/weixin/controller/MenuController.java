package com.weixin.controller;

import com.alibaba.fastjson.JSONObject;
import com.gen.common.services.CacheService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.core.pojo.Button;
import com.weixin.core.pojo.CommonButton;
import com.weixin.core.pojo.ComplexButton;
import com.weixin.core.pojo.ViewButton;
import com.weixin.entity.Menu;
import com.weixin.entity.Pubweixin;
import com.weixin.services.MenuService;
import com.weixin.services.PubWeixinService;
import com.weixin.services.WeixinInterfaceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信自定义菜单
 * Created by Jacky on 2017/12/4.
 */
@Controller
@RequestMapping(value = "weixin/menu")
public class MenuController {
    private static final Logger logger = Logger.getLogger("MenuController");
    @Autowired
    private MenuService menuService;
    @Autowired
    private PubWeixinService pubWeixinService;
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
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
        List<com.weixin.entity.Menu> menuPage =  menuService.findListAll(appid);
        model.addAttribute("menuPage",menuPage);
        model.addAttribute("appid",appid);
        model.addAttribute("pubweixinList",pubweixinList);
        return "pages/manager/weixin/menu";
    }

    @GetMapping("edit")
    public String edit(Integer id,Integer parent_id,String appid,Model model){
        try {
            if(id != null && id >0){
                model.addAttribute("menuObject",menuService.selectById(id));
            }else if(parent_id != null && parent_id != -1){
                Menu parent_menu = menuService.selectById(parent_id);
                Menu menu = new Menu();
                menu.setParent_id(parent_id);
                appid = parent_menu.getAppid();
                menu.setAppid(appid);
                model.addAttribute("menuObject",menu);
            }else if(parent_id == -1){
                Menu menu = new Menu();
                menu.setParent_id(parent_id);
            }
            model.addAttribute("appid",appid);
        }catch (Exception e){
            logger.error("MenuController->edit->系统异常",e);

        }
        return "pages/manager/weixin/menuEdit";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResponseVO save(Menu menu, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return menuService.saveOrUpdate(menu);
        } catch (Exception e) {
            logger.error("MenuController->save->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResponseVO delete(Integer id, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            return this.menuService.delete(id);
        } catch (Exception e) {
            logger.error("MenuController->delete->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
    @RequestMapping(value = "release")
    @ResponseBody
    public ResponseVO release(String appid, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 从数据库取出该公众号配置好的菜单,组成json调用微信接口生成菜单
            Page<Menu> pageMenu = menuService.findList(1,appid);
            List<Menu> menuList = pageMenu.getResult();
            JSONObject jsonObject = new JSONObject();
            com.weixin.core.pojo.Menu m = new com.weixin.core.pojo.Menu();
            //父级菜单
            List<Button>list = new ArrayList<Button>();
            for(Menu menu:menuList){
                if(menu.getParent_id()==-1){
                    List<Menu> submenuList= menuService.findListById(menu.getId());
                    if(submenuList.size()>0){
                        ComplexButton btn = new ComplexButton();
                        btn.setName(menu.getName());
                        //子菜单
                        List<Button>sublist = new ArrayList<Button>();
                        for(Menu submenu:submenuList){
                            if(submenu.getType().equals("click")){
                                CommonButton cb = new CommonButton();
                                cb.setName(submenu.getName());
                                cb.setType(submenu.getType());
                                cb.setKey(submenu.getKey());
                                sublist.add(cb);
                            }else if(submenu.getType().equals("view")){
                                ViewButton vbtn = new ViewButton();
                                vbtn.setName(submenu.getName());
                                vbtn.setType(submenu.getType());
                                vbtn.setUrl(submenu.getUrl());
                                sublist.add(vbtn);
                            }
                        }
                        btn.setSub_button(sublist.toArray(new Button[sublist.size()]));
                        list.add(btn);
                    }else{
                        if(menu.getType().equals("click")){
                            CommonButton btn = new CommonButton();
                            btn.setName(menu.getName());
                            btn.setType(menu.getType());
                            btn.setKey(menu.getKey());
                            list.add(btn);
                        }else if(menu.getType().equals("view")){
                            ViewButton vbtn = new ViewButton();
                            vbtn.setName(menu.getName());
                            vbtn.setType(menu.getType());
                            vbtn.setUrl(menu.getUrl());
                            list.add(vbtn);
                        }
                    }
                }
            }
            m.setButton(list.toArray(new Button[list.size()]));
            JSONObject js = weixinInterfaceService.createMenu(appid,m);
            //成功返回 {"errcode":0,"errmsg":"ok"}
            //失败返回 {"errcode":40055,"errmsg":"invalid button url domain hint: [lmkCQA0454vr21]"}
            if(js.containsKey("errcode") && js.getInteger("errcode") == 0){
                return new ResponseVO(1,"菜单发布成功",null);
            }else{
                String errmsg = "";
                if(js.containsKey("errmsg")) errmsg = js.getString("errmsg");
                return new ResponseVO(-1,"菜单发布失败"+errmsg,null);
            }
        } catch (Exception e) {
            logger.error("MenuController->release->系统异常",e);
            return new ResponseVO(-1,"菜单发布失败",null);
        }
    }
}
