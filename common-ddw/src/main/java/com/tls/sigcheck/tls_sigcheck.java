package com.tls.sigcheck;

import com.ddw.config.DDWGlobals;
import com.ddw.util.LiveRadioConstant;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.util.Properties;

@Component
@Scope("prototype")
public class tls_sigcheck {
    private final Logger logger = Logger.getLogger(tls_sigcheck.class);

    @Autowired
    private DDWGlobals ddwGlobals;



    private static String initLib;
    /**
     * 加载动态库
     */
    public  void loadJniLib() {
        if(initLib==null){
            initLib=ddwGlobals.getLibpath();
            System.load(initLib);
        }

    }

    /**
     * @param strExpire 超时时长，以秒为单位，建议使用不超过一个月，如果是一天，10*24*3600=1116000，将字符串 1116000 填入即可
     * @param strAppid3rd 如果是自有账号，填写与 SdkAppid 一样的值可以（但一定要填）
     * @param SdkAppid 创建应用时，页面上分配的 sdkappid
     * @param strIdentifier 用户标示符，即用户 id
     * @param dwAccountType 创建应用时，页面上分配的 accounttype
     * @param strPriKey 私钥内容，注意是内容，不是私钥的文件路径
     *
     * @return 0 为成功，非零为失败，使用 getErrMsg() 获取错误信息
     */
    @Deprecated
    public native int tls_gen_signature_ex(
        String strExpire,
        String strAppid3rd,
        String SdkAppid,
        String strIdentifier,
        String dwAccountType,
        String strPriKey
        );
    /**
     * @param sdkAppid 创建应用时，页面上分配的 sdkappid
     * @param identifier 用户标示符，即用户 id
     * @param priKey 私钥内容，注意是内容，不是私钥的文件路径
     *
     * @return 0 为成功，非零为失败，使用 getErrMsg() 获取错误信息
     */
    public native int tls_gen_signature_ex2(
        String sdkAppid,
        String identifier,
        String priKey
        );

    /**
     * @param sdkAppid 创建应用时，页面上分配的 sdkappid
     * @param identifier 用户标示符，即用户 id
     * @param priKey 私钥内容，注意是内容，不是私钥的文件路径
	 * @param expire 用户定义有效期，不再使用默认值
     *
     * @return 0 为成功，非零为失败，使用 getErrMsg() 获取错误信息
     */
    public native int tls_gen_signature_ex2_with_expire(
        String sdkAppid,
        String identifier,
        String priKey,
		String expire
        );

    /**
     * 校验sig
     * @param strJsonWithSig sig 字符串
     * @param strPubKey 公钥内容
     * @param strAccountType 配置页面上的 acctype
     * @param str3rd 与 sdkappid 一致
     * @param Appid 配置页面上的 sdkappid
     * @param strIdentify 用户 id
     * @return 0 为成功，非零为失败，使用 getErrMsg() 获取错误信息
     */
    @Deprecated
	public native int tls_check_signature_ex(
        String strJsonWithSig,
        String strPubKey,
        String strAccountType,
        String str3rd,
        String Appid,
        String strIdentify
        );
    
    /**
     * 校验sig
     * @param userSig sig 字符串
     * @param pubKey 公钥内容
     * @param sdkAppid 配置页面上的 sdkappid
     * @param identifier 用户 id
     * @return 0 为成功，非零为失败，使用 getErrMsg() 获取错误信息，
     * 另外可以通过 getExpireTime() 和 getInitTime() 获取有效期和生成时间
     */
	public native int tls_check_signature_ex2(
        String userSig,
        String pubKey,
        String sdkAppid,
        String identifier
        );

    protected int expireTime;
    protected int initTime;
	protected String strErrMsg;	
	protected String strJsonWithSig;        // 这个名称已经过时了，格式不是 json

    public int getExpireTime()
    {
        return expireTime;
    }

    public int getInitTime()
    {
        return initTime;
    }

	public String getErrMsg()
	{
		return strErrMsg;
	}
	public String getSig()
	{
		return strJsonWithSig;
	}

	public String createSign(String identifier){

        try {
            loadJniLib();
            String keycontent= FileUtils.readFileToString(new File(ddwGlobals.getPrivateKeypath()));
            int n= tls_gen_signature_ex2(LiveRadioConstant.SDK_API,identifier,keycontent);
                if(n==0){
                return getSig();
            }
        }catch (Exception e){
            logger.error("tls_signcheck->createSign",e);
        }
        return null;

    }

    public void setDdwGlobals(DDWGlobals ddwGlobals) {
        this.ddwGlobals = ddwGlobals;
    }

    public static void main(String[] args) throws Exception{
	    tls_sigcheck ts=new tls_sigcheck();

        Properties pro=new Properties();
        pro.load(tls_sigcheck.class.getClassLoader().getResourceAsStream("application-dev.properties"));
        DDWGlobals ddwGlobals=new DDWGlobals();
        ddwGlobals.setLibpath(pro.getProperty("im.tls.sigcheck.lib.path"));
        ts.setDdwGlobals(ddwGlobals);
        //System.out.println(ts.createSign("ddwadmin"));

    }

}
