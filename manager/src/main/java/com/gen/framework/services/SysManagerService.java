package com.gen.framework.services;


import com.gen.common.dao.CommonMapper;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import com.gen.framework.beans.*;
import com.gen.framework.dao.MenuPowerMapper;
import com.gen.framework.util.MenuMapComparator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SysManagerService extends CommonService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private MenuPowerMapper menuPowerMapper;

    @Autowired
    private CacheManager cacheManager;
    public Page getUserPage(Integer pageNum, String uName)throws Exception{
        Map condition=new HashMap();
        if(StringUtils.isNotBlank(uName)){
            uName=uName.replace("%","");
            condition.put("uName,like",uName+"%");
        }

        return this.commonPage("baseUser","createTime desc",pageNum,10,condition);
    }
    public List getAllUser(){
        return this.commonList("baseUser","createTime desc",null,null,new HashMap<>());
    }
    public SysUserBean getUserById(Integer uid)throws Exception{
        Map map=this.commonObjectBySingleParam("baseUser","id",uid);
        SysUserBean userBean=new SysUserBean();
        if(map!=null){

            userBean.setId((Integer) map.get("id"));
            userBean.setuName((String)map.get("uName"));
            userBean.setuNickName((String)map.get("uNickName"));
            Integer disabled=(Integer)map.get("disabled");
            userBean.setDisabled(disabled==null || disabled==1?false:true);
            userBean.setuPassword((String)map.get("uPassword"));
        }

        return userBean;
        //return this.commonObjectBySingleParam("baseUser","id",uid,SysUserBean.class);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO disabledUser(SysUserBean sysUserBean)throws  Exception{

        Map setParams=new HashMap();
        setParams.put("disabled",sysUserBean.getDisabled());
       return this.commonUpdateBySingleSearchParam("baseUser",setParams,"id",sysUserBean.getId());

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO modifyPwd(Integer uid, String oldPwd , String newPwd, String makeSurePwd)throws Exception{
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(oldPwd)){
            vo.setReCode(-2);
            vo.setReMsg("密码为空");
            return vo;
        }
        if(StringUtils.isBlank(newPwd)){
            vo.setReCode(-2);
            vo.setReMsg("新密码为空");
            return vo;
        }
        if(!newPwd.equals(makeSurePwd)){
            vo.setReCode(-2);
            vo.setReMsg("请确保新密码和确认密码一致");
            return vo;
        }
        SysUserBean userBean=this.getUserById(uid);
        if(userBean.getId()!=null && StringUtils.isNotBlank(userBean.getuPassword())){
            if(!userBean.getuPassword().equals(oldPwd)){
                vo.setReCode(-2);
                vo.setReMsg("密码验证失败");
                return vo;
            }
            Map params=new HashMap();
            params.put("uPassword",newPwd);
            return this.commonUpdateBySingleSearchParam("baseUser",params,"id",uid);
        }
        vo.setReCode(-2);
        vo.setReMsg("修改失败");
        return vo;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveUser(SysUserBean sysUserBean)throws  Exception{
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysUserBean.getuName())){
            vo.setReCode(-2);
            vo.setReMsg("用户名为空");
            return vo;
        }
        /*if(!Tools.isNumberAndEnglishStr(sysUserBean.getuName())){
            vo.setReCode(-2);
            vo.setReMsg("用户名必须为数字");
            return vo;
        }*/
        if(sysUserBean.getId()==null && StringUtils.isBlank(sysUserBean.getuPassword())){
            vo.setReCode(-2);
            vo.setReMsg("密码为空");
            return vo;
        }
        Map params= BeanToMapUtil.beanToMap(sysUserBean);
        params.put("updateTime",new Date());
        Map map= this.commonObjectBySingleParam("baseUser","uName",sysUserBean.getuName());
        if(sysUserBean.getId()!=null && sysUserBean.getId()>0){

           if(map!=null && (Integer)map.get("id")!=sysUserBean.getId()){
               vo.setReCode(-2);
               vo.setReMsg("用户已经存在，请换一个");
               return vo;
           }
            params.remove("disabled");
            return this.commonUpdateBySingleSearchParam("baseUser",params,"id",sysUserBean.getId());
        }else{
            params.put("createTime",new Date());

            params.put("disabled",false);
           if(map!=null){
               vo.setReCode(-2);
               vo.setReMsg("用户已经存在，请换一个");
               return vo;
           }
            return this.commonInsertMap("baseUser",params);
        }
        }





    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveRole(SysRoleBean sysRoleBean, Integer[] uIds){
        String roleTable="baseRole";
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysRoleBean.getrName())){
            vo.setReCode(-2);
            vo.setReMsg("角色名为空");
            return vo;
        }
        long count=this.commonCountBySingleParam(roleTable,"rName",sysRoleBean.getrName());
        if(sysRoleBean.getId()==null && count>0){
            vo.setReCode(-2);
            vo.setReMsg("角色名已经重复，请重新换一个");
            return vo;
        }
        Map roleMap= BeanToMapUtil.beanToMap(sysRoleBean);
        roleMap.put("updateTime",new Date());
        if(sysRoleBean.getId()!=null && sysRoleBean.getId()>0){

            vo=this.commonUpdateBySingleSearchParam(roleTable,roleMap,"id",sysRoleBean.getId());
            vo.setData(sysRoleBean.getId());
            this.commonDelete("baseUserRole","rId",sysRoleBean.getId());
            vo.setReMsg("修改角色成功");
        }else{
            roleMap.put("createTime",new Date());
            vo=this.commonInsertMap(roleTable,roleMap);
            vo.setReMsg("创建角色成功");
        }

        if(vo.getReCode()==1){
            Map userRole=new HashMap();
            if(uIds!=null){
                SysUserRoleBean ur=null;
                for(Integer uid:uIds){
                    ur=new SysUserRoleBean();
                    ur.setrId((Integer) vo.getData());
                    ur.setuId(uid);
                    this.commonInsert("baseUserRole",ur);
                }
            }

        }
        vo.setReCode(1);

        return vo;
    }

    public Map getRoleById(Integer rId)throws Exception{
        Map data=new HashMap();
        data.put("roleObject",this.commonObjectBySingleParam("baseRole","id",rId,SysRoleBean.class));
        List<Map> list=getAllUser();
        List<Map> urList=this.commonObjectsBySingleParam("baseUserRole","rId",rId);
        Integer userId=null;
        Integer ur_uid=null;
        if(list!=null && !list.isEmpty()){
            for(Map map:list){
                if(map.containsKey("id")){
                    userId=(Integer) map.get("id");
                    for(Map ur:urList){
                        ur_uid=(Integer) ur.get("uId");
                        if(ur_uid==userId){
                            map.put("isThisRole",true);
                        }
                    }
                }
            }
        }
        data.put("userList",list);
        return data;

    }
    public Page getRolePage(Integer pageNum)throws Exception{

        return this.commonPage("baseRole","createTime desc",pageNum,10,new HashMap<String,Object>());
    }
    public SysMenuBean getMenuById(Integer mId)throws Exception{
       return  this.commonObjectBySingleParam("baseMenu","id",mId,SysMenuBean.class);
    }
    public Page getMenuPage(Integer pageNum)throws Exception{

        return this.commonPage("baseMenu","mSort asc",pageNum,10,new HashMap<String,Object>());
    }
    public TreeSet getMenuPage()throws Exception{

        return handlePower(null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveMenu(SysMenuBean sysMenuBean){
        ResponseVO vo=new ResponseVO();
        try {
            if(StringUtils.isBlank(sysMenuBean.getmName())){
                vo.setReCode(-2);
                vo.setReMsg("菜单名为空");
                return vo;
            }
            if(StringUtils.isBlank(sysMenuBean.getmUrl())){
                vo.setReCode(-2);
                vo.setReMsg("菜单地址为空");
                return vo;
            }
            if(sysMenuBean.getmParentId()==null || sysMenuBean.getmParentId()==0){
                sysMenuBean.setmParentId(-1);
            }else if(sysMenuBean.getmParentId()!=-1){
                long num=this.commonCountBySingleParam("baseMenu","id",sysMenuBean.getmParentId());
                if(num==0){
                    vo.setReCode(-2);
                    vo.setReMsg("上级菜单ID【"+sysMenuBean.getmParentId()+"】不存在");
                    return vo;
                }
            }
            if(sysMenuBean.getmSort()==null || sysMenuBean.getmSort()==0){
                sysMenuBean.setmSort(1);
            }
            Map params= BeanToMapUtil.beanToMap(sysMenuBean);
            params.put("updateTime",new Date());
            if(sysMenuBean.getId()!=null && sysMenuBean.getId()>0){
                vo=this.commonUpdateBySingleSearchParam("baseMenu",params,"id",sysMenuBean.getId());;

            }else{
                params.put("createTime",new Date());
                vo=this.commonInsertMap("baseMenu",params);
            }
            return vo;
        }finally {
            if(vo.getReCode()==1){
                Cache cache=cacheManager.getCache("allMenu");
                cache.put("'all'",getAllMenu());
            }
        }





    }
    @Cacheable(value="allMenu",key="'all'")
    public List getAllMenu(){
        return this.commonList("baseMenu","mSort asc",null,null,new HashMap<>());
    }
    public SysRoleMenuBean getPower(Integer rId)throws Exception{
        return this.commonObjectBySingleParam("baseRoleMenus","rId",rId,SysRoleMenuBean.class);

    }
    public TreeSet handlePower(Integer rId)throws Exception{
        List menuList=this.commonList("baseMenu","mSort asc",null,null,new HashMap<>());
        CopyOnWriteArrayList<Map> linkeMenuList=new CopyOnWriteArrayList(menuList);
        TreeSet<Map> topMenus=new TreeSet(new MenuMapComparator());
        Integer mParentId=null;
        Integer mId=null;
        Integer childMid=null;
        List srm=null;
        if(rId!=null){
            srm=this.commonObjectsBySingleParam("baseRoleMenus","rId",rId);
        }



        for(Map m:linkeMenuList){
            if(m.containsKey("mParentId")){
                mParentId=(Integer)m.get("mParentId");
                if(mParentId==-1){
                    mId =(Integer)m.get("id");
                    if(srm!=null)m.put("isPower",this.isPower(mId,srm));
                    topMenus.add(m);
                    linkeMenuList.remove(m);
                    for(Map child:linkeMenuList){
                        if(child.containsKey("mParentId") && m.containsKey("id")){
                            mParentId =(Integer)child.get("mParentId");
                            childMid=(Integer)child.get("id");
                            if(mParentId==mId){
                                if(srm!=null) child.put("isPower",this.isPower(childMid,srm));
                                m.put("isChild",true);
                                topMenus.add(child);
                                linkeMenuList.remove(child);
                            }

                        }
                    }
                }
            }
        }

        return topMenus;
    }
    private boolean isPower(Integer mId,List<Map> roleMenu){
        if(roleMenu!=null && !roleMenu.isEmpty()){
            Integer roleMenu_mid=null;
            for(Map map:roleMenu){
                if(map.containsKey("mId")){
                    roleMenu_mid=(Integer) map.get("mId");
                    if(roleMenu_mid==mId){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO savePower(Integer rId, Integer[] mIds)throws Exception{
        ResponseVO vo=new ResponseVO();

        if(rId==null || rId <= 0){
            vo.setReCode(-2);
            vo.setReMsg("请选择一个角色");
            return vo;
        }
        if(mIds==null){
            vo.setReCode(-2);
            vo.setReMsg("请选择授权菜单");
            return vo;
        }
        long count=this.commonCountBySingleParam("baseRole","id",rId);
        if(count==0){
            vo.setReCode(-2);
            vo.setReMsg("角色不存在");
            return vo;
        }
        SysRoleMenuBean srm=null;
        this.commonDelete("baseRoleMenus","rId",rId);
        for(Integer mid:mIds){
            srm=new SysRoleMenuBean();
            srm.setrId(rId);
            srm.setmId(mid);
            this.commonInsert("baseRoleMenus",srm);
        }

        vo.setReCode(1);
        vo.setReMsg("授权成功");
        return vo;
    }
    public ResponseVO login(SysUserBean sysUserBean)throws Exception{
        ResponseVO vo=new ResponseVO();
        if(StringUtils.isBlank(sysUserBean.getuName())){
            vo.setReCode(-2);
            vo.setReMsg("用户名为空");
            return vo;
        }
        if(StringUtils.isBlank(sysUserBean.getuPassword())){
            vo.setReCode(-2);
            vo.setReMsg("密码为空");
            return vo;
        }
        Map searchMap=new HashMap();
        searchMap.put("uName",sysUserBean.getuName());
        searchMap.put("uPassword",sysUserBean.getuPassword());
        //searchMap.put("disabled",false);

        List list=this.commonList("baseUser",null,null,null,searchMap);
        if(list!=null && !list.isEmpty()){
            Map user=(Map) list.get(0);
            if(user.containsKey("disabled")){
               Integer disabled=(Integer) user.get("disabled");
               if(disabled==1){
                   vo.setReCode(-2);
                   vo.setReMsg("用户已经禁用，请与管理员联系");
                   return vo;
               }
            }
            vo.setReCode(1);
            vo.setReMsg("登录成功");
            vo.setData(user);
            return vo;
        }
        vo.setReCode(-2);
        vo.setReMsg("用户名或密码错误");
        return vo;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deleteRole(String ridStr)throws Exception{
        String ridValue= MyEncryptUtil.getRealValue(ridStr);
        if(StringUtils.isBlank(ridValue)){
            return new ResponseVO(-2,"删除失败",null);
        }
        Integer rid=Integer.valueOf(ridValue);
        int n=this.commonDelete("baseRole","id",rid);
        if(n>0){
            this.resetPowerByRid(rid);
            this.commonDelete("baseUserRole","rId",rid);
            this.commonDelete("baseRoleMenus","rId",rid);
        }else{
            return new ResponseVO(-2,"删除失败",null);
        }
        return new ResponseVO(1,"删除成功",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deleteMenu(String midStr)throws Exception{
        String midValue= MyEncryptUtil.getRealValue(midStr);
        if(StringUtils.isBlank(midValue)){
            return new ResponseVO(-2,"删除失败",null);
        }
        Integer mid=Integer.valueOf(midValue);
        int n=this.commonDelete("baseMenu","id",mid);
        if(n>0){
            this.resetPowerByMid(mid);
            this.commonDelete("baseRoleMenus","mId",mid);
            List<Map> parentList=this.commonObjectsBySingleParam("baseMenu","mParentId",mid);
            if(parentList!=null && parentList.size()>0){
                for(Map pmap:parentList){
                    mid=(Integer)pmap.get("id");
                    n=this.commonDelete("baseMenu","id",mid);
                    if(n>0){
                        this.resetPowerByMid(mid);
                        this.commonDelete("baseRoleMenus","mId",mid);
                    }
                }
            }
            Cache cache=cacheManager.getCache("allMenu");
            cache.put("'all'",getAllMenu());
        }else{
            return new ResponseVO(-2,"删除失败",null);
        }
        return new ResponseVO(1,"删除成功",null);
    }

    @Cacheable(value = "commonCache",key = "#uid")
    public List getPowerMenu(Integer uid){
        //Cache cache=cacheManager.getCache("commonCache");

        return this.menuPowerMapper.queryById(uid);
    }
    /*@Cacheable(value = "commonCache",key = "#uid")
    public void clearCacheMenuByUid(Integer uid){
        this.cacheManager.
    }*/
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void resetPowerByRid(Integer rid)throws Exception{
        List<Map> lur=this.commonObjectsBySingleParam("baseUserRole","rid",rid);
        if(lur!=null && lur.size()>0){

            Cache cache=cacheManager.getCache("commonCache");
            Integer uid;

            for(Map map:lur){
                uid=(Integer)map.get("uId");
                if(cache.get(uid)!=null){
                    cache.put(uid,getPowerMenu(uid));
                }

            }

        }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void resetPowerByMid(Integer mid)throws Exception{
        List<Integer> uidList=this.menuPowerMapper.queryUidByMid(mid);
        if(uidList!=null && uidList.size()>0){
            Cache cache=cacheManager.getCache("commonCache");
            for(Integer uid:uidList){
                if(cache.get(uid)!=null){
                    cache.put(uid,getPowerMenu(uid));
                }

            }
        }
    }
}
