package com.gen.common.config;

import com.gen.common.thymeleaf.Enums;
import com.gen.common.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ThymeleafConfig {

	@Value("${gen.thymeleaf.extTagFun}")
	private String extTagFun;

	@Value("${a}")
	private String a;

	@Value("${b}")
	private String b;




	/**
	 * 添加自定义函数
	 * 
	 * @param springTemplateEngine
	 */
	@Autowired
	public void additionalExpression(SpringTemplateEngine springTemplateEngine) {
		// 添加言
		// final Set<IDialect> additionalDialects = new HashSet<>();
		// additionalDialects.add(menuDialect());
		// springTemplateEngine.setAdditionalDialects(additionalDialects);
		System.out.println(a+","+b);
		Map<String, Object> objects=new HashMap();
		objects.put("TdTool", new Tools());
		objects.put("TdEnum", new Enums());

		try {
			if(StringUtils.isNotBlank(extTagFun)){
				String[] exts=extTagFun.split(",");
				if(exts!=null && exts.length>0){
					for(String e:exts){
						String[] eKey=e.split(":");
						Class clas=Class.forName(eKey[1].trim());
						objects.put(eKey[0], clas.newInstance());
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		final SpringStandardDialect standardDialect = (SpringStandardDialect) springTemplateEngine.getDialectsByPrefix()
				.get("th");
		standardDialect.setVariableExpressionEvaluator(new SpelVariableExpressionEvaluator() {
			@Override
			protected Map<String, Object> computeAdditionalExpressionObjects(IProcessingContext processingContext) {
				return objects;
			}
		});

	}
}