package com.example.rwl.service;

public enum Risk {

    VERY_LOW("VERY LOW"),
    LOW("LOW"),
    MODERATE("MODERATE"),
    HIGH("HIGH"),
    VERY_HIGH("VERY HIGH"),
    INVALID("INVALID");

    private String risk;

    Risk(String risk) {
        this.risk = risk;
    }

    public String getRisk() {
        return risk;
    }
}
