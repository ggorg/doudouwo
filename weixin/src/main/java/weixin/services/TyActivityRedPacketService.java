package weixin.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.beans.CommonChildBean;
import com.gen.framework.common.beans.CommonSearchBean;
import com.gen.framework.common.exception.GenException;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.Tools;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.dao.TyActivityMapper;
import com.ty.dao.TyRedPacketMapper;
import com.ty.entity.TyRedPacket;
import com.ty.entity.TyUser;
import com.ty.entity.UserInfo;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Service
public class TyActivityRedPacketService extends CommonService {
    private final Logger logger = LoggerFactory.getLogger(TyActivityRedPacketService.class);
    @Autowired
    private Globals globals;

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private TyRedPacketMapper tyRedPacketMapper;

    @Autowired
    private TyActivityMapper tyActivityMapper;
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO<Map> pullRedPacket()throws Exception{
        Map condition=new HashMap();
        condition.put("subscribe",1);
        CommonSearchBean csb=new CommonSearchBean("ty_user",null,new CommonChildBean("weixin_user","openid","tuOpenId",condition));
        csb.setCustom("t1.*,ct0.appid");
        List<Map> objs=this.getCommonMapper().selectObjects(csb);
        Map<String,String> openidMap=new HashMap<String,String>();
        if(objs!=null && !objs.isEmpty()){
            JSONObject param=null;
            String callBackStr=null;
            Map insertMap=null;
            for(Map paramMap:objs){
                if(paramMap.containsKey("tuTelphone")){
                    openidMap.put(paramMap.get("tuOpenId").toString(),paramMap.get("appid").toString());
                    param=new JSONObject();
                    param.put("pay_user",paramMap.get("tuTelphone"));
                    param.put("act_code", ActEnum.act4.getCode());
                    if(globals.getPullRedPacketUrl().startsWith("http")){
                        callBackStr=HttpUtil.sendHtpps(globals.getPullRedPacketUrl(), TydicDES.encodeValue(param.toJSONString()));
                    }else{
                        callBackStr= FileUtils.readFileToString(new File(globals.getPullRedPacketUrl()));
                    }
                    if(StringUtils.isNotBlank(callBackStr)){
                        JSONObject callBackJson=JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
                        if(callBackJson.containsKey("status") && callBackJson.getString("status").equals("0")){
                            if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                                JSONArray jsonArray=callBackJson.getJSONArray("data");
                                JSONObject dataJson=null;
                                for(int i=0;i<jsonArray.size();i++){
                                    insertMap=new HashMap();
                                    dataJson=jsonArray.getJSONObject(i);
                                    TyRedPacket rp=this.commonObjectBySingleParam("ty_activity_red_packet","trFromId",dataJson.getString("packetId"),TyRedPacket.class);

                                    insertMap.put("tUid",paramMap.get("id"));
                                    insertMap.put("trAmount",Integer.parseInt(dataJson.getString("packetValue")));
                                    insertMap.put("trActivityId",dataJson.getString("efCampaignId"));
                                    insertMap.put("trFromId",dataJson.getString("packetId"));
                                    insertMap.put("trSendDate", DateUtils.parseDate(dataJson.getString("sendDate"),"yyyy-MM-dd HH:mm:ss"));
                                    insertMap.put("trIsOpen",false);
                                    insertMap.put("updateTime",new Date());

                                    if(rp==null){
                                        this.commonInsertMap("ty_red_packet",insertMap);
                                    }else{
                                        this.commonUpdateBySingleSearchParam("ty_activity_red_packet",insertMap,"id",rp.getId());
                                    }

                                }

                            }

                        }
                    }
                }
            }
            return new ResponseVO(1,"红包获取成功",openidMap);
        }
        return new ResponseVO(-2,"红包获取失败",null);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public ResponseVO openRedPacket(String openid,Integer actId)throws Exception{
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付关众号",null);
        }
        TyUser tyUser=this.commonObjectBySingleParam("ty_user","tuOpenId",openid, TyUser.class);
        if(tyUser==null || StringUtils.isBlank(tyUser.getTuTelphone())){
            return new ResponseVO(-2,"抱歉，请绑定手机号",null);
        }
        Map condition=new HashMap();
        condition.put("tUid",tyUser.getId());
       // condition.put("trIsOpen",false);
        List<Map>  tyAct= this.tyActivityMapper.getActivity(tyUser.getId(),actId);
        //List<Map> tyRedPackList=this.commonList("ty_activity_red_packet",null,null,null,condition);
        if(tyAct==null || tyAct.isEmpty()){
            return new ResponseVO(-3,"抱歉，没有红包可领取",null);
        }

        JSONObject param=null;
        Map updateMap=null;
        List amount=new ArrayList();

        Map insertMap=null;
        Map paramMap=null;
        Map conditionMap=null;
        Object taMinCostObj=null;
        Object taMaxCostObj=null;
        Object taUsedObj=null;
        Object taAmountObj=null;
        Object taNumberObj=null;
        Object taUsedNumberObj= null;

        for(Map map:tyAct){
            taMinCostObj=map.get("taMinCost");
            taMaxCostObj=map.get("taMaxCost");
            taUsedObj= map.get("taUsed");
            taAmountObj=map.get("taAmount");
            taNumberObj=map.get("taNumber");
            taUsedNumberObj= map.get("taUsedNumber");

            Long taMinCost=taMinCostObj instanceof Long?(Long)taMinCostObj:((Integer)taMinCostObj).longValue();
            Long taMaxCost=taMaxCostObj instanceof Long?(Long)taMaxCostObj:((Integer)taMaxCostObj).longValue();
            Long taUsed=taUsedObj instanceof Long?(Long)taUsedObj:((Integer)taUsedObj).longValue();
            Long taAmount=taAmountObj instanceof Long?(Long)taAmountObj:((Integer)taAmountObj).longValue();
            Long taNumber=taNumberObj instanceof Long?(Long)taNumberObj:((Integer)taNumberObj).longValue();
            Long taUsedNumber=taUsedNumberObj instanceof Long?(Long)taUsedNumberObj:((Integer)taUsedNumberObj).longValue();

            insertMap=new HashMap();
            insertMap.put("tUid",tyUser.getId());

            int randRedPackCost=new Random().nextInt(taMaxCost.intValue() - taMinCost.intValue() + 1) + taMinCost.intValue();

            if((taUsed+randRedPackCost)>taAmount){
                randRedPackCost=taAmount.intValue()-taUsed.intValue();
            }
            insertMap.put("trAmount",randRedPackCost);
            insertMap.put("trActivityId",map.get("id"));
            //insertMap.put("trFromId",dataJson.getString("packetId"));
            insertMap.put("updateTime",new Date());
            insertMap.put("trSeqCode", DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+01);
            ResponseVO reIsert=this.commonInsertMap("ty_activity_red_packet",insertMap);

             paramMap=new HashMap();
            paramMap.put("taUsed",taUsed+randRedPackCost);
            paramMap.put("taUsedNumber",taNumber+taUsedNumber);
            ResponseVO res1=this.commonUpdateBySingleSearchParam("ty_activity",paramMap,"id",map.get("id"));
            if(res1.getReCode()!=1){
                throw new GenException("openRedPacket->充值红包异常");
            }
            amount.add(Tools.getRealAmount(map.get("taAmount").toString()));
            param=new JSONObject();
            param.put("pay_user",tyUser.getTuTelphone());
            param.put("act_code", ActEnum.act4.getCode());
            param.put("efCampaignId", map.get("id"));
            param.put("packetId", reIsert.getData().toString());
            param.put("packetValue", insertMap.get("trAmount").toString());
            param.put("seqCode",insertMap.get("trSeqCode").toString());
            logger.info("TyActivityRedPacketService->openRedPacket->请求红包充值接口->requestData:{}",param.toJSONString());

            String callBackStr=HttpUtil.sendHtpps(globals.getOpenRedPacketUrl(),TydicDES.encodeValue(param.toJSONString()));
            if(StringUtils.isBlank(callBackStr)){
                throw new GenException("openRedPacket->充值红包返回参数异常");
            }

            JSONObject callBackJson=JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
            logger.info("TyActivityRedPacketService->openRedPacket->响应红包充值接口->requestData:{}",callBackJson.toJSONString());
            if(callBackJson.containsKey("status") && !callBackJson.getString("status").equals("0")){
                throw new GenException("openRedPacket->充值红包失败->"+callBackJson.getString("message"));
            }





        }
        return new ResponseVO(1,"抢红包成功",amount);


    }
    public ResponseVO getRedPacketSum(String openid)throws Exception{
        UserInfo user=this.weixinUserService.selectByopenid(openid);
        if(user==null || StringUtils.isBlank(user.getSubscribe()) || user.getSubscribe()=="未关注"){
            return new ResponseVO(-2,"请先关注翼支付关众号",null);
        }
        TyUser tyUser=this.commonObjectBySingleParam("ty_user","tuOpenId",openid, TyUser.class);
        if(tyUser==null || StringUtils.isBlank(tyUser.getTuTelphone())){
            return new ResponseVO(-2,"抱歉，请绑定手机号",null);
        }
        Map condition=new HashMap();
        condition.put("tUid",tyUser.getId());
        //condition.put("trIsOpen",true);
        CommonSearchBean csb=new CommonSearchBean("ty_activity_red_packet",null,"sum(t1.trAmount) as rpSum",null,null,condition);
        List<Map> dataList=this.getCommonMapper().selectObjects(csb);
        if(dataList!=null && !dataList.isEmpty() && dataList.size()>0){
            return new ResponseVO(1,"获取红包余额成功",Tools.getRealAmount(dataList.get(0)==null?"0":dataList.get(0).get("rpSum").toString()));
        }
        return new ResponseVO(1,"获取红包余额成功",Tools.getRealAmount("0"));
    }
    public ResponseVO isHasRedPacket(String openid,Integer actId)throws  Exception{

        Map map=this.commonObjectBySingleParam("ty_user","tuOpenid",openid);
        if(map!=null){
            Integer id=(Integer) map.get("id");
            List actList=this.tyActivityMapper.getActivity(id,actId);
            return new ResponseVO(1,"查询成功",actList);
        }
        return new ResponseVO(-2,"查询失败",null);
    }
    public ResponseVO findRedPacketRecord(String openid){
        Map chilCondition=new HashMap();
        chilCondition.put("tuOpenId",openid);
       // Map condition=new HashMap();
        //condition.put("trIsOpen",isOpen);
        //condition.put("trActivityId", MyEncryptUtil.getRealValue(actId));



        //this.commonList("ty_red_packet","updateTime by desc",null,null)
        CommonChildBean ccb=new CommonChildBean("ty_user","id","tUid",chilCondition);
        CommonSearchBean csb=new CommonSearchBean("ty_activity_red_packet","updateTime desc","t1.*",null,null,null,ccb);

        return new ResponseVO(1,"查询成功",this.getCommonMapper().selectObjects(csb));
    }

    public List countRedPacket(){
        List<Map> all=tyRedPacketMapper.getCount(null);

        return all;

    }

}
