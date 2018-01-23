package com.gen.framework.common.thymeleaf;

import com.gen.framework.common.util.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Thymeleaf扩展函数 <code>
 
 <span th:text="${#TdEnum.text('RepaymentTypeEnum',pageindex)}"></span>
 <span th:text="${#TdEnum.text('RepaymentTypeEnum',1)}"></span>
 
 </code>
 * 
 * @author cancheung
 *
 */
public class ThymeleafExpressionObjects {
	private static Map<String, Object> objects;

	static {
		objects = new HashMap<String, Object>();
		objects.put("TdTool", new Tools());
		objects.put("TdEnum", new Enums());
	}

	public static Map<String, Object> getObjects() {
		return objects;
	}
}
