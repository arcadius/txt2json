package com.abobos.springboot.demo.txt2json.filter;

import java.io.IOException;
import java.util.List;

import com.abobos.springboot.demo.txt2json.model.GeoIp;
import com.abobos.springboot.demo.txt2json.service.GeoIpService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GeoIpFilter extends OncePerRequestFilter {

    public static final String GEO_IP = "GEO_IP";

    @Value("#{'${app.blockedCountries}'.split(',')}")
    private List<String> blockedCountries;

    @Value("#{'${app.blockedIsps}'.split(',')}")
    private List<String> blockedIsps;


    @Autowired
    private GeoIpService geoIpService;


    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!isBlocked(request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isBlocked(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final GeoIp res = geoIpService.getGeoIp(request);
        request.setAttribute(GEO_IP, res);
        return isBlocked(res, response);
    }

    private boolean isBlocked(GeoIp geoIp, final HttpServletResponse response) throws IOException {
        final String geoIpCountry = geoIp.getCountryCode().toUpperCase();
        final String geoIpIsp = geoIp.getIsp().toUpperCase();

        if (blockedCountries.contains(geoIpCountry)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Country " + geoIpCountry + " is not allowed to access this service");
            return true;
        }

        for (String blockedIsp : blockedIsps) {
            if (geoIpIsp.contains(blockedIsp)) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Organisation " + geoIpIsp + " is not allowed to access this service");
                return true;
            }
        }

        return false;

    }

}