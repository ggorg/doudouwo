package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.*;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.PayApiConstant;
import com.ddw.util.PayApiUtil;
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
import java.util.*;

/**
 * 钱包
 */
@Service
@Transactional(readOnly = true)
public class WalletService extends CommonService {

    @Autowired
    private DDWGlobals ddwGlobals;

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
     * 获取总资产
     * @param userid
     * @return
     * @throws Exception
     */
    public ResponseApiVO getAsset(Integer userid)throws Exception{
        WalletAssetVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userid, WalletAssetVO.class);

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
