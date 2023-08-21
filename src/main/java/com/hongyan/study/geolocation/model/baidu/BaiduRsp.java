package com.hongyan.study.geolocation.model.baidu;

import lombok.Data;

@Data
public class BaiduRsp {
    private String requestId;//请求id
    private int code;//响应码，0：成功
    private String message;//描述
    private BaiduDataRsp data;

    public boolean isSuccess(){
        if(0!=code){
            return false;
        }
        return true;
    }
}
