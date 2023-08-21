package com.hongyan.study.geolocation.repository.impl;

import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.model.data.IpData;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.util.FileUtil;
import com.hongyan.study.geolocation.util.IpBaseUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/15 2:01 PM
 * @description 纯真数据库离线查询location实现
 */
@Slf4j
@Repository
public class DataIp2Repository implements Ip2Repository {

    private static byte[] fileByteArray = null;

    static {
        try {
            fileByteArray = FileUtil.getByteArray("data.db");
        } catch (IOException e) {
            log.error("init data.db error");
        }
    }
    @SneakyThrows
    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("DataIp2Repository queryCity ip:{}", ip);
        IpResultPO result = new IpResultPO();
        IpData ipData = IpBaseUtils.getCityInfo(fileByteArray, ip);
        result.setCity(ipData.getCity());
        result.setCountryLong(ipData.getCountry());
        result.setRegion(ipData.getProvince());
        return result;
    }
}
