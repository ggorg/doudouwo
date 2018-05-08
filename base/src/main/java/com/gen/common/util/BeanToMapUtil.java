package com.gen.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BeanToMapUtil {
	private static final Logger logger = LoggerFactory.getLogger(BeanToMapUtil.class);

	/**
	 * pojo转换成map  </br>
	 * by cancheung modify
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj,boolean ... isNotAddFilterStr) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(obj.getClass());
			for (PropertyDescriptor sourcePd : descriptors) {
				String name = sourcePd.getName();

				if (!"class".equals(name)) {
					Method readMethod = sourcePd.getReadMethod();
					if (readMethod != null) {
						try {
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(obj);
							//if (value != null) {
							if(isNotAddFilterStr!=null && isNotAddFilterStr.length>0){
								params.put(name, value);
							}else{
								params.put("`"+name+"`", value);
							}

							//}
						} catch (Throwable ex) {
							logger.error("beanToMap:", ex);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("beanToMap:", e);
		}
		return params;
	}
}
