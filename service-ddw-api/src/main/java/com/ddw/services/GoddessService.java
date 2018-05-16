package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.*;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 女神
 * Created by Jacky on 2018/4/16.
 */
@Service
public class GoddessService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;

    @Autowired
    private CacheService cacheService;

    public GoddessPO getAppointment(Integer userid)throws Exception{
        GoddessPO obj=(GoddessPO)cacheService.get("goddess-"+userid);
        if(obj==null){
            GoddessPO po= this.commonObjectBySingleParam("ddw_goddess","userId",userid,GoddessPO.class);
            if(po!=null){
                Integer ap=po.getAppointment()==null?0:po.getAppointment();
                po.setAppointment(ap);
                cacheService.set("goddess-"+userid,po);
                return po;
            }
        }else{
            return  obj;
        }
       return null;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO updateAppointment(String userid,Integer ap)throws Exception{
       GoddessPO po= this.commonObjectBySingleParam("ddw_goddess","userId",userid,GoddessPO.class);
       if(po!=null){
           if(GoddessAppointmentEnum.getName(ap)==null){
               return new ResponseApiVO(-2,"ap状态值不正确",null);
           }
           Map map=new HashMap();
           map.put("appointment",ap);
           ResponseVO vo=this.commonUpdateBySingleSearchParam("ddw_goddess",map,"userId",userid);
           if(vo.getReCode()==1){
               po.setAppointment(ap);
               cacheService.set("goddess-"+userid,po);
               return new ResponseApiVO(1,"修改成功",null);
           }
       }
       return new ResponseApiVO(-2,"修改失败",null);

    }

    public GoddessPO getByUserId(Integer userId)throws Exception{
        return this.commonObjectBySingleParam("ddw_goddess","userId",userId,GoddessPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(GoddessDTO goddessDTO)throws Exception{
        GoddessPO goddessPO = new GoddessPO();
        PropertyUtils.copyProperties(goddessPO,goddessDTO);
        goddessPO.setCreateTime(new Date());
        goddessPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_goddess",goddessPO);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO apply(UserInfoVO user,int storeId)throws Exception{
        Map conditionMap = new HashMap<>();
        conditionMap.put("drProposer",user.getId());
        conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        //查询状态审核未通过
        conditionMap.put("drReviewStatus,!=",2);
        ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
        if(realPO != null){
            return new ResponseVO(-3,"不允许重复提交申请",null);
        }
        //插入审批表
        ReviewPO reviewPO=new ReviewPO();
        String bussinessCode = String.valueOf(new Date().getTime());
        reviewPO.setDrBusinessCode(bussinessCode);
        reviewPO.setCreateTime(new Date());
        reviewPO.setDrProposerName(user.getRealName());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        reviewPO.setDrProposer(Integer.valueOf(user.getId()));
        reviewPO.setDrApplyDesc("申请成为女神");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.goddessFlag2.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        return this.commonReviewService.submitAppl(reviewPO);
    }

}
