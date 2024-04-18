package com.abobos.springboot.demo.txt2json.service;

import java.util.Map;

import com.abobos.springboot.demo.txt2json.model.GeoIp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Service
public class GeoIpService {


    private static final Map<String, String> GEO_IP_API_FIELDS = Map.of("fields", "query,isp,countryCode");
    //private AccessLogRepository geoIpRepository;

    private final RestOperations restTemplate;
    private final IpService ipService;

    @Autowired
    public GeoIpService(final RestOperations restTemplate, final IpService ipService) {
        this.restTemplate = restTemplate;
        this.ipService = ipService;
    }

    public GeoIp getGeoIp(String ipAddress) {
        //TODO: caching should be introduced here
        try {
            return restTemplate.getForObject("/" + ipAddress, GeoIp.class, GEO_IP_API_FIELDS);
        } catch (RestClientException e) {
            //allow to run this app locally and access it through the web browser or curl
            if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "127.0.0.1".equals(ipAddress)) {
                return new GeoIp(ipAddress, "UNKNOWN", "UNKNOWN");
            }

            throw e;
        }
    }

    public GeoIp getGeoIp(final HttpServletRequest request) {
        final String ip = ipService.getClientIpAddr(request);
        return getGeoIp(ip);
    }
}
