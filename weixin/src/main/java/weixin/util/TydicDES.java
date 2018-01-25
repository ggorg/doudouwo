package weixin.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class TydicDES {
	private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	private static final String DES_KEY	="sDER$73Y";//

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度=8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 *
	 *             异常
	 */
	private static String encode(String key, String data) throws Exception {
		return encode(key, data.getBytes());
	}

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 *
	 *             异常
	 */
	private static String encode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			byte[] bytes = cipher.doFinal(data);

			BASE64Encoder base64 = new BASE64Encoder();
			return base64.encode(bytes);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	private static byte[] decode(String key, byte[] data) throws Exception {
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 获取编码后的值
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static String decodeValue(String key, String data) {
		byte[] datas;
		String value = null;
		try {
			// datas = decode(key, bs64.decode(data.getBytes()));
			BASE64Decoder bs64 = new BASE64Decoder();

			datas = decode(key, bs64.decodeBuffer(data));
			value = new String(datas);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}

	/**
	 * DES加密
	 * @return
	 */
	public static String encodeValue(String str) throws Exception{
		return encode(DES_KEY,str);
	}
	
	/**
	 * DES解密
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String decodedecodeValue(String str) throws Exception{
		return decodeValue(DES_KEY,str);
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println("明：abcd ；密：" + encode(WW_KEY, "abcd"));
//		System.out.println("明：abcd ；密：" + decodeValue(WW_KEY,"jsgOVUs260YHWr8Si/kE6w=="));
		String myString="YangTaoLinkU";
		String mm= encodeValue("{\"pay_user\":\"18712345678\",\"act_code\":\"6\",\"message\":\"123456\"}");
		System.out.println("密文："+mm);
		System.out.println("解密："+decodedecodeValue(mm));
	}
}
