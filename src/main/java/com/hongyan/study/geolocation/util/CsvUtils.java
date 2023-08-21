package com.hongyan.study.geolocation.util;

import com.hongyan.study.geolocation.model.IpResultPO;
import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/21 3:46 PM
 * @description csv文件解析util
 */
@Slf4j
public class CsvUtils {
    @SneakyThrows
    public static IpResultPO findLocationByIp(BigInteger ip, byte[] csvBytes)  {
        //byte[] 转 CSVReader
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvBytes);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //解析 io 流数据
        try (CSVReader reader = new CSVReader(bufferedReader)) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                BigInteger startIp = new BigInteger(row[0]);
                BigInteger endIp = new BigInteger(row[1]);

                if (startIp.compareTo(ip) <= 0 && endIp.compareTo(ip) >= 0) {
                    IpResultPO result = new IpResultPO();
                    result.setCity(row[5]);
                    result.setCountryLong(row[3]);
                    result.setRegion(row[4]);
                    return result; // 返回国家信息，你可以根据需求返回其他字段
                }
            }
        }
        return new IpResultPO(); // 没有匹配的记录
    }
}
