package com.hongyan.study.geolocation.service;

import com.hongyan.study.geolocation.model.Location;

/**
 * IP解析服务
 */
public interface IpLocationService {

    /**
     * 通过ip获取物理地区信息
     * @version v1
     * @param ip ip地址
     * @return 地区信息
     */
    Location getLocation(String ip);

    /**
     * 扩展ip库查询，支持各种类型查询：db、csv、baidu、data、bin     * @param ip
     * @return
     */
    Location getLocationExternel(String ip);

}
