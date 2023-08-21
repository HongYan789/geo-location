/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.config;

/**
 * @author zy
 * @version : IpType.java, v 1.0
 */
public enum IpType {

    /**
     * ipv4
     */
    IP_V4(4),
    /**
     * ipv6
     */
    IP_V6(6);
    /**
     * inet版本
     */
    private final int version;

    IpType(int version) {
        this.version = version;
    }

    public static IpType of(int ipType) {
        for (IpType value : values()) {
            if (value.version == ipType) {
                return value;
            }
        }
        return null;
    }
}
