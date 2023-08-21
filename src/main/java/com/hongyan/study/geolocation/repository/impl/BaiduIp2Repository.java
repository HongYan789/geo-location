package com.hongyan.study.geolocation.repository.impl;

import com.hongyan.study.geolocation.autoconfigure.GeoLocationProperties;
import com.hongyan.study.geolocation.config.KeyConfig;
import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.model.baidu.BaiduRsp;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.send.Baidu;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/15 2:01 PM
 * @description 百度在线查询location实现
 */
@Slf4j
@Repository
public class BaiduIp2Repository implements Ip2Repository {

    @Autowired
    private GeoLocationProperties geoLocationProperties;
    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("BaiduIp2Repository queryCity ip:{}", ip);
        IpResultPO result = new IpResultPO();
        if (!geoLocationProperties.getLibraryType()) {
            log.warn("geoLocationProperties.libraryType is false");
            return result;
        }
        if(StringUtils.isEmpty(geoLocationProperties.getAccessKey())){
            geoLocationProperties.setAccessKey(KeyConfig.AccessKey);
        }
        if(StringUtils.isEmpty(geoLocationProperties.getSecretkey())){
            geoLocationProperties.setSecretkey(KeyConfig.Secretkey);
        }
        BaiduRsp rsp = Baidu.send(ip, geoLocationProperties.getAccessKey(), geoLocationProperties.getSecretkey());

        result.setCity(rsp.getData().getDetails().getCity());
        result.setCountryLong(rsp.getData().getNation());
        result.setRegion(rsp.getData().getDetails().getRegion());
        result.setLatitude(String.valueOf(rsp.getData().getDetails().getLat()));
        result.setLongitude(String.valueOf(rsp.getData().getDetails().getLng()));
        return result;
    }

    /**
     * 对地级市特殊处理
     * 百度查不到 -> 本地查不到-> 返回默认省份
     * @param rsp
     * @return
     */
//    private String getCity(BaiduRsp rsp) throws Exception {
//        String city;
//        if(!StringUtils.isEmpty(rsp.getData().getDetails().getCity())){
//            city = rsp.getData().getDetails().getCity();
//        } else {
//            IpData ipData = IpBaseUtils.getCityInfo(fileByteArray, rsp.getData().getIp());
//            if(FileUtil.isChineseStr(ipData.getCity())){
//                city =ipData.getCity();
//            } else {
//                city = rsp.getData().getDetails().getRegion();
//            }
//        }
//        return city;
//    }
}
