/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.repository.impl;

import com.hongyan.study.geolocation.model.IpResultPO;
import com.hongyan.study.geolocation.model.jdbc.IpRowMapper;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigInteger;

/**
 * @author zy
 * @description db离线查询location实现
 * @version : JdbcIp2Repository.java, v 1.0
 */
@Slf4j
@Repository
public class JdbcIp2Repository implements Ip2Repository {

    /**
     * 为了减少因导入不同类型的csv文件类型导致对程序的影响，这里使用了*.
     * 即不管导入csv只有4列或者12列，sql语句是不变的
     */
    private static final String QUERY_CITY_DEF = "select * from ip2_location where ip_from <= ? and  ? <= ip_to limit 1";

    @Resource(name = "ipJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("JdbcIp2Repository queryCity ip:{}", ip);
        try {
            BigInteger bigInteger = null;

            if (IpUtils.isIpV4(ip)) {
                bigInteger = IpUtils.translateToV4No(ip);
            } else {
                bigInteger = IpUtils.translateToV6No(ip);
            }
            return jdbcTemplate.queryForObject(QUERY_CITY_DEF, new IpRowMapper(), bigInteger.longValue(), bigInteger.longValue());
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("JdbcIp2Repository queryCity error:{}", e);
            return new IpResultPO();
        }
    }

}
