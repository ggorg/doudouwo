package com.gen.common.util;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class GenPropertySourceFactory implements PropertySourceFactory {
    //@Autowired
   // private ConfigurableEnvironment env;

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

        String fileName=resource.getResource().getFilename();


        if(fileName.equals("application-*.propertiess")){


            DefaultResourceLoader defaultResourceLoader=new DefaultResourceLoader();
            resource=new EncodedResource(defaultResourceLoader.getResource("application-a.properties"),"UTF-8");
        }
        PropertySource ps=(name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));

       // Resource resource = this.resourceLoader.getResource(resolvedLocation);
        System.out.println(name+"----------------------"+fileName+"------"+ps.getName());
        return ps;
    }

}
