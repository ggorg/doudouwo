package com.ddw.servies;

import com.ddw.beans.GoodsEditDTO;
import com.ddw.beans.GoodsTypeDTO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.GoodsStatusEnum;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class StoreGoodsService extends CommonService {

    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;

    @Autowired
    private DDWGlobals ddwGlobals;

    public Page findPage(Integer pageNo,Integer status,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("storeId",storeId);
        if(status!=null && StringUtils.isNotBlank(GoodsStatusEnum.getName(status))){
            condtion.put("dgStatus",status);
        }
        return this.commonPage("ddw_goods","dgSort asc,updateTime desc",pageNo,10,condtion);

    }
    public List getGoodsProduct(Integer goodsId)throws Exception{
        Map search=new HashMap();
        search.put("dghGoodsId",goodsId);
        search.put("disabled",0);
        return this.commonObjectsBySearchCondition("ddw_goods_product",search);
    }
    public Map getGoods(String idStr)throws Exception{
        if(StringUtils.isNotBlank(idStr)){
            Integer id=Integer.parseInt(MyEncryptUtil.getRealValue(idStr));
            Map map= this.commonObjectBySingleParam("ddw_goods","id",id);
            if(map!=null){
                map.put("child",getGoodsProduct(id));
            }
            return map;
        }
        Map map=new HashMap();
        map.put("child",new ArrayList());
        return map;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveGoods(GoodsEditDTO dto, Integer storeId)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(dto.getDgTitle())){
            return new ResponseVO(-2,"商品名称是空",null);

        }
        if(dto.getDghName()==null){
            return new ResponseVO(-2,"货品名称是空",null);

        }
        if(dto.getDghCost()==null){
            return new ResponseVO(-2,"货品成本价格是空",null);

        }
        if(dto.getDghSalesPrice()==null){
            return new ResponseVO(-2,"货品销售价格是空",null);

        }
        Map goodsMap=new HashMap();
        goodsMap.put("dgDetail",dto.getDgDetail());
        goodsMap.put("dgSort",dto.getDgSort());
        goodsMap.put("dgTitle",dto.getDgTitle());
        goodsMap.put("dgType",dto.getDgType());
        goodsMap.put("updateTime",new Date());
        goodsMap.put("storeId",storeId);
        if(!dto.getFileImgShow().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( dto.getFileImgShow().getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( dto.getFileImgShow(),mainGlobals.getRsDir(), dmImgName);
            goodsMap.put("fileImgShow",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath());
            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);
            File icoFile=new File(fileInfoVo.getFileSrc().replace(".","-ico."));
            Thumbnails.of(fileInfoVo.getFileSrc()).scale(0.5f).outputQuality(0.5f).toFile(icoFile);

            goodsMap.put("fileImgIcoPath",ddwGlobals.getCallBackHost()+fileInfoVo.getUrlPath().replace(".","-ico."));

            CommonBeanFiles icoF=new CommonBeanFiles();
            PropertyUtils.copyProperties(icoF,f);
            icoF.setLocalPath(f.getLocalPath().replace(".","-ico."));
            icoF.setFileName(f.getFileName().replace(".","-ico."));
            icoF.setUrlPath(f.getUrlPath().replace(".","-ico."));
            this.fileService.saveFile(icoF);
        }

        Integer goodsId=null;
        if(dto.getId()==null){
            goodsMap.put("createTime",new Date());
            ResponseVO<Integer> inVo=this.commonInsertMap("ddw_goods",goodsMap);
            if(inVo.getReCode()!=1){
                return new ResponseVO(-2,"保存失败",null);
            }
            goodsId=inVo.getData();
        }else{
            this.commonUpdateBySingleSearchParam("ddw_goods",goodsMap,"id",dto.getId());
            goodsId=dto.getId();

        }

        List list=this.commonObjectsBySingleParam("ddw_goods_product","dghGoodsId",goodsId);
        if(list==null || list.isEmpty()){
            for(int i=0;i<dto.getDghName().length;i++){
                edit(dto,goodsId,i,storeId,true);

            }
        }else {
            CopyOnWriteArrayList<Map> cw=new CopyOnWriteArrayList(list);
            boolean  flag;
            for (int i = 0; i < dto.getDghName().length; i++) {
                flag=true;
                for(Map m:cw){
                    if(((Integer)m.get("id")).equals(dto.getDghId()[i])){
                        edit(dto,goodsId,i,storeId,false);
                        flag=false;
                        cw.remove(m);
                       break;
                    }
                }
                if(flag){
                    edit(dto,goodsId,i,storeId,true);
                }

            }
            if(cw!=null && !cw.isEmpty()){
                Map searchMap=null;
                for(Map m:cw){
                    searchMap=new HashMap();
                    m.put("disabled",1);
                    m.put("updateTime",new Date());
                    searchMap.put("id",m.get("id"));
                    this.commonOptimisticLockUpdateByParam("ddw_goods_product",m,searchMap,"dghVersion");
                }
                //this.commonDeleteByCombination("ddw_goods_product")
            }
        }

        return new ResponseVO(1,"保存成功",null);


    }
    private void edit(GoodsEditDTO dto,Integer goodsId,int i,Integer storeId,boolean isInsert)throws Exception{
        Map gPruductMap=new HashMap();
        gPruductMap.put("dghName",dto.getDghName()[i]);
        gPruductMap.put("dghCost",dto.getDghCost()[i]);
        gPruductMap.put("dghSalesPrice",dto.getDghSalesPrice()[i]);
        gPruductMap.put("dghActivityPrice",dto.getDghActivityPrice()[i]);
        gPruductMap.put("dghFormulaId",dto.getDghFormulaId()[i]);
        gPruductMap.put("dghStatus",dto.getDghStatus()[i]);
        gPruductMap.put("dghGoodsId",goodsId);
        gPruductMap.put("updateTime",new Date());
        gPruductMap.put("storeId",storeId);


        if(isInsert){
            gPruductMap.put("createTime",new Date());
            this.commonInsertMap("ddw_goods_product",gPruductMap);

        }else{
            Map searchMap=new HashMap();
            searchMap.put("id",dto.getDghId()[i]);
            searchMap.put("storeId",storeId);
            this.commonOptimisticLockUpdateByParam("ddw_goods_product",gPruductMap,searchMap,"dghVersion");
        }

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateStatus(String idStr,Integer status){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(GoodsStatusEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);

        }
        Integer id=Integer.parseInt(ids);
        Map map= new HashMap();
        map.put("dgStatus",status);
        ResponseVO ret=this.commonUpdateBySingleSearchParam("ddw_goods",map,"id",id);
        if(ret.getReCode()==1){
            if(GoodsStatusEnum.goodsStatus1.getCode().equals(status)){
                return new ResponseVO(1,"发布成功",null);
            }else if(GoodsStatusEnum.goodsStatus0.getCode().equals(status)){
                return new ResponseVO(1,"撤回发布成功",null);
            }else if(GoodsStatusEnum.goodsStatus2.getCode().equals(status)){
                return new ResponseVO(1,"下架成功",null);
            }

        }
        return new ResponseVO(-2,"操作失败",null);
    }

}
