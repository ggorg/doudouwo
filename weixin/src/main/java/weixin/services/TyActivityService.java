package weixin.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gen.framework.common.services.CacheService;
import com.gen.framework.common.services.CommonService;
import com.gen.framework.common.util.MyEncryptUtil;
import com.gen.framework.common.util.Page;
import com.gen.framework.common.vo.ResponseVO;
import com.ty.config.Globals;
import com.ty.entity.TyActivity;
import com.ty.enums.ActEnum;
import com.ty.util.HttpUtil;
import com.ty.util.TydicDES;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TyActivityService extends CommonService {
    private final Logger logger = LoggerFactory.getLogger(TyActivityService.class);
    @Autowired
    private Globals globals;

    @Autowired
    private CacheService cacheService;

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO pullActivity()throws Exception{
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("act_code", ActEnum.act3.getCode());
        String callBackStr=null;
        if(globals.getPullActivityUrl().startsWith("http")){
            logger.info("TyActivityService->pullActivity->请求获取活动接口->requestData:{}",jsonObject.toJSONString());
            callBackStr=HttpUtil.sendHtpps(globals.getPullActivityUrl(),TydicDES.encodeValue(jsonObject.toJSONString()));
        }else{
            callBackStr= FileUtils.readFileToString(new File(globals.getPullActivityUrl()));
        }

        if(StringUtils.isNotBlank(callBackStr)){
            JSONObject callBackJson=null;
            if(globals.getPullActivityUrl().startsWith("http")){

                callBackJson= JSONObject.parseObject(TydicDES.decodedecodeValue(callBackStr));
                logger.info("TyActivityService->pullActivity->响应获取活动接口->requestData:{}",callBackJson.toJSONString());
            }else{
                callBackJson= JSONObject.parseObject(callBackStr);
            }

            if(callBackJson.getString("status").equals("0")){
                if(callBackJson.containsKey("data") && !callBackJson.getJSONArray("data").isEmpty()){
                    JSONArray data=callBackJson.getJSONArray("data");
                    JSONObject obj=null;
                    Map map=null;
                    for(int d=0;d<data.size();d++){
                        obj=data.getJSONObject(d);
                        map=new HashMap();
                        map.put("id",Integer.parseInt(obj.getString("efCampaignId")));
                        map.put("taName",obj.getString("efCampaignName"));
                        map.put("taAmount",Integer.parseInt(obj.getString("cost")));
                        map.put("taRemark",obj.getString("remark"));
                        map.put("taBeginDate", DateUtils.parseDate(obj.getString("beginDate"),"yyyy-MM-dd HH:mm:ss"));
                        map.put("taEndDate", DateUtils.parseDate(obj.getString("endDate"),"yyyy-MM-dd HH:mm:ss"));
                        map.put("taRule", obj.getString("rule"));


                        map.put("updateTime",new Date());

                        long o=this.commonCountBySingleParam("ty_activity","id",map.get("id"));
                        if(o>0){
                            this.commonUpdateBySingleSearchParam("ty_activity",map,"id",map.get("id"));
                        }else{
                            map.put("createTime",new Date());
                            map.put("taDisabled",false);
                            map.put("taUsed",0);
                            map.put("taUsedNumber",0);
                            this.commonInsertMap("ty_activity",map);
                        }

                    }
                    return new ResponseVO(1,"获取最新活动成功",null);
                }
            }
        }
        return new ResponseVO(-2,"获取最新活动失败",null);
    }
    public ResponseVO getActivity(String idStr)throws Exception{
        String id=MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(id)){
            return new ResponseVO(-2,"获取失败",null);
        }
        TyActivity ta=this.commonObjectBySingleParam("ty_activity","id",Integer.parseInt(id), TyActivity.class);
        return new ResponseVO(1,"获取成功",ta);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseVO updateActivity(String idStr,Integer taMaxCost,Integer taMinCost,Integer taNumber,Boolean taDisabled)throws Exception{
        String id=MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(id)){
            return new ResponseVO(-2,"修改失败",null);
        }
        TyActivity ta= this.commonObjectBySingleParam("ty_activity","id",id,TyActivity.class);
        if(ta==null){
            return new ResponseVO(-2,"修改失败，记录不存在",null);
        }

        if(taNumber==null || taNumber==0){
            return new ResponseVO(-2,"修改失败,请填写数量",null);
        }

        if(taMaxCost==null || taMinCost==null){
            return new ResponseVO(-2,"修改失败,请填写区间值",null);
        }
        Integer realMaxMaxCost=ta.getTaAmount()%taNumber;
        if(realMaxMaxCost==0){
            realMaxMaxCost= ta.getTaAmount()/taNumber;
        }
        if(taMaxCost<taMinCost){
            return new ResponseVO(-2,"修改失败,区间最大值必须大于最小值",null);
        }
        Map map=new HashMap();
        map.put("taMaxCost",taMaxCost);
        map.put("taMinCost",taMinCost);
        map.put("taDisabled",taDisabled);
        map.put("taNumber",taNumber);
        map.put("updateTime",new Date());

        ResponseVO re=this.commonUpdateBySingleSearchParam("ty_activity",map,"id",Integer.parseInt(id));
        if(re.getReCode()==1){
            re.setReMsg(taDisabled?"保存并发布成功":"保存成功");
        }else{
            re.setReMsg("保存失败");
        }

        return re;

    }
    public Page getActivityPage(Integer pageNum)throws Exception{
        return this.commonPage("ty_activity","updateTime desc",pageNum,10,new HashMap<>());
    }
    public static void main(String[] args)throws Exception {
        System.out.println(new Random().nextInt(10 - 3 + 1) + 3);
        System.out.println(MyEncryptUtil.encry("1"));

        System.out.println(MyEncryptUtil.getRealValue("yUDM4MTOzYDN==QM"));
        JSONObject jsonObject=new JSONObject();
       // jsonObject.put("act_code", ActEnum.act3.getCode());
        jsonObject.put("phone_no","18038388686");
        String ranInt=RandomStringUtils.randomNumeric(5);
        jsonObject.put("act_code", ActEnum.act6.getCode());
        jsonObject.put("message",ranInt);

        String str=HttpUtil.sendHtpps("http://222.221.16.170/coupon/webserver/get",TydicDES.encodeValue(jsonObject.toJSONString()));
        System.out.println(TydicDES.decodedecodeValue(str));
    }
}
