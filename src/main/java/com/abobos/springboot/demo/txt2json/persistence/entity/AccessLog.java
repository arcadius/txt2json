package com.abobos.springboot.demo.txt2json.persistence.entity;

import java.time.OffsetDateTime;
import java.util.StringJoiner;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AccessLog {
    @Id
    private UUID requestId;
    private String requestUri;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime requestStartTimestamp;
    private Integer responseCode;
    private String requestIpAddress;
    private String requestCountryCode;
    private String requestIpProvider;
    private Long timeLapsed;

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public OffsetDateTime getRequestStartTimestamp() {
        return requestStartTimestamp;
    }

    public void setRequestStartTimestamp(final OffsetDateTime requestTimestamp) {
        this.requestStartTimestamp = requestTimestamp;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestIpAddress() {
        return requestIpAddress;
    }

    public void setRequestIpAddress(final String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }

    public String getRequestCountryCode() {
        return requestCountryCode;
    }

    public void setRequestCountryCode(final String requestCountryCode) {
        this.requestCountryCode = requestCountryCode;
    }

    public String getRequestIpProvider() {
        return requestIpProvider;
    }

    public void setRequestIpProvider(final String requestIpProvider) {
        this.requestIpProvider = requestIpProvider;
    }

    public Long getTimeLapsed() {
        return timeLapsed;
    }

    public void setTimeLapsed(final Long timeLapsed) {
        this.timeLapsed = timeLapsed;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AccessLog.class.getSimpleName() + "[", "]")
                .add("requestId=" + getRequestId())
                .add("requestUri='" + getRequestUri() + "'")
                .add("requestTimestamp=" + getRequestStartTimestamp())
                .add("responseCode=" + getResponseCode())
                .add("requestIpAddress='" + getRequestIpAddress() + "'")
                .add("requestCountryCode='" + getRequestCountryCode() + "'")
                .add("requestIpProvider='" + getRequestIpProvider() + "'")
                .add("timeLapsed=" + getTimeLapsed())
                .toString();
    }
}