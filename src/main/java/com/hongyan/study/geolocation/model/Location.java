package com.hongyan.study.geolocation.model;

import com.hongyan.study.geolocation.config.IpType;
import lombok.Data;

import java.io.Serializable;

/**
 * 目前位置仅仅支持以下基础参数
 *
 * @author zy
 * @version : Location.java, v 1.0
 */
@Data
public class Location implements Serializable {

    /**
     * 国际
     */
    private String country;
    /**
     * 地区
     */
    private String region;
    /**
     * 城市
     */
    private String city;
    /**
     * ipv4
     */
    private String ip;
    /**
     * ip类型
     */
    private IpType ipType;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;

}
