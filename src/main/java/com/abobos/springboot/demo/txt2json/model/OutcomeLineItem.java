package com.abobos.springboot.demo.txt2json.model;

public class OutcomeLineItem {
    // Wondering why we have no uuid or id as those are important for duplicate identification
    private String name;
    private String transport;
    private Double topSpeed;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(final String transport) {
        this.transport = transport;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(final Double topSpeed) {
        this.topSpeed = topSpeed;
    }
}
