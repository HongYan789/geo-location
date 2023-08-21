package com.hongyan.study.geolocation.util;

/**
 * @author zy
 * @date Created in 2023/8/16 5:00 PM
 * @description
 */
public class Utils {
    public static final char DOT = '.';
    public static final int IP_SECTION = 4;

    public Utils() {
    }

    public static void write(byte[] b, int offset, long v, int bytes) {
        for(int i = 0; i < bytes; ++i) {
            b[offset++] = (byte)((int)(v >>> 8 * i & 255L));
        }

    }

    public static void writeIntLong(byte[] b, int offset, long v) {
        b[offset++] = (byte)((int)(v >> 0 & 255L));
        b[offset++] = (byte)((int)(v >> 8 & 255L));
        b[offset++] = (byte)((int)(v >> 16 & 255L));
        b[offset] = (byte)((int)(v >> 24 & 255L));
    }

    public static long getIntLong(byte[] b, int offset) {
        return (long)b[offset++] & 255L | (long)(b[offset++] << 8) & 65280L | (long)(b[offset++] << 16) & 16711680L | (long)(b[offset] << 24) & 4278190080L;
    }

    public static int getInt3(byte[] b, int offset) {
        return b[offset++] & 255 | b[offset++] & '\uff00' | b[offset] & 16711680;
    }

    public static int getInt2(byte[] b, int offset) {
        return b[offset++] & 255 | b[offset] & '\uff00';
    }

    public static int getInt1(byte[] b, int offset) {
        return b[offset] & 255;
    }

    public static long ip2long(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return 0L;
        } else {
            int p1 = Integer.valueOf(p[0]) << 24 & -16777216;
            int p2 = Integer.valueOf(p[1]) << 16 & 16711680;
            int p3 = Integer.valueOf(p[2]) << 8 & '\uff00';
            int p4 = Integer.valueOf(p[3]) << 0 & 255;
            return (long)(p1 | p2 | p3 | p4) & 4294967295L;
        }
    }

    public static String long2ip(long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append(ip >> 24 & 255L).append('.').append(ip >> 16 & 255L).append('.').append(ip >> 8 & 255L).append('.').append(ip >> 0 & 255L);
        return sb.toString();
    }

    public static boolean isIpAddress(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return false;
        } else {
            String[] var2 = p;
            int var3 = p.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String pp = var2[var4];
                if (pp.length() > 3) {
                    return false;
                }

                int val = Integer.valueOf(pp);
                if (val > 255) {
                    return false;
                }
            }

            return true;
        }
    }
}
