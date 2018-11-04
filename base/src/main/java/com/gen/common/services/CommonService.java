package com.gen.common.services;

import com.gen.common.beans.*;
import com.gen.common.dao.CommonMapper;
import com.gen.common.exception.GenException;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        return commonList(tableName,ordername,null,pageNum,pageSize,searchCondition);
    }
    protected List commonList(String tableName,String ordername,String custom,Integer pageNum,Integer pageSize,Map<String,Object> searchCondition){
        Page page=null;
        if(pageNum!=null && pageSize!=null){
            page=new Page(pageNum,pageSize);
        }
        CommonSearchBean csb=new CommonSearchBean(tableName,ordername,custom, page==null?null:page.getStartRow(),page==null?null:page.getEndRow(),searchCondition);
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
    protected List commonObjectsBySearchCondition(String tableName,Map<String,Object> searchCondition)throws Exception{

        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,searchCondition));
        return list;
    }
    protected long commonCountBySearchCondition(String tableName,Map<String,Object> searchCondition){

        return this.commonMapper.selectCount( new CommonCountBean(tableName,searchCondition));
    }
    protected Map commonObjectBySearchCondition(String tableName,Map<String,Object> searchCondition){
        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,searchCondition));
        if(list!=null && list.size()>0){
           return list.get(0);
        }
        return null;
    }
    protected <T> T commonObjectBySearchCondition(String tableName,Map<String,Object> searchCondition,Class<T> clazz)throws Exception{
        Map map=commonObjectBySearchCondition(tableName,searchCondition);
        if(map!=null){
            T t=clazz.newInstance();
            PropertyUtils.copyProperties(t,map);
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
        return this.commonObjectBySearchCondition(tableName,condition);

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

    /**
     *  计算字段乐观锁更新
     * @param tableName 表名
     * @param setParams 修改的参数
     * @param searchCondition 查询的参数
     * @param versionName 版本字段名
     * @param calculatesName 要计算的字段名（加法计算）（比如金额字段）
     * @return
     */
    protected  ResponseVO commonCalculateOptimisticLockUpdateByParam(String tableName,Map setParams,Map searchCondition,String versionName,String[] calculatesName)throws Exception{
        Map map=null;
        Integer version=null;
        Integer num=null;
        Map vSetMap=null;
        Map vSearchMap=null;
        ResponseVO res=null;
        Integer cn=null;

        //setParams.remove(calculateName);
        for(int i=1;i<=5;i++){
            map=this.commonObjectBySearchCondition(tableName,searchCondition);
            if(map==null || map.isEmpty()){
                return new ResponseVO(-3,"更新失败",null);
            }else{
                version=(Integer)map.get(versionName);
                for(String calculateName:calculatesName){
                    cn=(Integer)setParams.get(calculateName);
                    if(cn==null)cn=0;
                    num=(Integer)map.get(calculateName);
                    num=(num==null?0:num)+cn;
                    if(num<0){
                        return new ResponseVO(-2,"更新失败，结果值出现小于0",null);
                    }
                    setParams.put(calculateName,num);
                }

                vSearchMap=new HashMap(searchCondition);
                if(version==null){
                    version=1;
                }else{
                    vSearchMap.put(versionName,version);
                }
                vSetMap =new HashMap(setParams);
                vSetMap.put(versionName,version+1);
                res=this.commonUpdateByParams(tableName,vSetMap,vSearchMap);
                if(res.getReCode()!=1){
                    if(i==5){
                        throw new GenException("更新失败");
                    }
                    Thread.sleep(i * 200);
                    continue;
                }else{

                    break;
                }

            }

        }
        return new ResponseVO(1,"更新成功",null);
    }
    /**
     *  普通乐观锁更新
     * @param tableName 表名
     * @param setParams 修改的字段集合
     * @param searchCondition 查询条件集合
     * @param versionName 版本号字段名称
     * @return
     * @throws Exception
     */
    protected ResponseVO commonOptimisticLockUpdateByParam(String tableName,Map setParams,Map searchCondition,String versionName)throws Exception{
        Map map=null;
        Integer version=null;
        Map vSetMap=null;
        Map vSearchMap=null;
        ResponseVO res=null;
        for(int i=1;i<=5;i++){
            map=this.commonObjectBySearchCondition(tableName,searchCondition);
            if(map==null || map.isEmpty()){
                return new ResponseVO(-2,"更新失败",null);
            }else{
                version=(Integer)map.get(versionName);
                vSearchMap=new HashMap(searchCondition);
               if(version==null){
                   version=1;
               }else{
                   vSearchMap.put(versionName,version);
               }
                vSetMap =new HashMap(setParams);
                vSetMap.put(versionName,version+1);
                res=this.commonUpdateByParams(tableName,vSetMap,vSearchMap);
                if(res.getReCode()!=1){
                    if(i==5){
                        throw new GenException("更新失败");
                    }
                    Thread.sleep(i * 200);
                    continue;
                }else{

                    break;
                }

            }

        }
        return new ResponseVO(1,"更新成功",null);

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
    protected Page commonPage(Integer pageNo,Integer pageSize,CommonSearchBean csb)throws Exception{
        Page page=new Page(pageNo,pageSize);
        csb.setStartNum(page.getStartRow());
        csb.setEndNum(page.getEndRow());
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
    protected long commonSumByBySingleSearchParam(String tableName,String sumParamName,String searchName,Object searchValue){
        Map sumCondition=new HashMap();
        sumCondition.put(searchName,searchValue);

        return commonSumByBySingleSearchMap(tableName,sumParamName,sumCondition);
    }
    protected long commonSumByBySingleSearchMap(String tableName,String sumParamName,Map searchMap){

        List list= this.commonMapper.selectObjects(new CommonSearchBean(tableName,null,"sum(t1."+sumParamName+") sumPrice",null,null,searchMap));
        if(list==null || list.isEmpty()){
            return 0;
        }
        Map map=(Map)list.get(0);
        if(map==null){
            return 0;
        }
        return ((BigDecimal)map.get("sumPrice")).longValue();
    }
    protected <T>T commonSingleFieldBySingleSearchParam(String tableName,String searchName,Object searchValue,String fieldName,Class<T> clazz){
        Map sumCondition=new HashMap();
        sumCondition.put(searchName,searchValue);
        List<Map> list=this.commonMapper.selectObjects(new CommonSearchBean(tableName,null,fieldName,null,null,sumCondition));
        if(list!=null && !list.isEmpty()){
            return (T)list.get(0).get(fieldName);
        }
        return null;
    }
    protected ResponseVO commonDeleteByParams(String tableName,Map<String,Object> searchCondition){
        ResponseVO vo=new ResponseVO();
        int n=this.commonMapper.deleteObject(new CommonDeleteBean(tableName,searchCondition));
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }

    protected ResponseVO commonDeleteByCombination(String tableName,Map<String,Object> searchCondition){
        ResponseVO vo=new ResponseVO();
        int n=this.commonMapper.deleteObjectCombination(new CommonDeleteBean(tableName,searchCondition));
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }
    protected CommonMapper getCommonMapper(){
        return this.commonMapper;
    }

}
