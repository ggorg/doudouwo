package weixin.util;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * 请求校验工具类
 */
public class SignUtil {
    // 与接口配置信息中的Token要一致
    private static String token = "123456";

    /**
     * 验证签名
     * 
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        // 参数为空直接返回
        signature = signature == null ? "" : signature;
        timestamp = timestamp == null ? "" : timestamp;
        nonce = nonce == null ? "" : nonce;
        // 数组排序
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr == null ? false : tmpStr.equals(signature.toUpperCase());
    }

    /**
     * JS-SDK使用权限签名
     * 
     * @param jsapi_ticket 用于调用微信JS接口的临时票据
     * @param url 当前网页的URL，不包含#及其后面部分
     * @return
     */
    public static JSONObject getSign(String jsapi_ticket, String url) {
        JSONObject jsonObject = new JSONObject();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String signature = "";
        String string1;
        // 注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(string1.getBytes());
            signature = byteToStr(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        jsonObject.put("url", url);
        jsonObject.put("jsapi_ticket", jsapi_ticket);
        jsonObject.put("nonceStr", nonce_str);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("signature", signature);
        jsonObject.put("retCode", 1);
        jsonObject.put("retMsg", "success");
        return jsonObject;
    }

    /**
     * 将字节数组转换为十六进制字符串
     * 
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     * 
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    /**
     * 
     * @return 随机字符串
     */
    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    /**
     * 时间戳
     * 
     * @return
     */
    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}