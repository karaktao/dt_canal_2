package com.ruoyi.transport.domain;

public class TransportDemandWithCity extends TransportDemand {
    private String originCity;
    private String destinationCity;

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }
}

