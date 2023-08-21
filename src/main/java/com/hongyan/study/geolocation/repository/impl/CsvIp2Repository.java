/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.repository.impl;

import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.util.CsvUtils;
import com.hongyan.study.geolocation.util.FileUtil;
import com.hongyan.study.geolocation.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 基于nio+2分法csv读取实现
 *
 * @author zy
 * @version : CsvIp2RepositoryImpl.java, v 1.0
 */
@Slf4j
@Repository
public class CsvIp2Repository implements Ip2Repository {

    private static byte[] fileByteArray = null;

    static {
        try {
            fileByteArray = FileUtil.getByteArray("IP2LOCATION-LITE-DB11.CSV");
        } catch (IOException e) {
            log.error("init csv file error");
        }
    }


    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("CsvIp2Repository queryCity ip:{}", ip);
        //利用ip 地址做为 key 读取 resource 目录下 IP2LOCATION-LITE-DB11.CSV 文件内容，并匹配查询结果
        try {
            BigInteger bigInteger = null;
            if (IpUtils.isIpV4(ip)) {
                bigInteger = IpUtils.translateToV4No(ip);
            } else {
                bigInteger = IpUtils.translateToV6No(ip);
            }
            return CsvUtils.findLocationByIp(bigInteger, fileByteArray);
        } catch (Exception e) {
            log.error("CsvIp2Repository queryCity error:{}", e);
            return new IpResultPO();
        }

    }
}
