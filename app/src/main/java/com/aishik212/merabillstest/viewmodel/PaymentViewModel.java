package com.aishik212.merabillstest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aishik212.merabillstest.models.PaymentData;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<PaymentData.Payment> paymentData = new MutableLiveData<>();

    public LiveData<PaymentData.Payment> getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(Integer amount, String description) {
        paymentData.setValue(new PaymentData.Payment(amount, description));
    }

    public void setAmount(Integer amount) {
        PaymentData.Payment value = getPaymentData().getValue();
        if (value != null) {
            String description = value.getDescription();
            setPaymentData(amount, description);
        } else {
            setPaymentData(amount, "");
        }
    }
}
