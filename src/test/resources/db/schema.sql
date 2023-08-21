drop table `ip2_location` if exists;
-- auto-generated definition
create table `ip2_location`
(
    id  int(11) auto_increment primary key,
    ip_from      BIGINT null,
    ip_to        BIGINT null,
    country_code CHAR(2) null,
    country_name varchar(64) null,
    region_name  varchar(128) null,
    city_name    varchar(128) null,
    latitude     double null,
    longitude    double null,
    zipcode      VARCHAR(30) null,
    timezone     VARCHAR(8) null
);

