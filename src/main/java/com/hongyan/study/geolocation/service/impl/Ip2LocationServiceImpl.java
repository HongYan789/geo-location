package com.hongyan.study.geolocation.service.impl;

import com.hongyan.study.geolocation.config.IpType;
import com.hongyan.study.geolocation.exception.GeoLocationException;
import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.model.Location;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.service.IpLocationService;
import com.hongyan.study.geolocation.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * 基于ip2location的商业化实现
 *
 * @author zy
 * @version : Ip2LocationServiceImpl.java, v 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "ipCache")
public class Ip2LocationServiceImpl implements IpLocationService {
    /** ip 4 */
    private static final int V4 = 4;
    /** ip 6 */
    private static final int V6 = 6;
    private static final String G = "-";

    @Autowired
    private Ip2Repository ip2Repository;

    @Override
    public Location getLocation(String ip) {
        if (StringUtils.isEmpty(ip)) {
            throw new GeoLocationException(ip, "no ip address found.Got " + ip);
        }
        BigInteger bigInteger;
        int type;

        if (IpUtils.isIpV4(ip)) {
            bigInteger = IpUtils.translateToV4No(ip);
            type = V4;
        } else {
            bigInteger = IpUtils.translateToV6No(ip);
            type = V6;
        }
        Location location = new Location();
        if (!BigInteger.ZERO.equals(bigInteger)) {
            //repository 读取
            IpResultPO result = ip2Repository.queryCity(ip);
            location.setIp(ip);
            location.setIpType(IpType.of(type));
            location.setCountry(result.getCountryLong());
            location.setRegion(result.getRegion());
            location.setCity(result.getCity());
            location.setLatitude(String.valueOf(result.getLatitude()));
            location.setLongitude(String.valueOf(result.getLongitude()));
        }
        return location;
    }

    @Cacheable(key = "#ip", sync = true, condition = "#ip != null")
    @Override
    public Location getLocationExternel(String ip) {
        Location location = new Location();
        location.setIp(ip);
        location.setIpType(IpType.of(IpUtils.isIpV4(ip) ? V4 : V6));
        try {
            //repository 读取
            IpResultPO result = ip2Repository.queryCity(ip);
            location.setCountry(result.getCountryLong());
            location.setRegion(result.getRegion());
            location.setCity(result.getCity());
            location.setLatitude(String.valueOf(result.getLatitude()));
            location.setLongitude(String.valueOf(result.getLongitude()));
        }catch (Exception e){
            log.error("ip查询异常：" + e.getMessage());
            location.setCountry(G);
            location.setRegion(G);
            location.setCity(G);
            location.setLatitude(G);
            location.setLongitude(G);
        }
        return location;
    }
}
