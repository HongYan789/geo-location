package com.hongyan.study.geolocation.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 4:51 PM
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpData implements Serializable {
    private int cityId;
    private String region;
    private int dataPtr;
    private String country;
    private String city;
    private String province;
    private String service;

    public IpData(int cityId, String region, int dataPtr) {
        this.cityId = cityId;
        this.region = region;
        this.dataPtr = dataPtr;
        if (region != null && region.length() > 0) {
            String[] data = region.split("\\|");
            this.country = data[0];
            this.province = data[2];
            this.city = data[3];
            this.service = data[4];
        }

    }
}
