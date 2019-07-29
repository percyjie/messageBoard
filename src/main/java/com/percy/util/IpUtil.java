package com.percy.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.InetAddress;

/**
 * Created with percy.
 * Date: 2019/7/29
 */
public class IpUtil {

    public static String getIpAddress(String ip) {

        try {

            // 创建 GeoLite2 数据库
//            File database = new File("ip/GeoLite2-City.mmdb");
            File database = ResourceUtils.getFile("classpath:GeoLite2-City.mmdb");
            // 读取数据库内容
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            InetAddress ipAddress = InetAddress.getByName(ip);

            // 获取查询结果
            CityResponse response = null;

            response = reader.city(ipAddress);

            // 获取国家信息
            Country country = response.getCountry();
//            System.out.println(country.getNames().get("zh-CN"));    // '中国'

            // 获取省份
            Subdivision subdivision = response.getMostSpecificSubdivision();
//            System.out.println(subdivision.getNames().get("zh-CN")); // '广西壮族自治区'

            // 获取城市
            City city = response.getCity();
//            System.out.println(city.getNames().get("zh-CN")); // '南宁'

            return subdivision.getNames().get("zh-CN") + city.getNames().get("zh-CN");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static void main(String[] args) {
        System.out.println(IpUtil.getIpAddress("183.128.141.25"));
    }
}
