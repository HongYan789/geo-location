/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.repository;

import com.hongyan.study.geolocation.model.IpResultPO;

/**
 * @author zy
 * @version : Ip2Repository.java, v 1.0
 */
public interface Ip2Repository {

    /**
     * 查询到城市级 DB3
     *
     * @param ip ip地址
     * @return po对象
     */
    IpResultPO queryCity(String ip);
}
