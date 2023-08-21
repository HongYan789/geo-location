/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author xk
 * @version : JdbcTemplateConfig.java, v 1.0 2021年07月20日 15时22分 xk Exp$
 */
@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class JdbcTemplateConfig {

    @Value("${spring.datasource.ip.driver-class-name:null}")
    private String driverClass;

    @Value("${spring.datasource.ip.url:null}")
    private String url;

    @Value("${spring.datasource.ip.username:null}")
    private String userName;

    @Value("${spring.datasource.ip.password:null}")
    private String passWord;


    @Bean(name = "ipJdbcTemplate")
    @Qualifier("ipJdbcTemplate")
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "db")
    public JdbcTemplate getIpJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);

        return new JdbcTemplate(dataSource);
    }

}
