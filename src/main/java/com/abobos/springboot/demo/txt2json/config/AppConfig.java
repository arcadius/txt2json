package com.abobos.springboot.demo.txt2json.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String geoIpApiBaseUrl;

    public String getGeoIpApiBaseUrl() {
        return geoIpApiBaseUrl;
    }

    public void setGeoIpApiBaseUrl(final String geoIpApiBaseUrl) {
        this.geoIpApiBaseUrl = geoIpApiBaseUrl;
    }
}
