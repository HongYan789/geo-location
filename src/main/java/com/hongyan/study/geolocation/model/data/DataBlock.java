package com.hongyan.study.geolocation.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 3:26 PM
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBlock  implements Serializable {

    private int cityId;
    private String region;
    private int dataPtr;

    public IpData getData() {
        return new IpData(this.cityId, this.region, this.dataPtr);
    }

}
