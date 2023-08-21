package com.hongyan.study.geolocation.model.data;

import com.hongyan.study.geolocation.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 4:47 PM
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderBlock implements Serializable {

    private long indexStartIp;
    private int indexPtr;

    public byte[] getBytes() {
        byte[] b = new byte[8];
        Utils.writeIntLong(b, 0, this.indexStartIp);
        Utils.writeIntLong(b, 4, (long)this.indexPtr);
        return b;
    }
}
