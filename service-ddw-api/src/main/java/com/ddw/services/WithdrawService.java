package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.enums.WithdrawStatusEnum;
import com.ddw.enums.WithdrawTypeEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class WithdrawService extends CommonService {

    public ResponseApiVO searchWithdrawDetail(String token, PageNoDTO pageNoDTO){
        Map searchMap = new HashMap();
        searchMap.put("userId", TokenUtil.getUserId(token));//drReviewStatus,//drReviewDesc
        Page page=new Page(pageNoDTO.getPageNo()==null?1:pageNoDTO.getPageNo(),10);
        CommonSearchBean csb=new CommonSearchBean("ddw_withdraw_record","t1.updateTime desc","t1.incomeType type,t1.money,DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') applTime,ct1.accountType,ct0.drReviewStatus reviewStatus,ct0.drReviewDesc reviewDesc,DATE_FORMAT(ct0.updateTime,'%Y-%m-%d %H:%i:%S') reviewTime",page.getStartRow(),page.getEndRow(),searchMap,
                new CommonChildBean("ddw_review","drBusinessCode","id",null),
                new CommonChildBean("ddw_withdraw_way","id","withdrawWayId",null)
        );

        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        //WithdrawStatusEnum.
        list.forEach(a->{
            Integer reviewCode=(Integer) a.get("reviewStatus");
            a.put("reviewStatusName",ReviewStatusEnum.getName(reviewCode));
            a.put("accountTypeName",WithdrawTypeEnum.getName((Integer) a.get("accountType")));
        });
        return new ResponseApiVO(1,"成功",new ListVO<>(list));

    }

    public ResponseApiVO search(String token)throws Exception{
        Map searchMap = new HashMap();
        searchMap.put("userId", TokenUtil.getUserId(token));
        List<Map> list=this.commonList("ddw_withdraw_way",null,"t1.id code,t1.accountNoStr,t1.accountType,t1.accountRealName",null,null,searchMap);
        if(list==null || list.isEmpty()){

            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        String accountNoStr=null;
        String accountRealName=null;
        for(Map m:list){
            accountNoStr=(String)m.get("accountNoStr");
            accountRealName=(String)m.get("accountRealName");
            if(accountNoStr.length()==11){
                m.replace("accountNoStr",accountNoStr.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }else if(accountNoStr.matches("^.*@[^.]+[.].+$")){
                m.replace("accountNoStr",accountNoStr.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4"));
            }else if(accountNoStr.length()>11){
                m.replace("accountNoStr",accountNoStr.substring(0,accountNoStr.length()-4).replaceAll(".","*")+accountNoStr.substring(accountNoStr.length()-4,accountNoStr.length()));
            }else{
                m.replace("accountNoStr",accountNoStr.replaceAll("(.).*(.)","$1****$2"));

            }
            m.replace("accountRealName", accountRealName.replaceAll("([\\d\\D]{1})(.*)", "$1**"));

        }

        return new ResponseApiVO(1,"成功",new ListVO<>(list));
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO delete(String token, CodeDTO dto){
        if (dto.getCode()==null || dto.getCode()<=0) {
            return new ResponseApiVO(-2, "参数异常", null);
        }
        Map searchMap=new HashMap();
        searchMap.put("id",dto.getCode());
        searchMap.put("userId",TokenUtil.getUserId(token));
        ResponseVO vo=this.commonDeleteByParams("ddw_withdraw_way",searchMap);
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO save(String token,WithdrawWayDTO dto) {
        if (StringUtils.isBlank(dto.getAccountNoStr())) {
            return new ResponseApiVO(-2, "账号不能为空", null);
        }
        if (StringUtils.isBlank(dto.getAccountRealName())) {
            return new ResponseApiVO(-2, "账号人名称不为空", null);
        }
        if (StringUtils.isBlank(WithdrawTypeEnum.getName(dto.getAccountType()))) {
            return new ResponseApiVO(-2, "提现类型不正常", null);
        }
        Integer userId = TokenUtil.getUserId(token);
        Map searchMap = new HashMap();
        //searchMap.put("userId",userId);
        searchMap.put("accountNoStr", dto.getAccountNoStr());
        Map map = this.commonObjectBySearchCondition("ddw_withdraw_way", searchMap);
        Map poMap = BeanToMapUtil.beanToMap(dto, true);
        poMap.put("updateTime", new Date());
        poMap.remove("code");
        if (dto.getCode() == null || dto.getCode() <= 0) {
            if (map != null) {
                return new ResponseApiVO(-2, "此账号已被绑定", null);

            }

            poMap.put("createTime", new Date());
            poMap.put("userId",userId);
            ResponseVO inserVo=this.commonInsertMap("ddw_withdraw_way",poMap);
            if(inserVo.getReCode()!=1){
                return new ResponseApiVO(-2, "添加失败", null);

            }
            return new ResponseApiVO(1, "添加成功", null);

        }else{
            if(map!=null){
                Integer poUserId=(Integer) map.get("userId");
                Integer id=(Integer) map.get("id");
                if(!userId.equals(poUserId) || !id.equals(dto.getCode())){
                    return new ResponseApiVO(-2, "此账号已被绑定", null);

                }
            }
            ResponseVO updateVo=this.commonUpdateBySingleSearchParam("ddw_withdraw_way",poMap,"id",dto.getCode());
            if(updateVo.getReCode()!=1){
                return new ResponseApiVO(-2, "修改失败", null);

            }
            return new ResponseApiVO(1, "修改成功", null);

        }
    }

    public static void main(String[] args) {
        System.out.println("aa@nn.com".matches("^.*@[^.]+[.].+$"));
        System.out.println("aa000000000000@nn.com".replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4"));
        String name = "13";
        //name.substring(0,name.length()-4);
        System.out.println(name.replaceAll("(.).*(.)","$1****$2"));
    }
}
