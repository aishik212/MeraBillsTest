package com.aishik212.merabillstest.models;

public class Payment {
    private String label;
    private double amount;
    private String provider;
    private String transId;

    public Payment(String label, double amount, String provider, String transId) {
        this.label = label;
        this.amount = amount;
        this.provider = provider;
        this.transId = transId;
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
