package com.gen.common.services;

import com.gen.common.beans.*;
import com.gen.common.dao.CommonMapper;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommonService {

    @Autowired
    private CommonMapper commonMapper;

    protected ResponseVO commonInsert(String tableName, Object bean){
        Map params= BeanToMapUtil.beanToMap(bean);
        return commonInsertMap(tableName,params);
    }
    protected ResponseVO commonInsertMap(String tableName, Map params){
        ResponseVO vo=new ResponseVO();
        CommonInsertBean cib=new CommonInsertBean(tableName,params);
        int n=this.commonMapper.insertObject(cib);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("创建成功");
            vo.setData(cib.getId());
        }else{
            vo.setReCode(-2);
            vo.setReMsg("创建失败");
        }

        return vo;
    }
    protected List commonList(String tableName,String ordername,Integer pageNum,Integer pageSize,Map<String,Object> searchCondition){
        Page page=null;
        if(pageNum!=null && pageSize!=null){
            page=new Page(pageNum,pageSize);
        }
        CommonSearchBean csb=new CommonSearchBean(tableName,ordername,null, page==null?null:page.getStartRow(),page==null?null:page.getEndRow(),searchCondition);
        return this.commonMapper.selectObjects(csb);
    }
    protected long commonCountBySingleParam(String tableName,String paramName,Object paramValue){
        Map<String,Object> searchCondition=new HashMap<>();
        searchCondition.put(paramName+",=",paramValue);
        return this.commonMapper.selectCount( new CommonCountBean(tableName,searchCondition));
    }
    protected List commonObjectsBySingleParam(String tableName,String paramName,Object paramValue)throws Exception{
        Map<String,Object> condition=new HashMap<>();
        condition.put(paramName+",=",paramValue);
        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,condition));
        return list;
    }
    protected long commonCountBySearchCondition(String tableName,Map<String,Object> searchCondition){

        return this.commonMapper.selectCount( new CommonCountBean(tableName,searchCondition));
    }

    protected <T> T commonObjectBySearchCondition(String tableName,Map<String,Object> searchCondition,Class<T> clazz)throws Exception{
        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,searchCondition));
        if(list!=null && list.size()>0){
            T t=clazz.newInstance();
            PropertyUtils.copyProperties(t,list.get(0));
            return t;

        }
        return null;
    }
    protected <T> T commonObjectBySingleParam(String tableName,String paramName,Object paramValue,Class<T> clazz)throws Exception{
        Map map=commonObjectBySingleParam(tableName, paramName, paramValue);

        if(map!=null){
            T t=clazz.newInstance();
            PropertyUtils.copyProperties(t,map);
            return t;

        }
        return null;
    }
    protected Map commonObjectBySingleParam(String tableName,String paramName,Object paramValue)throws Exception{
        Map<String,Object> condition=new HashMap<>();
        condition.put(paramName+",=",paramValue);
        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,condition));
        if(list!=null && !list.isEmpty()){

            return list.get(0);

        }
        return null;
    }
    protected ResponseVO commonUpdateBySingleSearchParam(String tableName, Map setParams, String searchParamName, Object searchParamValue){
        ResponseVO vo=new ResponseVO();
        Map searchCondition=new HashMap();
        searchCondition.put(searchParamName,searchParamValue);
        CommonUpdateBean cub=new CommonUpdateBean(tableName,setParams,searchCondition);
        int n=this.commonMapper.updateObject(cub);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("修改成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("修改失败");
        }
        return vo;
    }
    protected ResponseVO commonUpdateByParams(String tableName, Map setParams,Map searchCondition){
        ResponseVO vo=new ResponseVO();

        CommonUpdateBean cub=new CommonUpdateBean(tableName,setParams,searchCondition);
        int n=this.commonMapper.updateObject(cub);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("修改成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("修改失败");
        }
        return vo;
    }
    protected Page commonPage(String tableName, String ordername, Integer pageNum, Integer pageSize, Map<String,Object> searchCondition)throws Exception{
        Page page=new Page(pageNum,pageSize);

        CommonSearchBean csb=new CommonSearchBean(tableName,ordername,null, page.getStartRow(),page.getEndRow(),searchCondition);
        CommonCountBean ccb = new CommonCountBean();

        PropertyUtils.copyProperties(ccb, csb);
        long count = commonMapper.selectCount(ccb);
        if(count>0){
            List list=this.commonMapper.selectObjects(csb);
            page.setResult(list);
            page.setTotal(count);
        }

        return page;
    }
    protected int commonDelete(String tableName,String paramName,Object paramValue){
        Map searchCondition=new HashMap();
        searchCondition.put(paramName,paramValue);
        return this.commonMapper.deleteObject(new CommonDeleteBean(tableName,searchCondition));
    }
    protected CommonMapper getCommonMapper(){
        return this.commonMapper;
    }
}
