/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.autoconfigure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zy
 * @version : GeoLocationProperties.java, v 1.0
 */
@Data
@ConfigurationProperties(prefix = "geo")
public class GeoLocationProperties {
    /**
     * 地理位置服务提供商
     * 配置文件有则取配置文件的value
     * 配置文件没有则用默认值：ip2
     */
    @Value("${provider:ip2}")
    private String provider;
    /**
     * 存储类别：db、csv、data、baidu、bin（默认），主要针对于离线库
     */
    private String storetype;
    /**
     * 库类型：在线：true，离线：false
     */
    @Value("${libraryType:false}")
    private Boolean libraryType;
    /**
     * 在线库 accessKey
     */
    private String accessKey;
    /**
     * 在线库 secretkey
     */
    private String secretkey;

}
