package com.hongyan.study.geolocation;

import com.alibaba.fastjson.JSON;
import com.hongyan.study.geolocation.autoconfigure.GeoLocationAutoConfiguration;
import com.hongyan.study.geolocation.model.Location;
import com.hongyan.study.geolocation.model.data.IpData;
import com.hongyan.study.geolocation.service.IpLocationService;
import com.hongyan.study.geolocation.util.FileUtil;
import com.hongyan.study.geolocation.util.IpBaseUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DataSourceAutoConfiguration.class, JdbcTemplateConfig.class, GeoLocationAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class GeoLocationApplicationTests {

    @Autowired
    private IpLocationService ipLocationService;

    @Test
    @DisplayName("数据通过jdbc获取")
    public void testJdbcRepository() {
        Location location = ipLocationService.getLocation("8.8.8.8");
        System.out.println(JSON.toJSONString(location));
    }

    @Test
    @DisplayName("在线查询ip地址")
    public void queryIpInfo() throws IOException {
        //湖南,长沙:175.10.191.255
        //湖北,武汉:117.136.52.39，117.136.52.81,117.136.52.11
        //北京:180.149.130.16
        //117.136.23.151，119.103.159.228,117.136.23.245
        Location location = ipLocationService.getLocationExternel("10.10.2.92");
        System.out.println(JSON.toJSONString(location));
    }

    @Test
    @DisplayName("离线查询ip地址")
    public void queryIpInfo2() throws IOException {
        byte[] b = FileUtil.getByteArray("data.db");
        IpData location = IpBaseUtils.getCityInfo(b,"117.136.23.245");
        System.out.println(JSON.toJSONString(location));
    }

    @Test
    @DisplayName("测试查询ip地址")
    public void testLocationExternel() {
        String ip = "3.79.255.255";
        Location location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"美国","region":"华盛顿","city":"0","ip":"3.79.255.255","ipType":"IP_V4"}
        ip = "8.37.43.255";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"美国","region":"北卡罗来纳","city":"夏洛特","ip":"8.37.43.255","ipType":"IP_V4"}
        ip = "13.33.171.255";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"印度","region":"泰米尔纳德","city":"钦奈","ip":"13.33.171.255","ipType":"IP_V4"}
        ip = "2.16.165.255";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"德国","region":"法兰克福","city":"法兰克福","ip":"2.16.165.255","ipType":"IP_V4"}
        ip = "4.68.72.170";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"美国","region":"0","city":"0","ip":"4.68.72.170","ipType":"IP_V4"}
        ip = "27.16.150.25";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        //location:{"country":"中国","region":"湖北省","city":"武汉市","ip":"27.16.150.25","ipType":"IP_V4"}
        ip = "2.22.81.255";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        ip = "110.234.143.42";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
        ip = "8.8.8.8";
        location = ipLocationService.getLocationExternel(ip);
        System.out.println(JSON.toJSONString(location));
    }

}
