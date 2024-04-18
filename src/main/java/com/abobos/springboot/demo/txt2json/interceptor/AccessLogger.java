package com.abobos.springboot.demo.txt2json.interceptor;

import static java.lang.System.currentTimeMillis;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.abobos.springboot.demo.txt2json.filter.GeoIpFilter;
import com.abobos.springboot.demo.txt2json.model.GeoIp;
import com.abobos.springboot.demo.txt2json.persistence.entity.AccessLog;
import com.abobos.springboot.demo.txt2json.persistence.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessLogger implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogger.class);
    private static final String ACCESS_LOG_ATTR = "accessLog";
    private static final String REQUEST_START_MILLI = "requestStartMilli";
    public static final String DEMO_REQUEST_ID = "DEMO_REQUEST_ID";

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final AccessLog accessLog = new AccessLog();
        accessLog.setRequestId(UUID.randomUUID());
        accessLog.setRequestStartTimestamp(OffsetDateTime.now());
        accessLog.setRequestUri(request.getRequestURI());
        //instance from the filter
        final GeoIp geoIp = (GeoIp) request.getAttribute(GeoIpFilter.GEO_IP);

        accessLog.setRequestCountryCode(geoIp.getCountryCode());
        accessLog.setRequestIpAddress(geoIp.getQuery());
        accessLog.setRequestIpProvider(geoIp.getIsp());

        request.setAttribute(ACCESS_LOG_ATTR, accessLog);
        request.setAttribute(REQUEST_START_MILLI, currentTimeMillis());
        response.setHeader(DEMO_REQUEST_ID, accessLog.getRequestId().toString());

        logger.info("Request start: {}", accessLog);

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        final AccessLog accessLog = (AccessLog) request.getAttribute(ACCESS_LOG_ATTR);
        long executeTime = currentTimeMillis() - (Long) request.getAttribute(REQUEST_START_MILLI);
        accessLog.setResponseCode(response.getStatus());
        accessLog.setTimeLapsed(executeTime);
        logger.info("Request end: accessLog {}", accessLog);
        accessLogRepository.save(accessLog);
        logger.info("Request end -- access log saved");
    }

}
