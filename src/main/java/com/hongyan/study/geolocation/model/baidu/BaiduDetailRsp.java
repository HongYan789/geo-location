package com.hongyan.study.geolocation.model.baidu;

import lombok.Data;

@Data
public class BaiduDetailRsp {

    private String code;//城市码
    private String region;//地区
    private String city;//城市
    private String district;//区
    private double lng = 0;//经度
    private double lat = 0;//纬度
}
