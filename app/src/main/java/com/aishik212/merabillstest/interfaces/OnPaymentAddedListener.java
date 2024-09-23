package com.aishik212.merabillstest.interfaces;

import com.aishik212.merabillstest.models.PaymentType;

public interface OnPaymentAddedListener {
    void onPaymentAdded(String amount, PaymentType paymentType, String provider, String transID);
}
