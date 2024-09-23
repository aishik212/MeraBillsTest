package com.aishik212.merabillstest.models;

import com.aishik212.merabillstest.models.enums.PaymentType;

public class PaymentDetailsModel {
    private String label;
    private double amount;
    private String provider;
    private String transId;
    private PaymentType type;

    public PaymentDetailsModel(String label, double amount, String provider, String transId, PaymentType type) {
        this.label = label;
        this.amount = amount;
        this.provider = provider;
        this.transId = transId;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Payment{");
        sb.append("label='").append(label).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", provider=").append(provider);
        sb.append(", transId=").append(transId);
        sb.append('}');
        return sb.toString();
    }
}
