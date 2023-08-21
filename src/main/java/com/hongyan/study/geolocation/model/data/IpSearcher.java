package com.hongyan.study.geolocation.model.data;

import com.hongyan.study.geolocation.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * @author zy
 * @date Created in 2023/8/16 4:53 PM
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpSearcher {
    public static final int BTREE_ALGORITHM = 1;
    public static final int BINARY_ALGORITHM = 2;
    public static final int MEMORY_ALGORITYM = 3;
    public static final int SIZE = 8;
    private IpConfig ipConfig = null;
    private RandomAccessFile raf = null;
    private long[] headerSip = null;
    private int[] headerPtr = null;
    private int headerLength;
    private long firstIndexPtr = 0L;
    private long lastIndexPtr = 0L;
    private int totalIndexBlocks = 0;
    private byte[] dbBinStr = null;

    public IpSearcher(IpConfig ipConfig, File dbFile) throws FileNotFoundException {
        this.ipConfig = ipConfig;
        this.raf = new RandomAccessFile(dbFile, "r");
    }

    public IpSearcher(IpConfig ipConfig, byte[] dbBinStr) {
        this.ipConfig = ipConfig;
        this.dbBinStr = dbBinStr;
        this.firstIndexPtr = Utils.getIntLong(dbBinStr, 0);
        this.lastIndexPtr = Utils.getIntLong(dbBinStr, 4);
        this.totalIndexBlocks = (int)((this.lastIndexPtr - this.firstIndexPtr) / (long)IndexBlock.getIndexBlockLength()) + 1;
    }

    public DataBlock memorySearch(long ip) throws IOException {
        int blen = IndexBlock.getIndexBlockLength();
        if (this.dbBinStr == null) {
            this.dbBinStr = new byte[(int)this.raf.length()];
            this.raf.seek(0L);
            this.raf.readFully(this.dbBinStr, 0, this.dbBinStr.length);
            this.firstIndexPtr = Utils.getIntLong(this.dbBinStr, 0);
            this.lastIndexPtr = Utils.getIntLong(this.dbBinStr, 4);
            this.totalIndexBlocks = (int)((this.lastIndexPtr - this.firstIndexPtr) / (long)blen) + 1;
        }

        int l = 0;
        int h = this.totalIndexBlocks;
        long dataptr = 0L;

        int m;
        int p;
        while(l <= h) {
            m = l + h >> 1;
            p = (int)(this.firstIndexPtr + (long)(m * blen));
            long sip = Utils.getIntLong(this.dbBinStr, p);
            if (ip < sip) {
                h = m - 1;
            } else {
                long eip = Utils.getIntLong(this.dbBinStr, p + 4);
                if (ip <= eip) {
                    dataptr = Utils.getIntLong(this.dbBinStr, p + 8);
                    break;
                }

                l = m + 1;
            }
        }

        if (dataptr == 0L) {
            return null;
        } else {
            m = (int)(dataptr >> 24 & 255L);
            p = (int)(dataptr & 16777215L);
            int cityId = (int)Utils.getIntLong(this.dbBinStr, p);
            String region = new String(this.dbBinStr, p + 4, m - 4, StandardCharsets.UTF_8);
            return new DataBlock(cityId, region, p);
        }
    }

    public DataBlock memorySearch(String ip) throws IOException {
        return this.memorySearch(Utils.ip2long(ip));
    }

    public DataBlock getByIndexPtr(long ptr) throws IOException {
        this.raf.seek(ptr);
        byte[] buffer = new byte[12];
        this.raf.readFully(buffer, 0, buffer.length);
        long extra = Utils.getIntLong(buffer, 8);
        int dataLen = (int)(extra >> 24 & 255L);
        int dataPtr = (int)(extra & 16777215L);
        this.raf.seek((long)dataPtr);
        byte[] data = new byte[dataLen];
        this.raf.readFully(data, 0, data.length);
        int cityId = (int)Utils.getIntLong(data, 0);
        String region = new String(data, 4, data.length - 4, StandardCharsets.UTF_8);
        return new DataBlock(cityId, region, dataPtr);
    }

    private SptrData getSptr(long ip, int sptr, int eptr) {
        int l = 0;
        int h = this.headerLength;

        while(l <= h) {
            int m = l + h >> 1;
            if (ip == this.headerSip[m]) {
                if (m > 0) {
                    sptr = this.headerPtr[m - 1];
                    eptr = this.headerPtr[m];
                } else {
                    sptr = this.headerPtr[m];
                    eptr = this.headerPtr[m + 1];
                }
                break;
            }

            if (ip < this.headerSip[m]) {
                if (m == 0) {
                    sptr = this.headerPtr[m];
                    eptr = this.headerPtr[m + 1];
                    break;
                }

                if (ip > this.headerSip[m - 1]) {
                    sptr = this.headerPtr[m - 1];
                    eptr = this.headerPtr[m];
                    break;
                }

                h = m - 1;
            } else {
                if (m == this.headerLength - 1) {
                    sptr = this.headerPtr[m - 1];
                    eptr = this.headerPtr[m];
                    break;
                }

                if (ip <= this.headerSip[m + 1]) {
                    sptr = this.headerPtr[m];
                    eptr = this.headerPtr[m + 1];
                    break;
                }

                l = m + 1;
            }
        }

        return new SptrData(ip, sptr, eptr);
    }

    public DataBlock btreeSearch(long ip) throws IOException {
        this.checkBtree();
        if (ip == this.headerSip[0]) {
            return this.getByIndexPtr((long)this.headerPtr[0]);
        } else if (ip == this.headerSip[this.headerLength - 1]) {
            return this.getByIndexPtr((long)this.headerPtr[this.headerLength - 1]);
        } else {
            int h = this.headerLength;
            Integer sptr = 0;
            Integer eptr = 0;
            SptrData sptrData = this.getSptr(ip, sptr, eptr);
            ip = sptrData.getIp();
            sptr = sptrData.getSptr();
            eptr = sptrData.getEptr();
            if (sptr == 0) {
                return null;
            } else {
                int blockLen = eptr - sptr;
                int blen = IndexBlock.getIndexBlockLength();
                byte[] iBuffer = new byte[blockLen + blen];
                this.raf.seek((long)sptr);
                this.raf.readFully(iBuffer, 0, iBuffer.length);
                int l = 0;
                h = blockLen / blen;
                long dataptr = 0L;

                int m;
                int p;
                while(l <= h) {
                    m = l + h >> 1;
                    p = m * blen;
                    long sip = Utils.getIntLong(iBuffer, p);
                    if (ip < sip) {
                        h = m - 1;
                    } else {
                        long eip = Utils.getIntLong(iBuffer, p + 4);
                        if (ip <= eip) {
                            dataptr = Utils.getIntLong(iBuffer, p + 8);
                            break;
                        }

                        l = m + 1;
                    }
                }

                if (dataptr == 0L) {
                    return null;
                } else {
                    m = (int)(dataptr >> 24 & 255L);
                    p = (int)(dataptr & 16777215L);
                    this.raf.seek((long)p);
                    byte[] data = new byte[m];
                    this.raf.readFully(data, 0, data.length);
                    int cityId = (int)Utils.getIntLong(data, 0);
                    String region = new String(data, 4, data.length - 4, StandardCharsets.UTF_8);
                    return new DataBlock(cityId, region, p);
                }
            }
        }
    }

    private void checkBtree() throws IOException {
        if (this.headerSip == null) {
            this.raf.seek(8L);
            byte[] b = new byte[this.ipConfig.getTotalHeaderSize()];
            this.raf.readFully(b, 0, b.length);
            int len = b.length >> 3;
            int idx = 0;
            this.headerSip = new long[len];
            this.headerPtr = new int[len];

            for(int i = 0; i < b.length; i += 8) {
                long startIp = Utils.getIntLong(b, i);
                long dataPtr = Utils.getIntLong(b, i + 4);
                if (dataPtr == 0L) {
                    break;
                }

                this.headerSip[idx] = startIp;
                this.headerPtr[idx] = (int)dataPtr;
                ++idx;
            }

            this.headerLength = idx;
        }

    }

    public DataBlock btreeSearch(String ip) throws IOException {
        return this.btreeSearch(Utils.ip2long(ip));
    }

    public DataBlock binarySearch(long ip) throws IOException {
        int blen = IndexBlock.getIndexBlockLength();
        if (this.totalIndexBlocks == 0) {
            this.raf.seek(0L);
            byte[] superBytes = new byte[8];
            this.raf.readFully(superBytes, 0, superBytes.length);
            this.firstIndexPtr = Utils.getIntLong(superBytes, 0);
            this.lastIndexPtr = Utils.getIntLong(superBytes, 4);
            this.totalIndexBlocks = (int)((this.lastIndexPtr - this.firstIndexPtr) / (long)blen) + 1;
        }

        int l = 0;
        int h = this.totalIndexBlocks;
        byte[] buffer = new byte[blen];
        long dataptr = 0L;

        int m;
        while(l <= h) {
            m = l + h >> 1;
            this.raf.seek(this.firstIndexPtr + (long)(m * blen));
            this.raf.readFully(buffer, 0, buffer.length);
            long sip = Utils.getIntLong(buffer, 0);
            if (ip < sip) {
                h = m - 1;
            } else {
                long eip = Utils.getIntLong(buffer, 4);
                if (ip <= eip) {
                    dataptr = Utils.getIntLong(buffer, 8);
                    break;
                }

                l = m + 1;
            }
        }

        if (dataptr == 0L) {
            return null;
        } else {
            m = (int)(dataptr >> 24 & 255L);
            int dataPtr = (int)(dataptr & 16777215L);
            this.raf.seek((long)dataPtr);
            byte[] data = new byte[m];
            this.raf.readFully(data, 0, data.length);
            int cityId = (int)Utils.getIntLong(data, 0);
            String region = new String(data, 4, data.length - 4, StandardCharsets.UTF_8);
            return new DataBlock(cityId, region, dataPtr);
        }
    }

    public DataBlock binarySearch(String ip) throws IOException {
        return this.binarySearch(Utils.ip2long(ip));
    }

    public IpConfig getIpConfig() {
        return this.ipConfig;
    }

    public void close() throws IOException {
        this.headerSip = null;
        this.headerPtr = null;
        this.dbBinStr = null;
        if (this.raf != null) {
            this.raf.close();
        }

    }
}
