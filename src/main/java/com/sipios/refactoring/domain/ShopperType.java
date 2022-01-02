package com.sipios.refactoring.domain;

public enum ShopperType {
    STANDARD_CUSTOMER("standard customer"),
    PREMIUM_CUSTOMER("premium customer"),
    PLATINUM_CUSTOMER("platinum customer");

    private final String label;

    ShopperType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
