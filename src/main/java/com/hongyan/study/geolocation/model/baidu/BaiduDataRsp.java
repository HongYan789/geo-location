package com.hongyan.study.geolocation.model.baidu;

import lombok.Data;

@Data
public class BaiduDataRsp {

    private String ip;//ip
    private String nation;//国家
    private BaiduDetailRsp details;
}
