package com.weixin.services;

import com.alibaba.fastjson.JSONObject;

import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.weixin.core.pojo.CommonButton;
import com.weixin.core.pojo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.weixin.core.pojo.Button;
import com.weixin.dao.MenuMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 微信菜单Service
 * @author Jacky
 *
 */
@Service
public class MenuService {
    @Autowired
    private WeixinInterfaceService weixinInterfaceService;
    @Autowired
    private MenuMapper menuMapper;


	/**
	 * 普通
	 * @param appid 应用ID
	 * @param name 菜单名称
	 * @param type click：点击推事件
	 * @param key
	 * @return
	 */
	public JSONObject commonButton(String appid, String name, String type, String key){
		JSONObject jsonObject = new JSONObject();
		CommonButton btn = new CommonButton();
		btn.setName(name);
		btn.setType(type);
		btn.setKey(key);
		Menu menu = new Menu();
		menu.setButton(new Button[] { btn });
		jsonObject = weixinInterfaceService.createMenu(appid, menu);
		return jsonObject;
	}
    /**
	 * 获取自定义菜单配置接口
	 * @param appid 应用id
	 * @return
	 */
	public JSONObject getCurrentSelfMenu(String appid){
		return weixinInterfaceService.getCurrentSelfMenu(appid);
	}

    /**
     * 根据appid查询自定义菜单
     * @param pageNum 页数
     * @param appid 应用id
     * @return
     */
    public Page findList(Integer pageNum, String appid){
        Page<com.weixin.entity.Menu> page = new Page<com.weixin.entity.Menu>(pageNum,100);
        List<com.weixin.entity.Menu> list = menuMapper.findList(page,appid);
        int total = menuMapper.findListCount(appid);
        page.setResult(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 根据应用id查询菜单
     * @param appid 应用id
     * @return
     */
    public ArrayList findListAll(String appid){
        Page<com.weixin.entity.Menu>page = new Page<com.weixin.entity.Menu>(1,100);
        List<com.weixin.entity.Menu> list = menuMapper.findList(page,appid);
        return handleMenu(list);
    }

    /**
     * 菜单结构整理
     * @param menuList
     * @return
     */
    public ArrayList handleMenu(List<com.weixin.entity.Menu> menuList){
        CopyOnWriteArrayList<com.weixin.entity.Menu> linkeMenuList=new CopyOnWriteArrayList(menuList);
        ArrayList<com.weixin.entity.Menu> topMenus = new ArrayList<com.weixin.entity.Menu>();
        for(com.weixin.entity.Menu menu:linkeMenuList){
            if(menu.getParent_id() == -1){
                topMenus.add(menu);
                linkeMenuList.remove(menu);
                for(com.weixin.entity.Menu childMenu:linkeMenuList){
                    if(childMenu.getParent_id() == menu.getId()){
                        topMenus.add(childMenu);
                        linkeMenuList.remove(menu);
                    }
                }
            }
        }
        return topMenus;
    }

    /**
     * 根据主键id查询子菜单
     * @param id 菜单主键id
     * @return
     */
    public List<com.weixin.entity.Menu> findListById(Integer id){
        return menuMapper.findListById(id);
    }

    /**
     * 根据菜单主键查询菜单配置
     * @param id 主键
     * @return
     */
    public com.weixin.entity.Menu selectById(int id){
        return menuMapper.selectById(id);
    }

    /**
     * 插入或更新菜单
     * @param menu
     * @return
     */
    public ResponseVO saveOrUpdate(com.weixin.entity.Menu menu){
        ResponseVO vo=new ResponseVO();
        com.weixin.entity.Menu pw = menuMapper.selectById(menu.getId());
        if(pw!=null){
            menuMapper.update(menu);
            vo.setReMsg("修改成功");
        }else{
            menuMapper.insert(menu);
            vo.setReMsg("创建成功");
        }
        vo.setReCode(1);
        return vo;
    }

    /**
     * 删除菜单
     * @param id 主键
     * @return
     */
    public ResponseVO delete(Integer id){
        ResponseVO vo=new ResponseVO();
        List<com.weixin.entity.Menu> menuList = menuMapper.findListById(id);
        if (menuList.size() > 0) {
            vo.setReCode(-1);
            vo.setReMsg("包含子菜单,删除失败");
        }else{
            int res = menuMapper.delete(id);
            if(res>0){
                vo.setReCode(1);
                vo.setReMsg("删除成功");
            }else{
                vo.setReCode(-2);
                vo.setReMsg("删除失败");
            }
        }
        return vo;
    }

}
