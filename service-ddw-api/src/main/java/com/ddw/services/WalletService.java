package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.PayApiConstant;
import com.ddw.util.PayApiUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.OrderUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 钱包
 */
@Service
@Transactional(readOnly = true)
public class WalletService extends CommonService {

    @Autowired
    private DDWGlobals ddwGlobals;

    public ResponseApiVO getIncome(Integer incomeType,Integer pageNo,String token)throws Exception{
        if(StringUtils.isBlank(IncomeTypeEnum.getName(incomeType))){
            return new ResponseApiVO(-2,"参数异常",null);
        }
        if(pageNo==null){
            pageNo=1;
        }
        Map search=new HashMap();
        search.put("diType",incomeType);
        search.put("userId",TokenUtil.getUserId(token));

        List list=this.commonList("ddw_income_record","createTime desc","t1.diMoney money,t1.diType type,t1.createTime,t1.orderNo,t1.orderType",pageNo,10,search);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(2,"成功",new ListVO(new ArrayList()));

        }
        List dataList=new ArrayList();

        list.forEach(a->{
            IncomeVO incomeVO=new IncomeVO();
            try {
                PropertyUtils.copyProperties(incomeVO,a);
                incomeVO.setOrderTypeName(OrderTypeEnum.getName(incomeVO.getOrderType()));
                dataList.add(incomeVO);
            } catch (Exception e) {
              e.printStackTrace();
            }
        });

        return new ResponseApiVO(1,"成功",new ListVO(dataList));
    }

    /**
     * 获取余额
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getBalance(Integer userid)throws Exception{
        WalletBalanceVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletBalanceVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }
    /**
     * 获取逗币
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getCoin(Integer userid)throws Exception{
        WalletDoubiVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletDoubiVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }
    public CouponPO getCoupon(Integer couponId,Integer userId,Integer storeId)throws Exception{
        Map search=new HashMap();
        search.put("id",couponId);
        Map csearch=new HashMap();
        csearch.put("userId",userId);
        csearch.put("used",0);
        csearch.put("storeId",storeId);
        CommonSearchBean csb=new CommonSearchBean("ddw_coupon",null,"t1.*",0,1,search,
                new CommonChildBean("ddw_userinfo_coupon","couponId","id",csearch));
        List couponlist=this.getCommonMapper().selectObjects(csb);
        if(couponlist!=null && !couponlist.isEmpty()){
            CouponPO po=new CouponPO();
            PropertyUtils.copyProperties(po,couponlist.get(0));
            return po;
        }
        return null;
    }
    public ResponseApiVO getGiftPackge(Integer userid){
        Map search=new HashMap();
        search.put("used",0);
        search.put("userId",userid);
        Map csearch=new HashMap();
        csearch.put("doPayStatus",PayStatusEnum.PayStatus1.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_order_gift",null,"ct1.id giftCode,ct1.dgName name,ct1.dgImgPath imgUrl",null,null,search,
                new CommonChildBean("ddw_order","id","orderId",csearch),
                new CommonChildBean("ddw_gift","id","giftId",null));
        List giftPacketlist=this.getCommonMapper().selectObjects(csb);
        if(giftPacketlist==null){
            return new ResponseApiVO(2,"空背包",new ListVO(new ArrayList()));
        }
        return new ResponseApiVO(1,"成功",new ListVO(giftPacketlist));
    }
    public ResponseApiVO getCouponList(Integer userid){
        Map csearch=new HashMap();
        csearch.put("userId",userid);
        csearch.put("used",0);
        CommonSearchBean csb=new CommonSearchBean("ddw_coupon",null,"t1.id couponCode,t1.dcName name,t1.dcType type,t1.dcMoney mop,t1.dcStartTime startTime,t1.dcEndTime endTime,t1.dcDesc 'desc',ct1.dsName storeName,t1.dcMinPrice minPrice,ct0.used",1,1,null,
                new CommonChildBean("ddw_userinfo_coupon","couponId","id",csearch),
                new CommonChildBean("ddw_store","id","storeId",null));
        List couponlist=this.getCommonMapper().selectObjects(csb);
        List list=new ArrayList();
        Date now=new Date();
        if(couponlist!=null){
            couponlist.forEach(a->{
                CouponVO vo=new CouponVO();
                try {

                    PropertyUtils.copyProperties(vo,a);
                    if(vo.getEndTime().after(now)){
                        vo.setExpire(1);
                    }else{
                        vo.setExpire(-1);
                    }
                    list.add(vo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(couponlist==null){
            return new ResponseApiVO(2,"没有优惠卷",new ListVO(list));
        }
        return new ResponseApiVO(1,"成功",new ListVO(list));
    }
    /**
     * 获取总资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getAsset(Integer userid)throws Exception{
        WalletAssetVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletAssetVO.class);
        ResponseApiVO<ListVO<List>> couponVo=this.getCouponList(userid);
        ResponseApiVO<ListVO<List>> giftPackVo=this.getGiftPackge(userid);
        List couponlist=couponVo.getData().getList();
        if(couponlist!=null && !couponlist.isEmpty()){
            balanceVO.setCouponList(couponlist);
        }
        List giftPacketlist=giftPackVo.getData().getList();
        if(giftPacketlist!=null && !giftPacketlist.isEmpty()){
            balanceVO.setPackList(giftPacketlist);
        }

        return new ResponseApiVO(1,"成功",balanceVO);
    }


    /**
     * 获取女神资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getGoddessIn(Integer userid)throws Exception{
        WalletGoddessInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletGoddessInVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }


    /**
     * 获取代练资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getPracticeIn(Integer userid)throws Exception{
        WalletPracticeInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletPracticeInVO.class);

        return new ResponseApiVO(1,"成功",balanceVO);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO createWallet(Integer userid){
        Map wallet=new HashMap();
        wallet.put("userId",userid);
        wallet.put("money",0);
        wallet.put("coin",0);
        wallet.put("version",1);
        wallet.put("goddessIncome",0);
        wallet.put("practiceIncome",0);
        wallet.put("createTime",new Date());
        wallet.put("updateTime",new Date());
        ResponseVO vo=this.commonInsertMap("ddw_my_wallet",wallet);
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }



}
