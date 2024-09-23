package com.aishik212.merabillstest;

import static com.aishik212.merabillstest.Constants.AMOUNT_KEY;
import static com.aishik212.merabillstest.Constants.DIALOG_VISIBLE_KEY;
import static com.aishik212.merabillstest.Constants.PAYMENT_TYPE_KEY;
import static com.aishik212.merabillstest.Constants.PROVIDER_KEY;
import static com.aishik212.merabillstest.Constants.TRANS_KEY;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.aishik212.merabillstest.databinding.ActivityMainBinding;
import com.aishik212.merabillstest.dialog.AddPaymentDialog;
import com.aishik212.merabillstest.interfaces.OnPaymentAddedListener;
import com.aishik212.merabillstest.models.Payment;
import com.aishik212.merabillstest.models.PaymentType;
import com.aishik212.merabillstest.viewmodel.MainViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnPaymentAddedListener {

    private ActivityMainBinding binding = null;
    private MainViewModel viewModel;
    private ChipGroup paymentsChipGroup;
    private double sum = 0;
    private AddPaymentDialog addPaymentDialog = null;
    private boolean isDialogVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState != null) {
            isDialogVisible = savedInstanceState.getBoolean(DIALOG_VISIBLE_KEY, false);
            if (isDialogVisible) {
                String savedAmount = savedInstanceState.getString(AMOUNT_KEY, "");
                int savedPaymentTypeIndex = savedInstanceState.getInt(PAYMENT_TYPE_KEY, 0);
                String transId = savedInstanceState.getString(TRANS_KEY, addPaymentDialog.getTransID());
                String provider = savedInstanceState.getString(PROVIDER_KEY, addPaymentDialog.getProvider());
                showPaymentDialog(savedAmount, savedPaymentTypeIndex, transId, provider);// Re-show dialog after rotation
            }
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        observeViewModel();

    }

    private void initViews() {
        if (binding != null) {
            paymentsChipGroup = binding.paymentsChipGroup;
            binding.addPaymentBtn.setOnClickListener(v -> showPaymentDialog("", 0, "", ""));

            binding.saveBtn.setOnClickListener(v -> viewModel.savePayments());
        }
    }

    private void showPaymentDialog(String amount, int paymentTypeIndex, String transId, String provider) {
        if (addPaymentDialog == null) {
            addPaymentDialog = new AddPaymentDialog(this, this);
        }

        addPaymentDialog.setAmount(amount);
        addPaymentDialog.setPaymentTypeIndex(paymentTypeIndex);
        addPaymentDialog.setTransId(transId);
        addPaymentDialog.setProvider(provider);

        addPaymentDialog.show();
        isDialogVisible = true;
    }

    private void addPaymentChip(PaymentType paymentType, String amount, String provider, String transID) {
        if (paymentsChipGroup != null) {
            Chip chip = new Chip(this);
            chip.setText(paymentType.toString() + " = ₹" + amount);
            chip.setCloseIconVisible(true);
            double sum = 0.0;
            try {
                sum = Double.parseDouble(amount);
            } catch (Exception e) {
                Log.d("ERROR", "addPaymentChip: " + e.getLocalizedMessage());
            }

            Payment payment = new Payment(paymentType.toString(), sum, provider, transID);
            chip.setOnCloseIconClickListener(v -> {
                paymentsChipGroup.removeView(chip);
                viewModel.removePayment(payment);
                addPaymentDialog.addToList(paymentType);
            }); // Remove chip on close icon click
            paymentsChipGroup.addView(chip);
            viewModel.addPayment(payment);
        }
    }

    private void observeViewModel() {
        viewModel.getPayments().observe(this, this::observePayments);
    }

    private void observePayments(List<Payment> payments) {
        sum = 0.0;
        for (Payment s : payments) {
            sum += s.getAmount();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedSum = decimalFormat.format(sum);
        if (sum == 0) {
            binding.amountTv.setText("₹0.0");
        } else {
            binding.amountTv.setText(String.format(Locale.getDefault(), "₹%s", formattedSum));
        }
        if (payments.size() >= 3) {
            binding.addPaymentBtn.setVisibility(View.GONE);
        } else {
            binding.addPaymentBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (addPaymentDialog != null && addPaymentDialog.isShowing()) {
            addPaymentDialog.dismiss();
            isDialogVisible = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save dialog visibility state
        outState.putBoolean(DIALOG_VISIBLE_KEY, isDialogVisible);

        // Save the dialog data if it is currently visible
        if (addPaymentDialog != null) {
            outState.putString(AMOUNT_KEY, addPaymentDialog.getAmount());
            outState.putInt(PAYMENT_TYPE_KEY, addPaymentDialog.getPaymentTypeIndex());
            outState.putString(TRANS_KEY, addPaymentDialog.getTransID());
            outState.putString(PROVIDER_KEY, addPaymentDialog.getProvider());
        }
    }

    @Override
    public void onPaymentAdded(String amount, PaymentType paymentType, String provider, String transID) {
        addPaymentChip(paymentType, amount, provider, transID);
    }
}