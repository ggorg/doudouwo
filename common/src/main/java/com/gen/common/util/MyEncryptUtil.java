package com.gen.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

public class MyEncryptUtil {
	public static String decipher(String input){

		return new String(Base64.decodeBase64(input.getBytes()));
	}
	public static String encry(String input){
		StringBuilder builder=new StringBuilder();
		builder.append(new String(Base64.encodeBase64(input.getBytes())));
		return getDateBase64Encrypt()+builder.reverse().toString();
	}
	public static String getRealValue(String input){
		String dbeStr=getDateBase64Encrypt();
		if(input.startsWith(dbeStr)){
			StringBuilder builderUsername=new StringBuilder();

			builderUsername.append(input.replace(dbeStr, "")).reverse();
			return decipher(builderUsername.toString());
		}
		return null;
	}
	public static String getDateBase64Encrypt() {
		try {
			Calendar calendar=Calendar.getInstance();
			long currentMaxDay=calendar.getActualMaximum(calendar.DAY_OF_MONTH);
			long num=currentMaxDay-calendar.get(calendar.HOUR_OF_DAY);
	
			long hour=calendar.get(calendar.HOUR_OF_DAY);
			long math=Long.parseLong(DateFormatUtils.format(new Date(), "yyyyMMdd"));
	//System.out.println((math*num-hour));
			String dayBase64=new String(Base64.encodeBase64(((math*num-hour)+"").getBytes()));
			StringBuilder builder=new StringBuilder();
			return builder.append(dayBase64).reverse().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public static void main(String[] args) {
		System.out.println(encry("oJJk5xPBNr6XsKE9_nYowgtGc1Lw"));
		StringBuilder builder=new StringBuilder();
		System.out.println(builder.append(new String(Base64.encodeBase64("058571885".getBytes()))).reverse().toString());
	}
}
