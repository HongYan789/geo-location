# IP 地理定位综合解决方案

### IP 地理定位综合解决方案
实现多种模式，来在线/离线解析ip地址，支持的ip地址解析数据来源分别为：db/数据库存储；csv文件存储；纯真ip数据库；baidu在线；ip2location

未来支持的模式：ip2region


### 实现解析模式
1. db/数据库模式
2. csv 模式
3. data 模式
4. baidu/百度模式
5. bin 模式

### 数据来源
1. ip2location
2. ip2location
3. 纯真ip数据库
4. baidu在线接口调用
5. ip2location bin 文件方式


* [CSV文件/BIN文件下载地址](https://lite.ip2location.com/database/db11-ip-country-region-city-latitude-longitude-zipcode-timezone)

### 实现思路
#### 1.引入所需jar包
```xml
<!--csv模式所需jar包 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-csv</artifactId>
            <version>1.8</version>
        </dependency>
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>jsr250-api</artifactId>
            <version>1.0</version>
        </dependency>
        <!--db/jdbc模式所需jdbc jar包-->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.3.20</version>
        </dependency>

        <!--在线库查询 baidu -->
        <dependency>
            <groupId>com.baidubce</groupId>
            <artifactId>api-explorer-sdk</artifactId>
            <version>1.0.4.1</version>
        </dependency>

```

#### 2.引入所需ip data 源文件
在项目 resources 目录下引入 data.sql 文件
在项目 resources 目录下引入 IP2LOCATION-LITE-DB11.BIN 文件


#### 3.引入所需 sql 文件
在项目 resources 目录下引入 db/schema.sql
在项目 resources 目录下引入 db/data.sql

####  4.引入所需 csv 文件
在项目 resources 目录下引入 IP2LOCATION-LITE-DB11.CSV 文件

#### 5. 注入所需baseBean
```java
@Data
@ConfigurationProperties(prefix = "geo")
public class GeoLocationProperties {
    /**
     * 地理位置服务提供商
     * 配置文件有则取配置文件的value
     * 配置文件没有则用默认值：ip2
     */
    @Value("${provider:ip2}")
    private String provider;
    /**
     * 存储类别：db、csv、data、baidu、bin（默认），主要针对于离线库
     */
    private String storetype;
    /**
     * 库类型：在线：true，离线：false
     */
    @Value("${libraryType:false}")
    private Boolean libraryType;
    /**
     * 在线库 accessKey
     */
    private String accessKey;
    /**
     * 在线库 secretkey
     */
    private String secretkey;

}

@Configuration
@EnableConfigurationProperties(GeoLocationProperties.class)
@Import(DataSourceConfig.class)
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

```

#### 6. 新增 IP 地理定位解析入口service
```java
public interface IpLocationService {

    /**
     * 通过ip获取物理地区信息
     * @version v1
     * @param ip ip地址
     * @return 地区信息
     */
    Location getLocation(String ip);

    /**
     * 扩展ip库查询，支持各种类型查询：db、csv、baidu、data、bin     * @param ip
     * @return
     */
    Location getLocationExternel(String ip);

}


@Slf4j
@Service
@CacheConfig(cacheNames = "ipCache")
public class Ip2LocationServiceImpl implements IpLocationService {
    /** ip 4 */
    private static final int V4 = 4;
    /** ip 6 */
    private static final int V6 = 6;
    private static final String G = "-";

    @Autowired
    private Ip2Repository ip2Repository;

    @Override
    public Location getLocation(String ip) {
        if (StringUtils.isEmpty(ip)) {
            throw new GeoLocationException(ip, "no ip address found.Got " + ip);
        }
        BigInteger bigInteger;
        int type;

        if (IpUtils.isIpV4(ip)) {
            bigInteger = IpUtils.translateToV4No(ip);
            type = V4;
        } else {
            bigInteger = IpUtils.translateToV6No(ip);
            type = V6;
        }
        Location location = new Location();
        if (!BigInteger.ZERO.equals(bigInteger)) {
            //repository 读取
            IpResultPO result = ip2Repository.queryCity(ip);
            location.setIp(ip);
            location.setIpType(IpType.of(type));
            location.setCountry(result.getCountryLong());
            location.setRegion(result.getRegion());
            location.setCity(result.getCity());
            location.setLatitude(String.valueOf(result.getLatitude()));
            location.setLongitude(String.valueOf(result.getLongitude()));
        }
        return location;
    }

    @Cacheable(key = "#ip", sync = true, condition = "#ip != null")
    @Override
    public Location getLocationExternel(String ip) {
        Location location = new Location();
        location.setIp(ip);
        location.setIpType(IpType.of(IpUtils.isIpV4(ip) ? V4 : V6));
        try {
            //repository 读取
            IpResultPO result = ip2Repository.queryCity(ip);
            location.setCountry(result.getCountryLong());
            location.setRegion(result.getRegion());
            location.setCity(result.getCity());
            location.setLatitude(String.valueOf(result.getLatitude()));
            location.setLongitude(String.valueOf(result.getLongitude()));
        }catch (Exception e){
            log.error("ip查询异常：" + e.getMessage());
            location.setCountry(G);
            location.setRegion(G);
            location.setCity(G);
            location.setLatitude(G);
            location.setLongitude(G);
        }
        return location;
    }
}

```

#### 7. 新增底层各种ip地址库解析实现
```java
/**
 * @author zy
 * @version : Ip2Repository.java, v 1.0 
 */
public interface Ip2Repository {

    /**
     * 查询到城市级 DB3
     *
     * @param ip ip地址
     * @return po对象
     */
    IpResultPO queryCity(String ip);
}


/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/15 2:01 PM
 * @description 百度在线查询location实现
 */
@Slf4j
@Repository
public class BaiduIp2Repository implements Ip2Repository {

    @Autowired
    private GeoLocationProperties geoLocationProperties;

    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("BaiduIp2Repository queryCity ip:{}", ip);
        IpResultPO result = new IpResultPO();
        if (!geoLocationProperties.getLibraryType()) {
            log.warn("geoLocationProperties.libraryType is false");
            return result;
        }
        if (StringUtils.isEmpty(geoLocationProperties.getAccessKey())) {
            geoLocationProperties.setAccessKey(KeyConfig.AccessKey);
        }
        if (StringUtils.isEmpty(geoLocationProperties.getSecretkey())) {
            geoLocationProperties.setSecretkey(KeyConfig.Secretkey);
        }
        BaiduRsp rsp = Baidu.send(ip, geoLocationProperties.getAccessKey(), geoLocationProperties.getSecretkey());

        result.setCity(rsp.getData().getDetails().getCity());
        result.setCountryLong(rsp.getData().getNation());
        result.setRegion(rsp.getData().getDetails().getRegion());
        result.setLatitude(String.valueOf(rsp.getData().getDetails().getLat()));
        result.setLongitude(String.valueOf(rsp.getData().getDetails().getLng()));
        return result;
    }
}


/**
 * 基于bin文件 ip解析成location实现
 *
 * @author liushengbin
 * @date 2022-02-23
 */
@Slf4j
@Repository
public class BinIp2Repository implements Ip2Repository {

    public BinIp2Repository() {
        // 初始化构造函数时，拷贝BIN文件到工作目录下
        GeoIp2LocationUtil.initGeo();
    }

    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("BinIp2Repository queryCity ip:{}", ip);
        IPResult ipResult = GeoIp2LocationUtil.ip2Location(ip);
        if (ipResult == null) {
            return new IpResultPO();
        }
        IpResultPO result = new IpResultPO();
        result.setCity(ipResult.getCity());
        result.setCountryLong(ipResult.getCountryLong());
        result.setRegion(ipResult.getRegion());
        result.setLatitude(String.valueOf(ipResult.getLatitude()));
        result.setLongitude(String.valueOf(ipResult.getLongitude()));
        return result;
    }
}

/**
 * 基于nio+2分法csv读取实现
 *
 * @author xk
 * @version : CsvIp2RepositoryImpl.java, v 1.0 2021年07月20日 10时36分 xk Exp$
 */
@Slf4j
@Repository
public class CsvIp2Repository implements Ip2Repository {

    private static byte[] fileByteArray = null;

    static {
        try {
            fileByteArray = FileUtil.getByteArray("IP2LOCATION-LITE-DB11.CSV");
        } catch (IOException e) {
            log.error("init csv file error");
        }
    }


    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("CsvIp2Repository queryCity ip:{}", ip);
        //利用ip 地址做为 key 读取 resource 目录下 IP2LOCATION-LITE-DB11.CSV 文件内容，并匹配查询结果
        try {
            BigInteger bigInteger = null;
            if (IpUtils.isIpV4(ip)) {
                bigInteger = IpUtils.translateToV4No(ip);
            } else {
                bigInteger = IpUtils.translateToV6No(ip);
            }
            return CsvUtils.findLocationByIp(bigInteger, fileByteArray);
        } catch (Exception e) {
            log.error("CsvIp2Repository queryCity error:{}", e);
            return new IpResultPO();
        }

    }
}

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/15 2:01 PM
 * @description 纯真数据库离线查询location实现
 */
@Slf4j
@Repository
public class DataIp2Repository implements Ip2Repository {

    private static byte[] fileByteArray = null;

    static {
        try {
            fileByteArray = FileUtil.getByteArray("data.db");
        } catch (IOException e) {
            log.error("init data.db error");
        }
    }
    @SneakyThrows
    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("DataIp2Repository queryCity ip:{}", ip);
        IpResultPO result = new IpResultPO();
        IpData ipData = IpBaseUtils.getCityInfo(fileByteArray, ip);
        result.setCity(ipData.getCity());
        result.setCountryLong(ipData.getCountry());
        result.setRegion(ipData.getProvince());
        return result;
    }
}

/**
 * @author zy
 * @description db离线查询location实现
 * @version : JdbcIp2Repository.java, v 1.0 
 */
@Slf4j
@Repository
public class JdbcIp2Repository implements Ip2Repository {

    /**
     * 为了减少因导入不同类型的csv文件类型导致对程序的影响，这里使用了*.
     * 即不管导入csv只有4列或者12列，sql语句是不变的
     */
    private static final String QUERY_CITY_DEF = "select * from ip2_location where ip_from <= ? and  ? <= ip_to limit 1";

    @Resource(name = "ipJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public IpResultPO queryCity(String ip) {
        log.debug("JdbcIp2Repository queryCity ip:{}", ip);
        try {
            BigInteger bigInteger = null;

            if (IpUtils.isIpV4(ip)) {
                bigInteger = IpUtils.translateToV4No(ip);
            } else {
                bigInteger = IpUtils.translateToV6No(ip);
            }
            return jdbcTemplate.queryForObject(QUERY_CITY_DEF, new IpRowMapper(), bigInteger.longValue(), bigInteger.longValue());
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("JdbcIp2Repository queryCity error:{}", e);
            return new IpResultPO();
        }
    }

}
```

#### 8. 测试类验证
```java
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
```