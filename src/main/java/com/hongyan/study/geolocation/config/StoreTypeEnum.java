package com.hongyan.study.geolocation.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/17 10:44 AM
 * @description 存储/读取类型枚举
 */
@AllArgsConstructor
@Getter
public enum StoreTypeEnum {

    DB("db", "数据库"),
    CSV("csv", "csv文件"),
    DATA("data", "data文件"),
    BAIDU("baidu", "百度"),
    BIN("bin", "bin文件");

    private final String code;

    private final String desc;

    /**
     * 根据code获取枚举
     * @param code
     * @return
     */
    public static StoreTypeEnum of(String code) {
        return Stream.of(values()).filter(value -> value.code.equals(code)).findFirst().orElse(null);
    }

    /**
     * 判断是否存在
     * @param code
     * @return
     */
    public static Boolean isExist(String code) {
        return Stream.of(values()).anyMatch(value -> value.code.equals(code));
    }
}
