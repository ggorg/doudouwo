package com.gen.framework.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import com.gen.framework.common.interceptor.MybatisInterceptor;

@Configuration
public class SpringContextConfig {

    @Autowired
    private Environment env;

    /**
     * 创建数据源(数据源的名称：方法名可以取为XXXDataSource(),XXX为数据库名称,该名称也就是数据源的名称)
     */
 @Bean(destroyMethod="close",name="mainDbDataSource")
    public DataSource mainDbDataSource() throws Exception {
    	
        Properties props = new Properties();
        
        props.put("driverClassName", env.getProperty("mybatis.jdbc.driverClassName"));
        props.put("url", env.getProperty("mybatis.jdbc.url"));
        props.put("username", env.getProperty("mybatis.jdbc.username"));
        props.put("password", env.getProperty("mybatis.jdbc.password"));
       props.put("initialSize", env.getProperty("mybatis.jdbc.initialSize"));
         props.put("maxActive", env.getProperty("mybatis.jdbc.maxActive"));
        props.put("maxWait", env.getProperty("mybatis.jdbc.maxWait"));
         props.put("timeBetweenEvictionRunsMillis", env.getProperty("mybatis.jdbc.timeBetweenEvictionRunsMillis"));
         props.put("minEvictableIdleTimeMillis", env.getProperty("mybatis.jdbc.minEvictableIdleTimeMillis"));
        props.put("validationQuery", env.getProperty("mybatis.jdbc.validationQuery"));
        props.put("testWhileIdle", env.getProperty("mybatis.jdbc.testWhileIdle"));
        props.put("testOnBorrow", env.getProperty("mybatis.jdbc.testOnBorrow"));
        props.put("testOnReturn", env.getProperty("mybatis.jdbc.testOnReturn"));
        props.put("poolPreparedStatements", env.getProperty("mybatis.jdbc.poolPreparedStatements"));
        props.put("maxPoolPreparedStatementPerConnectionSize", env.getProperty("mybatis.jdbc.maxPoolPreparedStatementPerConnectionSize"));
        return DruidDataSourceFactory.createDataSource(props);
    }

/*    @Bean(destroyMethod="close",name="otherDbDataSource")
    public DataSource otherDbDataSource() throws Exception {
        Properties props = new Properties();
        props.put("driverClassName", env.getProperty("mybatis.jdbc2.driverClassName"));
        props.put("url", env.getProperty("mybatis.jdbc2.url"));
        props.put("username", env.getProperty("mybatis.jdbc2.username"));
        props.put("password", env.getProperty("mybatis.jdbc2.password"));
        return DruidDataSourceFactory.createDataSource(props);
    }*/

    /**
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）
     */
    @Bean
   @Primary
   @Scope("singleton")
    public DataSource dataSource( DataSource mainDbDataSource) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.main, mainDbDataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(mainDbDataSource);
       // MapperScannerConfigurer m=new MapperScannerConfigurer();
        
        return dataSource;
    }


/*    *//**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean(name="sqlSessionFactorys")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    	
    	System.out.println("readdatasource");
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        
        fb.setDataSource(dataSource);// 指定数据源(这个必须有，否则报错)
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        //fb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));// 指定基包
        fb.setMapperLocations( new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapperLocations")));//

        Interceptor[] its = new Interceptor[1];
        its[0] = new MybatisInterceptor();
        fb.setPlugins(its);
      // fb.setConfigLocation(new DefaultResourceLoader() .getResource(env .getProperty("mybatis.configLocation")));

        return fb.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager( DataSource dataSource) throws Exception {
    
        return new DataSourceTransactionManager(dataSource);
    }

}
