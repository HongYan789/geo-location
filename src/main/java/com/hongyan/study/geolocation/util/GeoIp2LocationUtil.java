package com.hongyan.study.geolocation.util;


import com.hongyan.study.geolocation.model.bin.IP2Location;
import com.hongyan.study.geolocation.model.bin.IPResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author zy
 * @description bin文件读取ip解析位置工具类
 * @date 2022/02/23 3:59 PM
 */
@Slf4j
public class GeoIp2LocationUtil {


    public static final String IP2LOCATION_FILE_NAME = "IP2LOCATION-LITE-DB11.BIN";

    //ip2location
    private static IP2Location ip2Location;

    /**
     * 增加一个是否正在初始化拷贝BIN文件中的锁标识,防止多线程下拷贝多次
     */
    private volatile static boolean isCopyed = false;

    /**
     * 初始化ip2Location解析的环境
     */
    public static void initGeo() {
        try {
            if (ip2Location == null) {
                // 如果有其他线程正在拷贝bin文件时，则直接返回
                if (isCopyed) {
                    return;
                }
                isCopyed = true;
                // 把jar包中的IP2LOCATION-LITE-DB11.BIN文件拷贝到系统工作目录
                String sourceFileName = IP2LOCATION_FILE_NAME;
                String tempPath = FileUtil.getUserWorkDir();
                log.info("System.getProperty(\"user.dir\") is{}", tempPath);

                // 如果没有/结尾，就多加一个/
                if (StringUtils.isNotEmpty(tempPath)
                        && !File.separator.equalsIgnoreCase(tempPath.substring(tempPath.length() - 1))) {
                    tempPath = tempPath + File.separator;
                }

                //geo final name
                String targetFileName = tempPath + IP2LOCATION_FILE_NAME;

                long startTime = System.currentTimeMillis();
                FileUtil.copyJarFile(sourceFileName, targetFileName, true);
                long tookTime = System.currentTimeMillis() - startTime;
                log.info("copy IP2LOCATION-LITE-DB11.BIN to {} is success,Total tookTime {} ms. ", targetFileName, tookTime);

                //ip 2 location
                ip2Location = new IP2Location();
                ip2Location.UseMemoryMappedFile = true;
                ip2Location.IPDatabasePath = targetFileName;
            }
        } catch (Exception e) {
            log.error("Init geo file is error", e);
        }
    }

    /**
     * 根据ip获取位置信息
     *
     * @param ip
     * @return
     */
    public static IPResult ip2Location(String ip) {
        IPResult ipResult = null;
        try {
            if (ip2Location == null) {
                log.debug("ip2Location is null ,start perepre geo env.");
                initGeo();
            }
            // 如果经过一次初始化ip2Location解析的环境后，ip2Location还是为空，则返回为空，不解析ip所属位置了
            if (ip2Location == null) {
                log.error("ip2Location init error ，ip2location is null！");
                return null;
            }
            ipResult = ip2Location.IPQuery(ip);
        } catch (IOException e) {
            log.error("ip2location parse is error", e);
        }
        return ipResult;
    }
}
