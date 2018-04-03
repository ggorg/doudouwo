package com.ddw.servies.headquarters;

import com.ddw.beans.headquarters.MaterialDTO;
import com.ddw.beans.headquarters.MaterialPO;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
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

/**
 * 原材料
 */
@Service
public class MaterialService  extends CommonService {

    @Autowired
    private MainGlobals mainGlobals;

    public Page findPage(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        return this.commonPage("ddw_material","updateTime desc",pageNo,10,condtion);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(MaterialDTO materialDTO)throws Exception{
        if(materialDTO==null){
            return new ResponseVO(-2,"请填写原材料信息",null);

        }
        if(materialDTO.getDmUnit()==null){
            return new ResponseVO(-2,"请填写净含量信息",null);

        }
        if(StringUtils.isBlank(materialDTO.getDmName())){
            return new ResponseVO(-2,"请填写材料名称",null);

        }
        if(materialDTO.getDmCost()==null || materialDTO.getDmCost()<0){
            return new ResponseVO(-2,"请填写有效的成本价格",null);

        }
        if(materialDTO.getDmSales()==null || materialDTO.getDmSales()<0){
            return new ResponseVO(-2,"请填写有效的零售价格",null);

        }
       // Map mapPo=new HashMap();
        MaterialPO materialPO=new MaterialPO();

        PropertyUtils.copyProperties(materialPO,materialDTO);
        if( materialDTO.getDmImgFile().isEmpty()){
            String dmImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( materialDTO.getDmImgFile().getOriginalFilename());
            UploadFileMoveUtil.move( materialDTO.getDmImgFile(),mainGlobals.getRsDir(), dmImgName);
        }
        return null;
    }
}
