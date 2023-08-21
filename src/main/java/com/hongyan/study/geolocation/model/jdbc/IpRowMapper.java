/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.model.jdbc;

import com.hongyan.study.geolocation.model.IpResultPO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zy
 * @version : IpRowMapper.java, v 1.0
 */
public class IpRowMapper implements RowMapper<IpResultPO> {

    /**
     * City
     */
    private static final int CITY_DB = 6;
    /**
     * City Longitude TimeZone
     */
    private static final int TIMEZONE_DB = 10;

    @Override
    public IpResultPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        IpResultPO result = new IpResultPO();
        int columnCount = rs.getMetaData().getColumnCount();
        if (columnCount >= CITY_DB) {
            result.setCountryShort(rs.getString("country_code"));
            result.setCountryLong(rs.getString("country_name"));
            result.setRegion(rs.getString("region_name"));
            result.setCity(rs.getString("city_name"));
        }
        if (columnCount >= TIMEZONE_DB) {
            result.setLatitude(rs.getString("latitude"));
            result.setLongitude(rs.getString("longitude"));
            result.setZipcode(rs.getString("zipcode"));
            result.setTimezone(rs.getString("timezone"));
        }
        return result;
    }
}
