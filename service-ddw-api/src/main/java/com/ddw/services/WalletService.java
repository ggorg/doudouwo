package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.dao.WalletDealMapper;
import com.ddw.dao.WalletErrorLogMapper;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.enums.OrderTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.enums.WalletDealCountTypeEnum;
import com.ddw.token.TokenUtil;
import com.ddw.util.CouponComparator;
import com.ddw.util.MsgUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 钱包
 */
@Service
@Transactional(readOnly = true)
public class WalletService extends CommonService {

    @Autowired
    private DDWGlobals ddwGlobals;
    @Autowired
    private WalletErrorLogMapper walletErrorLogMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WalletDealMapper walletDealMapper;

    @Autowired
    protected IncomeService incomeService;


    @Autowired
    protected BaseConsumeRankingListService baseConsumeRankingListService;

    @Value("${withdraw.max.cost:30000}")
    private Integer withdrawMaxCost;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO transferMoney(String token ,WalletTransferMoneyDTO dto)throws Exception{
        if(StringUtils.isBlank(IncomeTypeEnum.getName(dto.getIncomeType()))){
            return new ResponseApiVO(-2,"收益类型异常",null);
        }
        Integer userId=TokenUtil.getUserId(token);
        ResponseVO vo=null;
        if(IncomeTypeEnum.IncomeType1.getCode().equals(dto.getIncomeType())){
            ResponseApiVO<WalletGoddessInVO> gvo=this.getGoddessIn(userId);
            if(gvo.getReCode()!=1){
                return new ResponseApiVO(-2,"失败",null);

            }else{
                WalletGoddessInVO wg=gvo.getData();
                if(wg.getGoddessIncome()==null || wg.getGoddessIncome()<dto.getMoney()){
                    return new ResponseApiVO(-2,"女神收益金额不足",null);

                }
                Map setMap=new HashMap();
                setMap.put("money",dto.getMoney());
                setMap.put("goddessIncome",-dto.getMoney());
                Map searchMap=new HashMap();
                searchMap.put("userId",userId);
                vo= this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money","goddessIncome"});
            }
        }else if(IncomeTypeEnum.IncomeType2.getCode().equals(dto.getIncomeType())){
            ResponseApiVO<WalletPracticeInVO> gvo=this.getPracticeIn(userId);
            if(gvo.getReCode()!=1){
                return new ResponseApiVO(-2,"失败",null);

            }else{
                WalletPracticeInVO wg=gvo.getData();
                if(wg.getPracticeIncome()==null || wg.getPracticeIncome()<dto.getMoney()){
                    return new ResponseApiVO(-2,"代练收益金额不足",null);

                }
                Map setMap=new HashMap();
                setMap.put("money",dto.getMoney());
                setMap.put("practiceIncome",-dto.getMoney());
                Map searchMap=new HashMap();
                searchMap.put("userId",userId);
                vo=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money","practiceIncome"});
            }
        }
        if(vo==null ||  vo.getReCode()!=1){
            return new ResponseApiVO(-2,"转入钱包失败",null);

        }else{
            Map map=new HashMap();
            map.put("createTime",new Date());
            map.put("userId",userId);
            map.put("type",dto.getIncomeType());
            map.put("cost",dto.getMoney());
            this.commonInsertMap("ddw_transfer",map);
            return new ResponseApiVO(1,"成功",null);

        }
    }

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
    public ResponseApiVO getCoinAndExpenseCoin(Integer userid)throws Exception{
        ResponseApiVO<WalletDoubiVO> vo=getCoin(userid);
        WalletDoubiVO balanceVO=vo.getData();
        Map searchMap=new HashMap();
        searchMap.put("userId",userid);
        searchMap.put("orderType",OrderTypeEnum.OrderType6.getCode());
        balanceVO.setExpenseCoin((int)this.commonSumByBySingleSearchMap("ddw_order_view","price",searchMap));
        return new ResponseApiVO(1,"成功",balanceVO);
    }
    public CouponPO getCoupon(Integer couponId,Integer userId)throws Exception{
        Map search=new HashMap();
        search.put("id",couponId);
        search.put("userId",userId);
        search.put("used",0);
       // search.put("storeId",storeId);



        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo_coupon",null,"ct0.*",null,null,search,
                new CommonChildBean("ddw_coupon","id","couponId",null));
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
        search.put("userId",userid);


        CommonSearchBean csb=new CommonSearchBean("ddw_packet_gift",null,"t1.currentNum num ,ct0.id giftCode,ct0.dgName name,ct0.dgImgPath imgUrl",null,null,search,
                new CommonChildBean("ddw_gift","id","giftId",null));
        List<Map> giftPacketlist=this.getCommonMapper().selectObjects(csb);
        if(giftPacketlist==null){
            return new ResponseApiVO(2,"空背包",new ListVO(new ArrayList()));
        }

        return new ResponseApiVO(1,"成功",new ListVO(giftPacketlist));
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO useGiftOfPacket(String token,GiftUseDTO dto)throws Exception{
        if(dto.getCode()==null || dto.getCode()<0){
            return new ResponseApiVO(-2,"参数礼物编号异常",null);
        }
        if(dto.getNum()==null || dto.getNum()<0){
            return new ResponseApiVO(-2,"参数数量异常",null);
        }
        String groupId=TokenUtil.getGroupId(token);
        if(StringUtils.isBlank(groupId)){
            return new ResponseApiVO(-2,"请进房间再使用",null);

        }
        Integer appectUserId=Integer.parseInt(groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2"));
        Integer userId=TokenUtil.getUserId(token);
        Map search=new HashMap();
        search.put("userId",userId);
        search.put("id",dto.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_packet_gift",null,"t1.*,ct0.dgPrice",null,null,search,
                new CommonChildBean("ddw_gift","id","giftId",null));
        List<Map> giftMapList=this.getCommonMapper().selectObjects(csb);
        if(giftMapList==null || giftMapList.size()==0){
            return new ResponseApiVO(-2,"背包没购买此礼物",null);

        }
        Map m=giftMapList.get(0);
        Integer currentNum=(Integer)m.get("currentNum");
        if(dto.getNum()>currentNum){
            return new ResponseApiVO(-2,"抱歉，背包礼物不足",null);

        }
        ResponseVO res= this.incomeService.commonIncome(appectUserId,(Integer) m.get("dgPrice")*10*dto.getNum(),IncomeTypeEnum.IncomeType1,OrderTypeEnum.OrderType6,(String)m.get("orderNo"));

        this.baseConsumeRankingListService.save(userId,appectUserId,(Integer) m.get("dgPrice")*10*dto.getNum(),IncomeTypeEnum.IncomeType1);

        Map giveGift=new HashMap();
        giveGift.put("userId",userId);
        giveGift.put("acceptUserId",appectUserId);
        giveGift.put("num",dto.getNum());
        giveGift.put("giftId",dto.getCode());
        giveGift.put("incomeRecordId",res.getData());
        giveGift.put("incomeType",IncomeTypeEnum.IncomeType1.getCode());
        giveGift.put("createTime",new Date());
        giveGift.put("updateTime",new Date());
        this.commonInsertMap("ddw_give_gift_record",giveGift);

        Map edit=new HashMap();
        edit.put("currentNum",-dto.getNum());
        edit.put("updateTime",new Date());
        res=this.commonCalculateOptimisticLockUpdateByParam("ddw_packet_gift",edit,search,"version",new String[]{"currentNum"});
        if(res.getReCode()!=1){
            throw new GenException("失败");
        }
        return new ResponseApiVO(1,"成功",null);


    }
    public ResponseApiVO getCouponList(Integer userid){
        Map csearch=new HashMap();
        csearch.put("userId",userid);
        csearch.put("used",0);
        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo_coupon",null,"t1.id couponCode,ct0.dcName name,ct0.dcType type,ct0.dcMoney mop,ct0.dcStartTime startTime,ct0.dcEndTime endTime,ct0.dcDesc 'desc',ct1.dsName storeName,ct0.dcMinPrice minPrice,t1.used",null,null,csearch,
                new CommonChildBean("ddw_coupon","id","couponId",null),
                new CommonChildBean("ddw_store","id","storeId",null));
        csb.setJointName("left");
        List couponlist=this.getCommonMapper().selectObjects(csb);
        List list=new ArrayList();
        Date now=new Date();
        if(couponlist!=null){
            couponlist.forEach(a->{
                CouponVO vo=new CouponVO();
                try {

                    PropertyUtils.copyProperties(vo,a);
                    if(StringUtils.isNotBlank(vo.getStoreName())){
                        vo.setStoreName("平台");
                    }
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
            Collections.sort(list,new CouponComparator());
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
        balanceVO.setWithdrawMinMoney(withdrawMaxCost);
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
        balanceVO.setWithdrawMinMoney(withdrawMaxCost);
        return new ResponseApiVO(1,"成功",balanceVO);
    }

    /**
     * 忘记支付密码
     * @param userId
     * @param walletForgetPayPwdDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO forgetPayPwd(Integer userId,WalletForgetPayPwdDTO walletForgetPayPwdDTO)throws Exception{
        UserInfoPO userInfoPO = userInfoService.querySimple(userId);
        if(!StringUtils.isBlank(userInfoPO.getPhone())){
            if(!userInfoPO.getPhone().equals(walletForgetPayPwdDTO.getTelphone())){
                return new ResponseApiVO(-2,"与绑定手机不一致",null);
            }
            if(StringUtils.isBlank(walletForgetPayPwdDTO.getNewPwd())){
                return new ResponseApiVO(-3,"密码不能为空",null);
            }
            if(!MsgUtil.verifyCode(walletForgetPayPwdDTO.getTelphone(),walletForgetPayPwdDTO.getCode())){
                return new ResponseApiVO(-4,"失败,验证码不对",null);
            }
            //修改钱包密码
            Map setParams = new HashMap();
            setParams.put("payPwd",walletForgetPayPwdDTO.getNewPwd());
            Map searchCondition = new HashMap();
            searchCondition.put("userId",userId);
            ResponseVO vo = this.commonOptimisticLockUpdateByParam("ddw_my_wallet",setParams,searchCondition,"version");
            return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
        }else {
            return new ResponseApiVO(-1,"没绑定手机号码",null);
        }
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

    public WalletPO getWallet(Integer userId)throws Exception{
        return this.commonObjectBySingleParam("ddw_my_wallet","userId",userId,WalletPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO updatePayPwd(Integer userId,String oldPwd,String newPwd)throws Exception{
        WalletPO walletPO = this.getWallet(userId);
        //校验密码
        if(walletPO.getPayPwd() == null){
            if(StringUtils.isBlank(newPwd)){
                return new ResponseApiVO(-1,"密码不能为空",null);
            }
        }else {
            if(StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)){
                return new ResponseApiVO(-1,"密码不能为空",null);
            }
            if(!oldPwd.equals(walletPO.getPayPwd())){
                return new ResponseApiVO(-2,"原密码不正确",null);
            }
        }
        //修改钱包密码
        Map setParams = new HashMap();
        setParams.put("payPwd",newPwd);
        Map searchCondition = new HashMap();
        searchCondition.put("userId",userId);
        ResponseVO vo = this.commonOptimisticLockUpdateByParam("ddw_my_wallet",setParams,searchCondition,"version");
        return new ResponseApiVO(vo.getReCode(),vo.getReMsg(),null);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPayPwdErrorLog(Integer userId,String payPwd)throws Exception{
        Map payPwdErrorLog = new HashMap<>();
        payPwdErrorLog.put("userId",userId);
        payPwdErrorLog.put("payPwd",payPwd);
        payPwdErrorLog.put("createTime",new Date());
        return this.commonInsertMap("ddw_my_wallet_error_log",payPwdErrorLog);
    }
    /**
     * 校验支付密码
     * @param userId
     * @param payPwd
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO verifyPayPwd(Integer userId,String payPwd)throws Exception{
        WalletPO walletPO = this.getWallet(userId);
        int errorCount = walletErrorLogMapper.errorTodayCount(userId);
        if(errorCount >= 5){
            return new ResponseApiVO(-2,"今日密码输入错误达5次,请明日再试",null);
        }
        if(payPwd.equals(walletPO.getPayPwd())){
            return new ResponseApiVO(1,"成功",null);
        }else{
            //插入失败记录
            this.insertPayPwdErrorLog(userId,payPwd);
            return new ResponseApiVO(-1,"支付密码错误",null);
        }
    }

    /**
     * 生成随机码值，包含数字、大小写字母
     * @param number 生成的随机码位数
     * @return
     */
    public static String getRandomCode(int number){
        String codeNum = "";
        int [] code = new int[3];
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int num = random.nextInt(10) + 48;
            int uppercase = random.nextInt(26) + 65;
            int lowercase = random.nextInt(26) + 97;
            code[0] = num;
            code[1] = uppercase;
            code[2] = lowercase;
            codeNum+=(char)code[random.nextInt(3)];
        }
        return codeNum;
    }
    private String monthName(int i){
        switch (i){
            case 1:return "1月";
            case 2:return "2月";
            case 3:return "3月";
            case 4:return "4月";
            case 5:return "5月";
            case 6:return "6月";
            case 7:return "7月";
            case 8:return "8月";
            case 9:return "9月";
            case 10:return "10月";
            case 11:return "11月";
            case 12:return "12月";
        }
        return null;

    }
    public ResponseApiVO getDealCount(String token,WalletDealCountDTO wdto){
        if(StringUtils.isBlank(WalletDealCountTypeEnum.getName(wdto.getType()))){
            return new ResponseApiVO(-2,"统计类型错误",null);
        }
        String date=null;
        if(StringUtils.isNotBlank(wdto.getDate()) &&!wdto.getDate().matches("^[0-9]{4}-[0-9]{2}$")){
            return new ResponseApiVO(-2,"日期格式错误",null);
        }else if(StringUtils.isNotBlank(wdto.getDate())){
            date=wdto.getDate().substring(0,4)+"%";
        }else{
            date=DateFormatUtils.format(new Date(),"yyyy")+"%";
        }
        Integer userId=TokenUtil.getUserId(token);
        List<WalletDealRecordVO> l=this.walletDealMapper.dealRecord(userId,date,null,null);

        int costCount=0;
        int dealCount=0;
        List monthList=new ArrayList();
        Map<String,Map> monthMap=new HashMap();
        Map<String,Object> everyMonthCount=null;
        for(int i=1;i<=12;i++){
            everyMonthCount=new HashMap();
            everyMonthCount.put("monthName",monthName(i));
            monthList.add(everyMonthCount);
            monthMap.put(monthName(i),everyMonthCount);
        }



        String monthName=null;
        for(WalletDealRecordVO a:l){

            monthName=monthName(Integer.parseInt(a.getCreateTime().replaceAll("^[0-9]{4}-([0-9]{2}).*$","$1")));
            if(WalletDealCountTypeEnum.WalletDealCountType1.getCode().equals(wdto.getType()) && StringUtils.isNotBlank(PayStatusEnum.getName(a.getDealType()))){
                if(StringUtils.isNotBlank(wdto.getDate()) && a.getCreateTime().startsWith(wdto.getDate())){

                    costCount=costCount+a.getCost();
                    dealCount=dealCount+1;
                }
               Map m= monthMap.get(monthName);
                if(!m.containsKey("count")){
                    m.put("count",a.getCost());
                }else{
                    m.replace("count",(Integer)m.get("count")+a.getCost());
                }

            }else if(WalletDealCountTypeEnum.WalletDealCountType2.getCode().equals(wdto.getType())){
                if(StringUtils.isNotBlank(wdto.getDate()) && a.getCreateTime().startsWith(wdto.getDate())){

                    costCount=costCount+a.getCost();
                    dealCount=dealCount+1;
                }
                Map m= monthMap.get(monthName);
                if(!m.containsKey("count")){
                    m.put("count",a.getCost());
                }else{
                    m.replace("count",(Integer)m.get("count")+a.getCost());
                }
            }



        }
        Map data=new HashMap();
        data.put("list",monthList);
        data.put("costCount",costCount);
        data.put("dealCount",dealCount);
        return new ResponseApiVO(1,"成功",data);

    }
    public ResponseApiVO getDealRecord(String token,WalletDealRecordDTO dto){
        if(StringUtils.isNotBlank(dto.getDate()) &&!dto.getDate().matches("^[0-9]{4}-[0-9]{2}$")){
            return new ResponseApiVO(-2,"日期格式错误",null);
        }else if(StringUtils.isNotBlank(dto.getDate())){
            dto.setDate(dto.getDate()+"%");
        }else{
            dto.setDate(null);
        }
        Page p=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        Integer userId=TokenUtil.getUserId(token);
        List<WalletDealRecordVO> list=this.walletDealMapper.dealRecord(userId,dto.getDate(),p.getStartRow(),p.getEndRow());
        WalletDealVO vo=new WalletDealVO();

        if(list!=null && list.size()>0){
            list.forEach(a->{
                if(PayStatusEnum.PayStatus1.getCode().equals(a.getDealType())){
                    a.setTitle(OrderTypeEnum.getName(a.getType()));
                }else if(PayStatusEnum.PayStatus2.getCode().equals(a.getDealType())){
                    a.setTitle(OrderTypeEnum.getName(a.getType())+"（退款）");
                }else if(a.getDealType()==4){
                    a.setTitle("充值");
                }else{
                    a.setTitle(IncomeTypeEnum.getName(a.getType())+"转入钱包");

                }
            });
            vo.setList(list);

            List<WalletDealRecordVO> l=this.walletDealMapper.dealRecord(userId,dto.getDate(),null,null);
            int income=0;
            int pay=0;
            for(WalletDealRecordVO a:l){
                if(PayStatusEnum.PayStatus1.getCode().equals(a.getDealType())){
                    pay=pay+a.getCost();
                }if(PayStatusEnum.PayStatus2.getCode().equals(a.getDealType())){
                    income=income+a.getCost();
                }else{
                    income=income+a.getCost();
                }
            }
            vo.setPay(pay);
            vo.setIncome(income);
        }else{
            vo.setList(new ArrayList<>());
        }
        return new ResponseApiVO(1,"成功",vo);

    }

}
