package com.hongyan.study.geolocation.autoconfigure;

import com.hongyan.study.geolocation.config.StoreTypeCondition;
import com.hongyan.study.geolocation.repository.Ip2Repository;
import com.hongyan.study.geolocation.repository.impl.BaiduIp2Repository;
import com.hongyan.study.geolocation.repository.impl.BinIp2Repository;
import com.hongyan.study.geolocation.repository.impl.CsvIp2Repository;
import com.hongyan.study.geolocation.repository.impl.DataIp2Repository;
import com.hongyan.study.geolocation.repository.impl.JdbcIp2Repository;
import com.hongyan.study.geolocation.service.IpLocationService;
import com.hongyan.study.geolocation.service.impl.Ip2LocationServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zy
 * @version : GeoLocationAutoConfiguration.java, v 1.0
 */
@Configuration
@EnableConfigurationProperties(GeoLocationProperties.class)
@Import(DataSourceConfig.class)
//@Conditional(StoreTypeCondition.class)
public class GeoLocationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IpLocationService.class)
    public IpLocationService ip2LocationService() {
        return new Ip2LocationServiceImpl();
    }

    @Bean
//    @ConditionalOnMissingBean(Ip2Repository.class)
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "bin", matchIfMissing = true)
    public Ip2Repository binIp2Repository() {
        return new BinIp2Repository();
    }

    @Bean
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "csv")
    public Ip2Repository csvIp2Repository() {
        return new CsvIp2Repository();
    }

    @Bean
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "db")
    public Ip2Repository jdbcIp2Repository() {
        return new JdbcIp2Repository();
    }

    @Bean
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "data")
    public Ip2Repository dataIp2Repository() {
        return new DataIp2Repository();
    }

    @Bean
    @ConditionalOnProperty(value = "geo.storetype", havingValue = "baidu")
    public Ip2Repository baiduIp2Repository() {
        return new BaiduIp2Repository();
    }

}
