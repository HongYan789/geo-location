package com.hongyan.study.geolocation.exception;

/**
 * @author zy
 * @date Created in 2023/8/16 4:52 PM
 * @description
 */
public class IpException extends Exception {
    private static final long serialVersionUID = 4495714680349884838L;

    public IpException(String info) {
        super(info);
    }

    public IpException(Throwable res) {
        super(res);
    }

    public IpException(String info, Throwable res) {
        super(info, res);
    }
}
