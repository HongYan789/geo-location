package com.hongyan.study.geolocation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * This class is used to store the geolocation data that is returned by the IP2Location class.
 * <p>
 * <b>Requirements:</b> Java SDK 1.4 or later<br>
 * <p>
 * Copyright (c) 2002-2021 IP2Location.com
 * <p>
 *
 * @author IP2Location.com
 * @version 8.6.0
 */
@Data
public class IpResultPO implements Serializable {

    private static final String NOT_SUPPORTED = "Not_Supported";
    /**
     * ipaddress
     */
    private String ipAddress;
    /**
     * two-character country code based on ISO 3166.
     */
    private String countryShort;
    /**
     * country name based on ISO 3166.
     */
    private String countryLong;
    /**
     * region or state name.
     */
    private String region;
    /**
     * city name.
     */
    private String city;
    /**
     * Internet Service Provider (ISP) name.
     */
    private String isp;
    /**
     * city latitude.
     */
    private String latitude;
    /**
     * city longitude.
     */
    private String longitude;
    /**
     * IP internet domain name associated to IP address range.
     */
    private String domain;
    /**
     * ZIP/Postal code.
     */
    private String zipcode;
    /**
     * internet connection speed (DIAL) DIAL-UP,(DSL) DSL/CABLE or(COMP) COMPANY
     */
    private String netSpeed;
    /**
     * UTC time zone.
     */
    private String timezone;
    /***
     * IDD prefix to call the city from another country.
     */
    private String iddCode;
    /**
     * e varying length number assigned to geographic areas for call between cities.
     */
    private String areaCode;
    /**
     * special code to identify the nearest weather observation station.
     */
    private String weatherStationCode;
    /**
     * name of the nearest weather observation station.
     */
    private String weatherStationName;
    /**
     * mobile country code.
     */
    private String mcc;
    /**
     * mobile network code.
     */
    private String mnc;
    /**
     * mobile brand.
     */
    private String mobileBrand;
    /**
     * city elevation.
     */
    private String elevation;
    /**
     * usage type.
     */
    private String usageType;
    /**
     * address type.
     */
    private String addressType;
    /**
     * IAB category.
     */
    private String category;
    /**
     * status code of query.
     */
    private String status;
    /**
     * component delay.
     */
    private boolean delay = false;
    /**
     * component version.
     */
    private String version = "Version 8.6.0";
}
