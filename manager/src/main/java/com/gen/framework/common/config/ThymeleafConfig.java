package com.gen.framework.common.config;

import java.util.Map;

import com.gen.framework.common.thymeleaf.ThymeleafExpressionObjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.dialect.SpringStandardDialect;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;

@Configuration
public class ThymeleafConfig {

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

		final SpringStandardDialect standardDialect = (SpringStandardDialect) springTemplateEngine.getDialectsByPrefix()
				.get("th");
		standardDialect.setVariableExpressionEvaluator(new SpelVariableExpressionEvaluator() {
			@Override
			protected Map<String, Object> computeAdditionalExpressionObjects(IProcessingContext processingContext) {
				return ThymeleafExpressionObjects.getObjects();
			}
		});

	}
}