package com.abobos.springboot.demo.txt2json.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpService {
    public String getClientIpAddr(HttpServletRequest request) {
        String ip = trim(request.getHeader("X-Forwarded-For"));
        if (ip == null) {
            ip = trim(request.getHeader("Proxy-Client-IP"));
        }

        if (ip == null) {
            ip = trim(request.getHeader("WL-Proxy-Client-IP"));
        }
        if (ip == null) {
            ip = trim(request.getHeader("HTTP_CLIENT_IP"));
        }
        if (ip == null) {
            ip = trim(request.getHeader("HTTP_X_FORWARDED_FOR"));
        }

        if (ip == null) {
            ip = trim(request.getRemoteAddr());
        }

        // handle comma separated values

        return getLastIp(ip);

    }

    private String getLastIp(String ip) {

        if (ip == null) {
            return null;
        }

        String[] ips = ip.split(",");
        for (int i = ips.length - 1; i >= 0; i--) {
            String tmp = trimToNull(ips[i]);
            if (tmp != null) {
                return tmp;
            }
        }

        return null;
    }


    private String trim(String s) {

        if (s != null) {
            s = s.toLowerCase().replaceAll("unknown", "");
        }

        return trimToNull(s);
    }


    private String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String tmp = str.trim();
        if (tmp.isEmpty()) {
            return null;
        }

        return tmp;
    }

}
