package weixin.services;

import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.beans.CommonChildBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.entity.TyUser;
import com.ty.entity.UserInfo;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TyBindService extends CommonService {
    private final Logger logger = LoggerFactory.getLogger(TyBindService.class);


    @Autowired
    private CacheService cacheService;

    @Autowired
    private WeixinUserService weixinUserService;
    @Autowired
    private TyVoucherService tyVoucherService;

    @Autowired
    private Globals globals;

    public ResponseVO sendVaildCode(String telphone,String openid)throws Exception{

        if(StringUtils.isBlank(telphone)){
            return new ResponseVO(-2,"手机号为空",null);
        }
        if(!NumberUtils.isNumber(telphone)){
            return new ResponseVO(-2,"请填写正确的手机号",null);
        }
        /*if(!telphone.matches("^("+globals.getTyTelPrefix()+").*$")){
            return new ResponseVO(-2,"抱歉，非电信手机号码不能绑定",null);
        }*/
        ResponseVO resCheck=checkTyTelphone(telphone);
        if(resCheck.getReCode()!=1){
            return resCheck;
        }
        TyUser tyUser=this.commonObjectBySingleParam("ty_user","tuTelphone",telphone, TyUser.class);
        //long count=this.commonCountBySingleParam("ty_user","tuTelphone",telphone);
        if(tyUser!=null && StringUtils.isNotBlank(tyUser.getTuOpenId())){
            return new ResponseVO(-2,"抱歉，此手机号已绑定过",null);
        }
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付关众号",null);
        }
        JSONObject obj=new JSONObject();

        obj.put("phone_no",telphone);
        String ranInt=StringUtils.isNotBlank(globals.getTestMsgValid())?globals.getTestMsgValid():RandomStringUtils.randomNumeric(5);
        obj.put("act_code", ActEnum.act6.getCode());
        obj.put("message",ranInt);
        String callBackStr=null;
        if(globals.getVaildCodeUrl().startsWith("http")){
            logger.info("TyBindService->sendVaildCode->请求短信接口->requestData:{}",obj.toJSONString());
            callBackStr= HttpUtil.sendHtpps(globals.getVaildCodeUrl(), TydicDES.encodeValue(obj.toJSONString()));
        }else{

            callBackStr=FileUtils.readFileToString(new File(globals.getVaildCodeUrl()));
        }
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject json=JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
            logger.info("TyBindService->sendVaildCode->响应短信接口->解密->{}",json.toJSONString());
            if(json.containsKey("status") && json.getString("status").equals("0")){
                cacheService.setValidCode(telphone,ranInt);
                return new ResponseVO(1,"获取验证码成功",null);
            }
        }
        return new ResponseVO(-2,"获取验证码失败",null);
    }
    public ResponseVO checkTyTelphone(String telphone)throws Exception{

        JSONObject obj=new JSONObject();
        obj.put("pay_user",telphone);
        obj.put("act_code", ActEnum.act7.getCode());
        String callBackStr=null;
        if(globals.getCheckTyTelphoneUrl().startsWith("http")){
            logger.info("TyBindService->checkTyTelphone->请求天翼手机验证->requestData:{}",obj.toJSONString());
            callBackStr= HttpUtil.sendHtpps(globals.getCheckTyTelphoneUrl(), TydicDES.encodeValue(obj.toJSONString()));
        }else{

            callBackStr=FileUtils.readFileToString(new File(globals.getVaildCodeUrl()));
        }
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject json=JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
            logger.info("TyBindService->checkTyTelphone->响应天翼手机验证->解密->{}",json.toJSONString());
            if(json.containsKey("status") && json.getString("status").equals("0")){

                return new ResponseVO(1,"手机验证成功",null);
            }
        }
        return new ResponseVO(-2,"手机验证失败",null);

    }
    public ResponseVO vaildeCode(String telphone,String code,String openid)throws Exception{
        if(StringUtils.isBlank(telphone)){
            return new ResponseVO(-2,"手机号为空",null);
        }
        if(StringUtils.isBlank(code)){
            return new ResponseVO(-2,"验证码为空",null);
        }
        if(!NumberUtils.isNumber(telphone)){
            return new ResponseVO(-2,"请填写正确的手机号",null);
        }
        if(!telphone.matches("^("+globals.getTyTelPrefix()+").*$")){
            return new ResponseVO(-2,"抱歉，非电信手机号码不能绑定",null);
        }
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付关众号",null);
        }
        String cacheCode=this.cacheService.getValidCode(telphone);
        if(StringUtils.isBlank(cacheCode)){
            return new ResponseVO(-2,"验证码已经超时请重新发送一次",null);
        }
        if(cacheCode.equals(code)){
            this.cacheService.deleteValidCode(telphone);
            return new ResponseVO(1,"验证成功",null);
        }else{
            return new ResponseVO(-2,"验证有误",null);
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO bind(String telphone,String openid)throws Exception{
        if(StringUtils.isBlank(telphone)){
            return new ResponseVO(-2,"手机号为空",null);
        }
        if(!telphone.matches("^("+globals.getTyTelPrefix()+").*$")){
            return new ResponseVO(-2,"抱歉，非电信手机号码不能绑定",null);
        }
        if(StringUtils.isBlank(openid)){
            return new ResponseVO(-2,"微信openid为空",null);
        }
        TyUser tyUser=this.commonObjectBySingleParam("ty_user","tuTelphone",telphone, TyUser.class);
        //long count=this.commonCountBySingleParam("ty_user","tuTelphone",telphone);
        if(tyUser!=null && StringUtils.isNotBlank(tyUser.getTuOpenId())){
            return new ResponseVO(-2,"抱歉，此手机号已绑定过",null);
        }
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付公众号",null);
        }
        Map param=new HashMap();
        param.put("tuOpenId",openid);
        param.put("tuTelphone",telphone);
        param.put("updateTime",new Date());
       // param.put("tuDisabled",false);
        ResponseVO<Integer> vo=null;
        if(tyUser==null){
            param.put("createTime",new Date());
            vo=this.commonInsertMap("ty_user",param);
        }else{
            vo=this.commonUpdateBySingleSearchParam("ty_user",param,"tuTelphone",telphone);
            TyUser tu=this.commonObjectBySingleParam("ty_user","tuTelphone",telphone,TyUser.class);
            vo.setData(tu.getId());
        }
        if(vo.getReCode()==1){
            //tyVoucherService.saveVoucheies(telphone,vo.getData());
            return new ResponseVO(1,"绑定成功",null);
        }
        return new ResponseVO(-2,"绑定失败",null);

    }
    //@Cacheable(value = "bindInfoCache",key = "#openid")
    public Map checkIsBind(String openid){
        Map childCondition=new HashMap();
        childCondition.put("subscribe",1);
        childCondition.put("openid",openid);

        CommonSearchBean csb=new CommonSearchBean("ty_user",  null,null,null,null,(Object)null,new CommonChildBean("weixin_user","openid","tuOpenId",childCondition));
        List<Map> dataList=this.getCommonMapper().selectObjects(csb);
        if(dataList!=null && !dataList.isEmpty()){
            return dataList.get(0);

        }
        return null;
    }

}
