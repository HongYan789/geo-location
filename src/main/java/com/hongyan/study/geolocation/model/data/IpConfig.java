package com.hongyan.study.geolocation.model.data;

import com.hongyan.study.geolocation.exception.IpException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 4:50 PM
 * @description
 */
@Data
@AllArgsConstructor
public class IpConfig implements Serializable {

    public static final int HEAD_SIZE = 8;
    private int totalHeaderSize;
    private int indexBlockSize;

    public IpConfig(int totalHeaderSize) throws IpException {
        if (totalHeaderSize % 8 != 0) {
            throw new IpException("totalHeaderSize must be times of 8");
        } else {
            this.totalHeaderSize = totalHeaderSize;
            this.indexBlockSize = 8192;
        }
    }

    public IpConfig() throws IpException {
        this(16384);
    }
}
