package com.aishik212.merabillstest.models;

public sealed class PaymentData permits PaymentData.Payment {
    public static final class Payment extends PaymentData {
        private Integer amount = 0;
        private String description = "";

        public Payment(Integer amount, String description) {
            this.amount = amount;
            this.description = description;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
