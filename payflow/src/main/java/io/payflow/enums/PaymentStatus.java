package io.payflow.enums;

public enum PaymentStatus {
    CREATED("CREATED"),
    APPROVED("APPROVED"),
    COMPLETED("COMPLETED"),
    PENDING("PENDING");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
