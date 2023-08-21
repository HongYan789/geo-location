package com.hongyan.study.geolocation.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 4:55 PM
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SptrData implements Serializable {

    private long ip;
    private int sptr;
    private int eptr;
}
