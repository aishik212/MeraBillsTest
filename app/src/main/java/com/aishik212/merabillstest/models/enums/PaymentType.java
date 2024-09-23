package com.aishik212.merabillstest.models.enums;

public enum PaymentType {
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer"),
    CREDIT_CARD("Credit Card");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
