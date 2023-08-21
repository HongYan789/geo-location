package com.hongyan.study.geolocation.model.data;

import com.hongyan.study.geolocation.util.Utils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zy
 * @date Created in 2023/8/16 4:48 PM
 * @description
 */
@Data
public class IndexBlock implements Serializable {
    private static final int LENGTH = 12;
    private long startIp;
    private long endIp;
    private int dataPtr;
    private int dataLen;

    public static int getIndexBlockLength() {
        return 12;
    }

    public byte[] getBytes() {
        byte[] b = new byte[12];
        Utils.writeIntLong(b, 0, this.startIp);
        Utils.writeIntLong(b, 4, this.endIp);
        long mix = (long)this.dataPtr | (long)(this.dataLen << 24) & 4278190080L;
        Utils.writeIntLong(b, 8, mix);
        return b;
    }
}
