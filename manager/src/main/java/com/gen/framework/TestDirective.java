package com.gen.framework;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.IOException;
import java.util.Map;

public class TestDirective implements TemplateDirectiveModel{

	
	public void execute(Environment env, Map params, TemplateModel[] tm, TemplateDirectiveBody tdb)
			throws TemplateException, IOException {
		Object paramValue = params.get("myid"); //这里模版页面传进来的参数叫做articleId   
	    int id=0;  
	    if (paramValue instanceof TemplateNumberModel) {  
	        id = ((TemplateNumberModel) paramValue).getAsNumber().intValue();  
	    }                                                                                                                                 


	    env.setVariable("article", DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(123)); /*使用env.setVariable方法设置变量article（这个就是要返回给页面的数据），注意需要使用freemarker中的静态成员变量DEFAULT_WRAPPER把article处理一下 */  
	    tdb.render(env.getOut()); //最后使用body.render(env.getOut())将数据交给模版页面
		
	}

}
