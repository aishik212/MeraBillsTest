package com.aishik212.merabillstest.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aishik212.merabillstest.models.Payment;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<Payment>> payments = new MutableLiveData<>();
    private final List<Payment> paymentList = new ArrayList<>();

    public MainViewModel() {
        // Initialize with an empty list or load from a data source
        payments.setValue(paymentList);
    }

    public LiveData<List<Payment>> getPayments() {
        return payments;
    }

    public void addPayment(Payment payment) {
        paymentList.add(payment);
        payments.setValue(paymentList);  // Notify observers
    }

    public void savePayments() {
        // Implement your save logic here, e.g., save to a database or API
        // For example, you could use a repository pattern
        // repository.savePayments(paymentList);
    }

    public void loadPayments() {
        // Load payments from a data source (e.g., database or API)
        // Example: payments.setValue(repository.getPayments());
    }

    public void removePayment(Payment payment) {
        if (paymentList.contains(payment)) {
            paymentList.remove(payment);
            payments.setValue(paymentList);  // Notify observers
            Log.d("texts", "removePayment: Remove " + paymentList);
        }
    }
}
