/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.util;

import com.hongyan.study.geolocation.exception.GeoLocationException;
import com.hongyan.study.geolocation.model.data.DataBlock;
import com.hongyan.study.geolocation.model.data.IpConfig;
import com.hongyan.study.geolocation.model.data.IpData;
import com.hongyan.study.geolocation.model.data.IpSearcher;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * IP工具类
 * 用于快速判定ip类型
 *
 * @author zy
 * @version : IpTypeUtils.java, v 1.0
 */
@UtilityClass
public class IpUtils {

    /**
     * ipv4 匹配器
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    /**
     * ip 段
     */
    private static final int IP_PHASE = 3;
    /**
     * 最大值 2^32-1 255:255:255:255
     */
    private static final BigInteger MAX_IPV4_RANGE = new BigInteger("4294967295");
    /**
     * ipv6最大值 2^128-1 FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF
     */
    private static final BigInteger MAX_IPV6_RANGE = new BigInteger("340282366920938463463374607431768211455");
    /**
     * from v6
     */
    private static final BigInteger FROM_6TO4 = new BigInteger("42545680458834377588178886921629466624");
    /**
     * to v6
     */
    private static final BigInteger TO_6TO4 = new BigInteger("42550872755692912415807417417958686719");
    /**
     * from teredo
     */
    private static final BigInteger FROM_TEREDO = new BigInteger("42540488161975842760550356425300246528");
    /**
     * from teredo
     */
    private static final BigInteger TO_TEREDO = new BigInteger("42540488241204005274814694018844196863");
    /**
     * 低32位
     */
    private static final BigInteger LAST_32BITS = new BigInteger("4294967295");

    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);
    private static byte[] FILE_DATA;

    /**
     * 判断当前ip是否是v4
     *
     * @return true是ipv4，false不是ipv4
     */
    public static boolean isIpV4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 将ip地址转换为bigInt数组 V4
     * 组成下表
     * <ul>
     *     <li>:[0]->ip类型,转换位的数字</li>
     * </ul>
     */
    public static BigInteger translateToV4No(String ip) {
        final String[] ipAddressInArray = ip.split("\\.");
        long result = 0;
        long ips;
        for (int x = IP_PHASE; x >= 0; x--) {
            ips = Long.parseLong(ipAddressInArray[3 - x]);
            result |= ips << (x << 3);
        }
        return new BigInteger(String.valueOf(result));
    }

    /**
     * 将ip地址转换为bigInt数组 V6
     * 组成下表
     * <ul>
     *     <li>[0]->ip类型,转换位的数字</li>
     * </ul>
     */
    public static BigInteger translateToV6No(String ip) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(ip);
            final byte[] byteArr = inetAddress.getAddress();
            if (inetAddress instanceof Inet6Address) {
                return new BigInteger(1, byteArr);
            }
        } catch (UnknownHostException e) {
            throw new GeoLocationException(ip, "incorrect ipv6. Got" + ip);
        }
        return BigInteger.ZERO;
    }

    public static IpData getCityInfo(byte[] data, String ip) throws IOException {
        int algorithm = 3;

        try {
            IpConfig config = new IpConfig();
            IpSearcher searcher = new IpSearcher(config, data);
            DataBlock dataBlock;
            switch (algorithm) {
                case 1:
                    dataBlock = searcher.btreeSearch(ip);
                    break;
                case 2:
                    dataBlock = searcher.binarySearch(ip);
                    break;
                case 3:
                    dataBlock = searcher.memorySearch(ip);
                    break;
                default:
                    return null;
            }

            if (!Utils.isIpAddress(ip)) {
                log.error("Error: Invalid ip address");
                return null;
            } else {
                return dataBlock.getData();
            }
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
            return null;
        }
    }

    public static IpData getCityInfo(String path, String ip) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            log.error("Error: Invalid data.db file, filePath：" + file.getPath());
            return null;
        } else {
            int algorithm = 1;

            try {
                IpConfig config = new IpConfig();
                IpSearcher searcher = new IpSearcher(config, file);
                DataBlock dataBlock;
                switch (algorithm) {
                    case 1:
                        dataBlock = searcher.btreeSearch(ip);
                        break;
                    case 2:
                        dataBlock = searcher.binarySearch(ip);
                        break;
                    case 3:
                        dataBlock = searcher.memorySearch(ip);
                        break;
                    default:
                        return null;
                }

                if (!Utils.isIpAddress(ip)) {
                    log.error("Error: Invalid ip address");
                    return null;
                } else {
                    return dataBlock.getData();
                }
            } catch (Exception var8) {
                log.error(var8.getMessage(), var8);
                return null;
            }
        }
    }

    public static byte[] file2byte(InputStream inputStream) {
        try {
            byte[] data = new byte[inputStream.available()];
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.read(data);
            bis.close();
            return data;
        } catch (Exception var3) {
            log.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static IpData getCityInfo(String ip) throws IOException {
        if (Objects.isNull(FILE_DATA)) {
            InputStream inputStream = IpUtils.class.getClassLoader().getResourceAsStream("data.db");
            log.info("记载地理信息文件:" + Objects.isNull(inputStream));
            log.info("记载地理信息文件数据:" + inputStream.available());
            FILE_DATA = file2byte(inputStream);
        }

        return getCityInfo(FILE_DATA, ip);
    }

}
