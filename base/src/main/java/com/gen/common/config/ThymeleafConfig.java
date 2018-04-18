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
import java.util.Set;

@Configuration
public class ThymeleafConfig {

	@Value("${gen.thymeleaf.extTagFun:}")
	private String extTagFun;


	@ConfigurationProperties(prefix ="gen.thymeleaf.tagFun")
	@Bean
	public Map getFun(){
	return new HashMap();
	}





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

		Map<String, Object> objects=new HashMap();
		objects.put("TdTool", new Tools());
		objects.put("TdEnum", new Enums());


		try {
			Map<String,String> extMap=getFun();
			if(extMap!=null){
				Set<String> keys=extMap.keySet();
				for(String k:keys){
					Class clas=Class.forName(extMap.get(k).trim());
					objects.put(k, clas.newInstance());
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