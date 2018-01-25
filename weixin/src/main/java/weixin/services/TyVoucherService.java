package weixin.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CommonService;
import com.ty.config.Globals;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@EnableAsync
@Service
public class TyVoucherService extends CommonService {
    private final Logger logger = LoggerFactory.getLogger(TyVoucherService.class);
    @Autowired
    private Globals globals;

    @Autowired
    private TyBindService tyBindService;

   // @Async
    //@Transactional(propagation = Propagation.REQUIRED)
    public List pullVoucheies(String telphone,Integer id)throws Exception{
        JSONObject param=new JSONObject();
        param.put("pay_user",telphone);
        param.put("act_code", ActEnum.act5.getCode());

        String callBackStr=null;
        if(globals.getSearchVoucherUrl().startsWith("http")){
            logger.info("TyVoucherService->saveVoucheies->请求代金卷接口->requestData:{}",param.toJSONString());
            callBackStr= HttpUtil.sendHtpps(globals.getSearchVoucherUrl(), TydicDES.encodeValue(param.toJSONString()));
        }else{
            callBackStr=FileUtils.readFileToString(new File(globals.getSearchVoucherUrl()));
        }
        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson= JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
            logger.info("TyVoucherService->saveVoucheies->响应代金卷接口->responseData:{}",callBackJson.toJSONString());
            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                    JSONArray jsonArray=callBackJson.getJSONArray("data");
                    JSONObject json=null;
                    Map obj=null;
                    List datalist=new ArrayList();
                    for(int i=0;i<jsonArray.size();i++){
                        obj=new HashMap();
                        json=jsonArray.getJSONObject(i);
                        obj.put("tvCode",json.getString("code"));
                       // obj.put("tvSendDate",DateUtils.parseDate(,"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.S"));
                        obj.put("tvSendDate",json.getString("sendDate"));
                        obj.put("tvBusinessType",json.getString("businessType").replace("null",""));
                        obj.put("tvRemark",json.getString("remark").replace("null",""));
                        obj.put("tvValue",Integer.parseInt(json.containsKey("value")?json.getString("value"):"0"));
                        obj.put("tvBeginTime",json.getString("beginTime").replace(" 00:00:00",""));
                        obj.put("tvEndTime",json.getString("endTime").replace(" 00:00:00",""));
                        obj.put("tvBeginTimeFlag",DateUtils.parseDate(json.getString("beginTime"),"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.S"));
                        obj.put("tvEndTimeFlag",DateUtils.parseDate(json.getString("endTime"),"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.S"));
                        obj.put("createTime",new Date());
                        obj.put("tuid",id);
                        datalist.add(obj);
                        //this.commonInsertMap("ty_voucher",obj);
                    }
                    return datalist;
                }
            }
        }
        return null;
    }
    public Map findVoucheies(String openid)throws Exception{

        Map tyUserMap=this.tyBindService.checkIsBind(openid);
        if(tyUserMap!=null && tyUserMap.size()>0){
            List<Map> dataList=this.pullVoucheies((String)tyUserMap.get("tuTelphone"),(Integer) tyUserMap.get("id"));
            if(dataList!=null && !dataList.isEmpty()){
                Date currentDate=new Date();

                List noExpire=dataList.stream().filter(k->currentDate.before((Date)k.get("tvEndTimeFlag"))).collect(Collectors.toList());
                List expire=dataList.stream().filter(k->currentDate.after((Date)k.get("tvEndTimeFlag"))).collect(Collectors.toList());
                Map map=new HashMap();
                map.put("noExpire",noExpire);
                map.put("expire",expire);
                return map;
            }
        }
      return null;
    }
    /*public Map findVoucheies_db(String openid){
        Map childCondition=new HashMap();
        childCondition.put("tuOpenId",openid);


        CommonSearchBean csb=new CommonSearchBean("ty_voucher",  null,"t1.*",null,null,null,new CommonChildBean("ty_user","id","tUid",childCondition));
        List<Map> dataList=this.getCommonMapper().selectObjects(csb);
        if(dataList!=null && !dataList.isEmpty()){
            Date currentDate=new Date();

            List noExpire=dataList.stream().filter(k->currentDate.before((Date)k.get("tvEndTime"))).collect(Collectors.toList());
            List expire=dataList.stream().filter(k->currentDate.after((Date)k.get("tvEndTime"))).collect(Collectors.toList());
            Map map=new HashMap();
            map.put("noExpire",noExpire);
            map.put("expire",expire);
            return map;
        }
        return null;
    }*/


}
