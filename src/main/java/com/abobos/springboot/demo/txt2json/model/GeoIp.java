package com.abobos.springboot.demo.txt2json.model;

public class GeoIp {
    private String query;
    private String countryCode;
    private String isp;

    public GeoIp() {
    }
    public GeoIp(final String query, final String countryCode, final String isp) {
        this.query = query;
        this.countryCode = countryCode;
        this.isp = isp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(final String isp) {
        this.isp = isp;
    }
}