package com.percy.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.percy.vo.IpVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with percy.
 * Date: 2019/7/29
 */
public class IpUtil {

//    public static String getIpAddress(String ip) {
//
//        try {
//
//            // 创建 GeoLite2 数据库
////            File database = new File("ip/GeoLite2-City.mmdb");
//            File database = ResourceUtils.getFile("classpath:GeoLite2-City.mmdb");
//            // 读取数据库内容
//            DatabaseReader reader = new DatabaseReader.Builder(database).build();
//            InetAddress ipAddress = InetAddress.getByName(ip);
//
//            // 获取查询结果
//            CityResponse response = null;
//
//            response = reader.city(ipAddress);
//
//            // 获取国家信息
//            Country country = response.getCountry();
////            System.out.println(country.getNames().get("zh-CN"));    // '中国'
//
//            // 获取省份
//            Subdivision subdivision = response.getMostSpecificSubdivision();
////            System.out.println(subdivision.getNames().get("zh-CN")); // '广西壮族自治区'
//
//            // 获取城市
//            City city = response.getCity();
////            System.out.println(city.getNames().get("zh-CN")); // '南宁'
//
//            return subdivision.getNames().get("zh-CN") + city.getNames().get("zh-CN");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "";
//
//    }

    public static String getIpAddress(HttpServletRequest request) {
        try {

            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.indexOf(",") != -1) {
                    ip = ip.split(",")[0];
                }
            }
            System.out.println("ip："+ip);
            String object = HttpUtil.get("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
            if(StringUtils.isNotBlank(object)){
                IpVo ipVo = JSON.parseObject(object, IpVo.class);
                // XX表示内网
                if (ipVo != null && ipVo.getCode() == 0 && !ipVo.getData().getRegion().equals("XX")) {
                    System.out.println(ipVo.getData().getRegion());
                    System.out.println(ipVo.getData().getCity());
                    return ipVo.getData().getRegion() + ipVo.getData().getCity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

//    public static void main(String[] args) {
//        System.out.println(IpUtil.getIpAddress("183.128.141.25"));
//    }
}
