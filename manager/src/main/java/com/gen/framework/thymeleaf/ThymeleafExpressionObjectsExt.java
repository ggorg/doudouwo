package com.gen.framework.thymeleaf;

import com.gen.framework.util.ToolsExt;

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
public class ThymeleafExpressionObjectsExt {
	private static Map<String, Object> objects;

	static {
		objects = new HashMap<String, Object>();
		objects.put("TdTool", new ToolsExt());
	}

	public static Map<String, Object> getObjects() {
		return objects;
	}
}
