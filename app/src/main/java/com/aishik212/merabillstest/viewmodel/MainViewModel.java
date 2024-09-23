package com.aishik212.merabillstest.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aishik212.merabillstest.models.PaymentDetailsModel;
import com.aishik212.merabillstest.utils.PaymentManager;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<PaymentDetailsModel>> payments = new MutableLiveData<>();
    private final MutableLiveData<Double> paymentSum = new MutableLiveData<>(0.0);
    private final List<PaymentDetailsModel> paymentDetailsModelList = new ArrayList<>();

    public MainViewModel() {
        // Initialize with an empty list or load from a data source
        payments.setValue(paymentDetailsModelList);
    }

    public LiveData<List<PaymentDetailsModel>> getPayments() {
        return payments;
    }

    public LiveData<Double> getPaymentSum() {
        return paymentSum;
    }

    public void addPayment(PaymentDetailsModel paymentDetailsModel) {
        paymentDetailsModelList.add(paymentDetailsModel);
        payments.setValue(paymentDetailsModelList);  // Notify observers
        updatePaymentSum();
    }

    public void addAllPayment(List<PaymentDetailsModel> paymentDetailsModel) {
        paymentDetailsModelList.clear();
        paymentDetailsModelList.addAll(paymentDetailsModel);
        payments.setValue(paymentDetailsModelList);  // Notify observers
        updatePaymentSum();
    }

    private void updatePaymentSum() {
        var sum = 0.0;
        for (PaymentDetailsModel s : paymentDetailsModelList) {
            if (s != null) {
                sum += s.getAmount();
            }
        }
        paymentSum.setValue(sum);
    }

    public void loadPayments(Context context) {
        List<PaymentDetailsModel> paymentDetailsModels = PaymentManager.loadDetailsFromFile(context);
        addAllPayment(paymentDetailsModels);
    }

    public void removePayment(PaymentDetailsModel paymentDetailsModel) {
        if (paymentDetailsModelList.contains(paymentDetailsModel)) {
            paymentDetailsModelList.remove(paymentDetailsModel);
            payments.setValue(paymentDetailsModelList);  // Notify observers
            updatePaymentSum();
        }
    }
}
