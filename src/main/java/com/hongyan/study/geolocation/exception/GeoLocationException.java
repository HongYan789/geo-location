/*
 * Copyright (C), 2008-2021, paraview All Rights Reserved.
 */
package com.hongyan.study.geolocation.exception;

/**
 * @author zy
 * @version : GeoLocationException.java, v 1.0
 */
public class GeoLocationException extends RuntimeException {

    /**
     * ip地址
     */
    protected final String ip;

    public GeoLocationException(String ip, String message) {
        super(message);
        this.ip = ip;
    }

    public GeoLocationException(String ip, String message, Throwable cause) {
        super(message, cause);
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
