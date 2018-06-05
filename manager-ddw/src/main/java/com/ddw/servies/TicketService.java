package com.ddw.servies;

import com.ddw.beans.GiftDTO;
import com.ddw.beans.GiftPO;
import com.ddw.beans.TicketDTO;
import com.ddw.beans.TicketPO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.DisabledEnum;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.*;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketService extends CommonService {

    @Autowired
    private FileService fileService;

    @Autowired
    private DDWGlobals ddwGlobals;

    @Autowired
    private MainGlobals mainGlobals;

    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_ticket","updateTime desc",pageNo,10,null);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_ticket","id",id);
    }
    public TicketPO getBeanById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_ticket","id",id,TicketPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String idStr,Integer status){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        Map map=new HashMap();
        map.put("dtDisabled",status);
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_ticket",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){
            CacheUtil.delete("publicCache","allTicket");

            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"启用成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(TicketDTO dto)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDtName())){
            return new ResponseVO(-2,"请填写文票名称",null);

        }
        if(dto.getDtPrice()==null ||dto.getDtPrice()<0){
            return new ResponseVO(-2,"请填写有效的价格",null);

        }
        if(dto.getDtActPrice()!=null && dto.getDtActPrice()<0){
            return new ResponseVO(-2,"请填写有效的活动价格",null);

        }
        Map map=BeanToMapUtil.beanToMap(dto,true);
        map.put("updateTime",new Date());
        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("dtDisabled",DisabledEnum.disabled0.getCode());
            ResponseVO res=this.commonInsertMap("ddw_ticket",map);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","allTicket");
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_ticket",map,"id",dto.getId());
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","allTicket");
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
