package com.i4u.company.domain.entity;

public enum CompanyType {
    PRODUCER("생산업체"),
    RECEIVER("수령업체");

    private final String description;

    CompanyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}