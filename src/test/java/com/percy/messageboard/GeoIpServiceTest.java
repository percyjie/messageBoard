package com.percy.messageboard;

import com.percy.messageboard.service.GeoIpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeoIpServiceTest {

    private final GeoIpService geoIpService = new GeoIpService();

    @AfterEach
    void close() {
        geoIpService.close();
    }

    @Test
    void resolvesLoopbackPrivateAndPublicIps() {
        assertThat(geoIpService.resolveProvince("127.0.0.1")).isEqualTo("本地");
        assertThat(geoIpService.resolveProvince("::1")).isEqualTo("本地");
        assertThat(geoIpService.resolveProvince("10.0.0.1")).isEqualTo("内网");
        assertThat(geoIpService.resolveProvince("192.168.1.10")).isEqualTo("内网");
        assertThat(geoIpService.resolveProvince("113.108.0.1")).isEqualTo("广东省");
        assertThat(geoIpService.resolveProvince("119.96.0.1")).isEqualTo("湖北省");
        assertThat(geoIpService.resolveProvince("8.8.8.8")).isEqualTo("California");
        assertThat(geoIpService.resolveProvince("2001:db8::1")).isEqualTo("未知");
        assertThat(geoIpService.resolveProvince(null)).isEqualTo("未知");
    }
}
