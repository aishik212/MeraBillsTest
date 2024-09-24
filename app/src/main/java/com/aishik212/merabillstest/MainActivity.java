package com.aishik212.merabillstest;

import static com.aishik212.merabillstest.Constants.AMOUNT_KEY;
import static com.aishik212.merabillstest.Constants.DIALOG_VISIBLE_KEY;
import static com.aishik212.merabillstest.Constants.PAYMENT_TYPE_KEY;
import static com.aishik212.merabillstest.Constants.PROVIDER_KEY;
import static com.aishik212.merabillstest.Constants.TAG_ERROR;
import static com.aishik212.merabillstest.Constants.TRANS_KEY;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.aishik212.merabillstest.databinding.ActivityMainBinding;
import com.aishik212.merabillstest.dialog.AddPaymentDialog;
import com.aishik212.merabillstest.interfaces.OnPaymentAddedListener;
import com.aishik212.merabillstest.models.PaymentDetailsModel;
import com.aishik212.merabillstest.utils.PaymentManager;
import com.aishik212.merabillstest.viewmodel.MainViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnPaymentAddedListener {

    private ActivityMainBinding binding = null;
    private MainViewModel viewModel;
    private ChipGroup paymentsChipGroup;
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

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        if (savedInstanceState != null) {
            isDialogVisible = savedInstanceState.getBoolean(DIALOG_VISIBLE_KEY, false);
            if (isDialogVisible) {
                String savedAmount = savedInstanceState.getString(AMOUNT_KEY, "");
                int savedPaymentTypeIndex = savedInstanceState.getInt(PAYMENT_TYPE_KEY, 0);
                String transId = savedInstanceState.getString(TRANS_KEY, addPaymentDialog.getTransID());
                String provider = savedInstanceState.getString(PROVIDER_KEY, addPaymentDialog.getProvider());
                showPaymentDialog(savedAmount, savedPaymentTypeIndex, transId, provider);// Re-show dialog after rotation
            }
        } else {
            getOldData();
        }
        observeViewModel();
    }

    private void getOldData() {
        viewModel.loadPayments(this);
    }

    private void initViews() {
        if (binding != null) {
            if (addPaymentDialog == null) {
                addPaymentDialog = new AddPaymentDialog(this, this);
            }

            paymentsChipGroup = binding.paymentsChipGroup;

            binding.addPaymentBtn.setOnClickListener(v -> showPaymentDialog("", 0, "", ""));

            binding.saveBtn.setOnClickListener(v -> {
                LiveData<List<PaymentDetailsModel>> payments = viewModel.getPayments();
                if (payments != null && payments.getValue() != null) {
                    if (!payments.getValue().isEmpty()) {
                        PaymentManager.saveDetailsToFile(this, payments.getValue());
                        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Unable to save", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showPaymentDialog(String amount, int paymentTypeIndex, String transId, String provider) {

        addPaymentDialog.setAmount(amount);
        addPaymentDialog.setPaymentTypeIndex(paymentTypeIndex);
        addPaymentDialog.setTransId(transId);
        addPaymentDialog.setProvider(provider);

        addPaymentDialog.show();
        isDialogVisible = true;
    }


    private void observeViewModel() {
        viewModel.getPayments().observe(this, this::observePayments);
        viewModel.getPaymentSum().observe(this, this::observePaymentSum);

    }

    private void observePaymentSum(Double sum) {
        if (sum == 0) {
            binding.amountTv.setText("₹0.0");
        } else {
            binding.amountTv.setText(String.format(Locale.getDefault(), "₹%s", sum));
        }
    }

    private void observePayments(List<PaymentDetailsModel> paymentDetailsModels) {
        try {
            paymentsChipGroup.removeAllViews();
        } catch (Exception e) {
            Log.d(TAG_ERROR, "observePayments: " + e.getLocalizedMessage());
        }
        for (PaymentDetailsModel s : paymentDetailsModels) {
            addPaymentDialog.removeFromList(s.getType());
            addPaymentChip(s);
        }
        if (paymentDetailsModels.size() >= 3) {
            binding.addPaymentBtn.setVisibility(View.GONE);
        } else {
            binding.addPaymentBtn.setVisibility(View.VISIBLE);
        }
    }

    private void addPaymentChip(PaymentDetailsModel paymentDetailsModel) {
        if (paymentsChipGroup != null) {
            Chip chip = new Chip(this);
            double amount = paymentDetailsModel.getAmount();
            chip.setText(paymentDetailsModel.getLabel() + " = ₹" + amount);
            chip.setTextSize(16F);
            chip.setShapeAppearanceModel(chip.getShapeAppearanceModel().withCornerSize(50F));
            ColorStateList chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#e0e0e0"));
            chip.setChipStrokeColor(chipBackgroundColor);
            chip.setChipBackgroundColor(chipBackgroundColor);
            chip.setCloseIconVisible(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            chip.setLayoutParams(params);
            params.setMargins(3, 0, 3, 0);
            chip.setOnCloseIconClickListener(v -> {
                paymentsChipGroup.removeView(chip);
                viewModel.removePayment(paymentDetailsModel);
                addPaymentDialog.addToList(paymentDetailsModel.getType());
            }); // Remove chip on close icon click
            paymentsChipGroup.addView(chip);
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
    public void onPaymentAdded(PaymentDetailsModel paymentDetailsModel) {
        viewModel.addPayment(paymentDetailsModel);
    }
}