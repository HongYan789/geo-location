/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.repository.impl;

import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.model.bin.IPResult;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.util.GeoIp2LocationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 基于bin文件 ip解析成location实现
 *
 * @author zy
 * @date 2022-02-23
 */
@Slf4j
@Repository
public class BinIp2Repository implements Ip2Repository {

    public BinIp2Repository() {
        // 初始化构造函数时，拷贝BIN文件到工作目录下
        GeoIp2LocationUtil.initGeo();
    }

    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("BinIp2Repository queryCity ip:{}", ip);
        IPResult ipResult = GeoIp2LocationUtil.ip2Location(ip);
        if (ipResult == null) {
            return new IpResultPO();
        }
        IpResultPO result = new IpResultPO();
        result.setCity(ipResult.getCity());
        result.setCountryLong(ipResult.getCountryLong());
        result.setRegion(ipResult.getRegion());
        result.setLatitude(String.valueOf(ipResult.getLatitude()));
        result.setLongitude(String.valueOf(ipResult.getLongitude()));
        return result;
    }
}
