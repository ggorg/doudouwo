package com.gen.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具计算类
 * 
 * @author cancheung
 *
 */
public class Tools {
	private static ThreadLocal<String> openid=new ThreadLocal();
	private static WebApplicationContext wa;

	/**
	 * 分析值，为空或错误时使用默认值
	 * 
	 * @param value
	 * @param defValue
	 * @return
	 */
	private Double parseByDefault(Object value, Double defValue) {
		if (null != value) {
			try {
				return Double.parseDouble(value.toString());
			} catch (Exception e) {
			}
		}
		if (null != defValue) {
			return defValue;
		} else {
			return 0.0;
		}
	}

	/**
	 * 两数相加，出错使用0
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public Number add(Object a, Object b) {
		double aValue = parseByDefault(a, 0.0);
		double bValue = parseByDefault(b, 0.0);
		return aValue + bValue;
	}

	/**
	 * 两数相减，出错使用0
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Number diff(Object o1, Object o2) {
		double aValue = parseByDefault(o1, 0.0);
		double bValue = parseByDefault(o2, 0.0);
		return aValue - bValue;
	}

	public String diffDelZero(Object o1, Object o2) {
		return deleteZero(diff(o1, o2));
	}

	/**
	 * 两数相除
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public Number divided(Object o1, Object o2) {
		double aValue = parseByDefault(o1, 0.0);
		double bValue = parseByDefault(o2, 0.0);
		if (bValue == 0) {
			return 0;
		}
		return aValue / bValue;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public String deleteZero(Object o) {
		DecimalFormat f = new DecimalFormat("0.##");
		return f.format(parseByDefault(o, 0.0));
	}

	/**
	 * 字符串转日期
	 * 
	 * @param dateTime
	 * @param format
	 * @return
	 */
	public static Date stringToDate(String dateTime, String format) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(dateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date stringToDate(String dateTime) {
		String format = "yyyy-MM-dd";
		if (dateTime.length() == "yyyy-MM-dd HH:mm:ss".length()) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
//		else if (dateTime.length() == "yyyy-MM-dd".length()) {
//			format = "yyyy-MM-dd";
//		}
		return stringToDate(dateTime, format);
	}

	/**
	 * 两个date相差的毫秒
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static long dateGetTime(Date d1, Date d2) {
		return (Math.abs(d1.getTime() - d2.getTime()));
	}

	/**
	 * 获取满标用时
	 * 
	 * @param startDate
	 * @param CompleteDate
	 * @return
	 */
	public String getFullUseTime(Date startDate, Date CompleteDate) {
		if (CompleteDate == null) {
			return "-时-分-秒";
		} else {
			long l = dateGetTime(startDate, CompleteDate);
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
			if (day > 0) {
				return day + "天" + hour + "时" + min + "分" + s + "秒";
			} else {
				return hour + "时" + min + "分" + s + "秒";
			}
		}
	}

	public String getDeadLineStr(Integer typeId, Integer deadLine, Integer deadType, Integer isRepayAd, Date addDate,
			Date returnDate) {
		int projectType = typeId == null ? 0 : typeId;
		if (projectType == 23)// 浮动项目宝
		{
			if (isRepayAd != null && isRepayAd == 1) {
				// 算是提前还款时
				long l = dateGetTime(returnDate, addDate);
				long diffDay = l / (24 * 60 * 60 * 1000) + 1;
				if (diffDay <= 31)
					return diffDay + "天";
				else {
					long diffMonth = l / (31 * 24 * 60 * 60 * 1000);
					return diffMonth + "个月" + (diffDay % 31) + "天";
				}

			} else {
				return deadLine + (deadLine == 2 ? " 天" : " 个月");
			}
		} else if (projectType == 21) { // 活期宝
			return "活期";
		} else {
			return deadLine + (deadLine == 2 ? " 天" : " 个月");
		}
	}
	
	public static String subtract(Date date,Date date2){
		if(date==null)return "-"+date2.getTime()/(24*60*60*1000)+"天";
		if(date2==null) return date.getTime()/(24*60*60*1000)+"天";
		long times = date.getTime()-date2.getTime();
		return (times>=0?"":"-")+ (times>=0?times/(24*60*60*1000):-times/(24*60*60*1000))+"天";
	}
	public static boolean isLogin(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();

		if(session.getAttribute("user")==null){
			return false;
		}
		return true;
	}

	public static Object getSession(String key){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		return session.getAttribute(key);
	}
	public static void setSession(String key,Object obj){


		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();

		/*HttpServletResponse response = attrs.getResponse();
		Cookie cookie = new Cookie(key, value);
		*//*if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}*//*

		cookie.setPath("/");
		response.addCookie(cookie); */
		session.setAttribute(key,obj);
	}
	public static boolean setParentSession(Integer id){


		setSession("parentId",id);
		return true;
	}
	public static Integer getParentId(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		return (Integer)session.getAttribute("parentId");


	}

	public static String getOpenidByThreadLocal(){
		String openidstr=openid.get();
		return openidstr;
	}
	public static String setOpenidByThreadLocal(String token){
		if(StringUtils.isBlank(token))return null;
		String value=MyEncryptUtil.getRealValue(token);
		if(StringUtils.isBlank(value)){
		return null;
		}
		openid.set(value);
		return value;
	}
	public static void clearLoginSession(){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpSession session=attrs.getRequest().getSession();
		session.removeAttribute("user");
		session.removeAttribute("userPower");
		session.invalidate();
	}

	public static String getRealAmount(String input){

		String newInput= StringUtils.isNotBlank(input)?input:"0";
		if(newInput.length()==1){
			newInput="0.0"+newInput;
		}else if(newInput.length()==2){
			newInput="0."+newInput;
		}else{
			newInput=newInput.replaceAll("^([0-9]+)([0-9][0-9])$","$1.$2");
		}
		return newInput;
	}
	public static void noCachePage(){
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response=attrs.getResponse();
		response.addHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		// 设置 HTTP/1.1 no-cache 头
		response.addHeader("Cache-Control", "no-store,no-cache,must-revalidate,post-check=0,no-transform");
		// 设置 IE 扩展 HTTP/1.1 no-cache headers， 用户自己添加
		// 设置标准 HTTP/1.0 no-cache header.
		response.addHeader("Pragma", "no-cache");


	}
	/*public static String initJssdk(String url,String jsApiList){

		if(StringUtils.isBlank(url)){
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request=attrs.getRequest();
			url=request.getRequestURL().append("?").append(request.getQueryString()).toString();
		}
		return WeiXinTools.initJssdk(url,jsApiList);
	}*/
	public static void setCookie(String key,String value){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


		HttpServletResponse response = attrs.getResponse();
		HttpServletRequest request=attrs.getRequest();
		try {

			System.out.println("i am cookie:"+value);
			Cookie cookie=new Cookie(key,MyEncryptUtil.encry(value));
			response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getCookie(String key){
		ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=attrs.getRequest();
		try {
			Cookie[] cookies=request.getCookies();
			if(cookies!=null && cookies.length>0){
				for(Cookie c:cookies){
					if(c.getName().equals(key)){
						return MyEncryptUtil.getRealValue(c.getValue());


					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public static String encrypt(String input){
		return MyEncryptUtil.encry(input);
	}

	public static boolean checked(String tagidList,String id){
        boolean res = false;
        if(StringUtils.isBlank(tagidList))return false;
        if(tagidList.indexOf(",")==-1 && tagidList.equals(id)){
            res = true;
        }else{
            for(String s:tagidList.split(",")){
                if(s.equals(id)){
                    res = true;
                    break;
                }
            }
        }
        return res;
    }


	public static boolean isNumberAndEnglishStr(String input) {
		if(NumberUtils.isNumber(input)){
			return false;
		}
		return NumberUtils.isNumber(input.replaceAll("(?i)[a-z]", ""));
	}
	public static WebApplicationContext getWebapplication(){
		if(wa==null){
			ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			wa= WebApplicationContextUtils.getWebApplicationContext(attrs.getRequest().getServletContext());
		}
		return wa;

	}
	public static <T>T getBean(Class<T> clazz){
		return getWebapplication().getBean(clazz);
	}
}