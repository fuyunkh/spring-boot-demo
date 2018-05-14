package com.example.config;

import com.example.spring.aspect.SystemLogAspect;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by Zhangkh on 2017/3/6.
 */
@Configuration
@EnableAutoConfiguration
@EnableAsync
@EnableSwagger2
@EnableWebMvc
//@EnableRedisHttpSession
@ComponentScan({"com.example.controllers",
        "com.example.services",
        "com.example.exception",
        "com.example.spring",
        "com.example.metrics",
        "com.example.thread",
        "com.example.spring.aspect"})
@MapperScan(basePackages = "com.example.mappers")
//@ServletComponentScan(basePackages = "com.example.spring") //配置servlet

@PropertySource(value = "classpath:/com/example/data/config/data.properties",
        ignoreResourceNotFound = true)
public class ApplicationConfig extends WebMvcConfigurerAdapter {
    private final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    @Profile("default")
    DataSource dataSource(Environment env, ApplicationContext context) throws IOException {
        Properties properties = new Properties();
        String file = env.getProperty("com.example.data.config.file");
        System.out.println("filename:" + file);
        properties.load(context.getResource(file).getInputStream());
        setPropsFromEnv("hikari", properties, env);
        return new HikariDataSource(new HikariConfig(properties));
    }

    @Bean
    SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        return sessionFactoryBean;
    }

    @Bean
    ExecutorType executorType(Environment environment) {
        String executorType = environment.getProperty("com.example.data.mybatis.executorType",
                "SIMPLE");
        ExecutorType type = ExecutorType.valueOf(executorType);
        return type == null ? ExecutorType.SIMPLE : type;
    }

    @Bean
    @Scope("prototype")
    SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        return new SqlSessionTemplate(sqlSessionFactory, executorType);
    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    SystemLogAspect systemLogAspect() {
        return new SystemLogAspect();
    }

//    @Bean
//    public ServletRegistrationBean registrationBean1()
//    {
//        ServletRegistrationBean registration = new ServletRegistrationBean();
//        registration.setServlet(new ServletA());
//        registration.addUrlMappings("/test/a");
//
//        return registration;
//    }
//
//    @Bean
//    public ServletRegistrationBean registrationBean2()
//    {
//        ServletRegistrationBean registration = new ServletRegistrationBean();
//        registration.setServlet(new ServletB());
//        registration.addUrlMappings("/test/b");
//
//        return registration;
//    }


    private void setPropsFromEnv(String prefix, Properties props, Environment env) {
        Collections.list(props.keys()).stream().forEach(key -> {
            String name = key.toString();
            String envName = prefix + '_' + name;
            String envAltName = prefix + '_' + name.replace('.', '_');

            if (env.containsProperty(envName)) {
                props.setProperty(name, env.getProperty(envName));
            } else if (env.containsProperty(envAltName)) {
                props.setProperty(name, env.getProperty(envAltName));
            }
        });
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        System.out.println("================ pre addInterceptors");
//        logger.info("regist Interceptor");
//        registry.addInterceptor(new InterceptorA()).addPathPatterns("/test/**");
//        //registry.addInterceptor(new InterceptorB()).addPathPatterns("/test/a");
//        System.out.println("addInterceptors after================");
//    }

    //连接redis配置
//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//        JedisConnectionFactory jcf = new JedisConnectionFactory();
//        return jcf;
//    }
//
//    @Bean
//    public HttpSessionStrategy httpSessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }


}
