package com.hongyan.study.geolocation.util;

import com.hongyan.study.geolocation.model.data.IpData;

import java.io.IOException;

/**
 * @author zy
 * @date Created in 2023/8/16 4:59 PM
 * @description
 */
public class IpBaseUtils {
    public IpBaseUtils() {
    }

    public static IpData getCityInfo(byte[] data, String ip) throws IOException {
        return IpUtils.getCityInfo(data, ip);
    }

    public static IpData getCityInfo(String path, String ip) throws IOException {
        return IpUtils.getCityInfo(path, ip);
    }

    public static IpData getCityInfo(String ip) throws IOException {
        return IpUtils.getCityInfo(ip);
    }
}
