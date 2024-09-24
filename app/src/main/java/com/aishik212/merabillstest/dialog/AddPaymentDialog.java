package com.aishik212.merabillstest.dialog;

import static com.aishik212.merabillstest.Constants.TAG_ERROR;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aishik212.merabillstest.databinding.DialogAddPaymentBinding;
import com.aishik212.merabillstest.interfaces.OnPaymentAddedListener;
import com.aishik212.merabillstest.models.PaymentDetailsModel;
import com.aishik212.merabillstest.models.enums.PaymentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPaymentDialog extends Dialog {

    private final List<PaymentType> addedList = new ArrayList<>();
    private final OnPaymentAddedListener listener; // Callback listener
    private List<PaymentType> values;
    private ArrayAdapter<PaymentType> adapter;
    private EditText editTextAmount;
    private EditText editTextProvider;
    private EditText editTextTransId;
    private Spinner paymentTypeSpinner;
    private String amt = "";
    private String provider = "";
    private String transId = "";
    private Integer index = 0;
    private DialogAddPaymentBinding inflate;

    public AddPaymentDialog(@NonNull Context context, OnPaymentAddedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = DialogAddPaymentBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setTitle("Add Payment");
        initViews();
    }


    private void initViews() {
        editTextAmount = inflate.amountEt;
        editTextProvider = inflate.providerEt;
        editTextTransId = inflate.transactionRefEt;
        paymentTypeSpinner = inflate.paymentTypeSpinner;

        // Setup Spinner with PaymentType Enum
        updateValuesVariable();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentTypeSpinner.setAdapter(adapter);
        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean b = position != -1 && !values.isEmpty();
                if (b) {
                    updateTextFields(values.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int position = paymentTypeSpinner.getSelectedItemPosition();
                boolean b = position != -1 && !values.isEmpty();
                if (b) {
                    updateTextFields(values.get(position));
                }
            }
        });

        // Handle OK Button Click
        inflate.okBtn.setOnClickListener(v -> {
            String amount = editTextAmount.getText().toString().trim();
            PaymentType selectedPaymentType = (PaymentType) paymentTypeSpinner.getSelectedItem();

            if (amount.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount1 = 0.0;
            try {
                amount1 = Double.parseDouble(getAmount());
                if (amount1 <= 0) {
                    Toast.makeText(getContext(), "Please enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Incorrect amount provided", Toast.LENGTH_SHORT).show();
                return;
            }
            int position = paymentTypeSpinner.getSelectedItemPosition();
            PaymentType paymentType = values.get(position);

            if (paymentType != PaymentType.CASH) {
                if (getProvider().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter provider", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getTransID().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter transaction id", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            PaymentDetailsModel paymentDetailsModel = new PaymentDetailsModel(selectedPaymentType.getDisplayName(), amount1, getProvider(), getTransID(), selectedPaymentType);

            listener.onPaymentAdded(paymentDetailsModel);

            updateUI(selectedPaymentType);

            dismiss();
        });

        // Handle Cancel Button Click
        inflate.cancelBtn.setOnClickListener(v -> dismiss());

        editTextAmount.setText(amt);
        editTextProvider.setText(provider);
        editTextTransId.setText(transId);
        paymentTypeSpinner.setSelection(index);
    }

    private void updateUI(PaymentType selectedPaymentType) {
        removeFromList(selectedPaymentType);
    }

    private void updateTextFields(PaymentType position) {
        inflate.providerEt.setText("");
        inflate.transactionRefEt.setText("");
        if (position != PaymentType.CASH) {
            inflate.providerEt.setVisibility(View.VISIBLE);
            inflate.transactionRefEt.setVisibility(View.VISIBLE);
        } else {
            inflate.providerEt.setVisibility(View.GONE);
            inflate.transactionRefEt.setVisibility(View.GONE);
        }
    }

    public void setTransId(String transId) {
        try {
            this.transId = transId;
            if (editTextTransId != null) {
                editTextTransId.setText(transId);
            }
        } catch (Exception e) {
            Log.d(TAG_ERROR, "setTransId: " + e.getLocalizedMessage());
        }
    }

    public String getAmount() {
        return editTextAmount != null ? editTextAmount.getText().toString() : amt;
    }

    public void setAmount(String amount) {
        try {
            this.amt = amount;
            if (editTextAmount != null) {
                editTextAmount.setText(amount);
            }
        } catch (Exception e) {
            Log.d(TAG_ERROR, "setAmount: " + e.getLocalizedMessage());
        }
    }

    public int getPaymentTypeIndex() {
        return paymentTypeSpinner != null ? paymentTypeSpinner.getSelectedItemPosition() : index;
    }

    public void setPaymentTypeIndex(int index) {
        this.index = index;
    }

    public String getProvider() {
        return editTextProvider != null ? editTextProvider.getText().toString() : provider;
    }

    public void setProvider(String provider) {
        try {
            this.provider = provider;
            if (editTextProvider != null) {
                editTextProvider.setText(provider);
            }
        } catch (Exception e) {
            Log.d(TAG_ERROR, "setProvider: " + e.getLocalizedMessage());
        }
    }

    public String getTransID() {
        return editTextTransId != null ? editTextTransId.getText().toString() : transId;
    }

    public void removeFromList(PaymentType selectedPaymentType) {
        if (!addedList.contains(selectedPaymentType)) {
            addedList.add(selectedPaymentType);
            updateValuesVariable();
        }
    }

    public void addToList(PaymentType selectedPaymentType) {
        try {
            addedList.remove(selectedPaymentType);
            updateValuesVariable();
        } catch (Exception e) {
            Log.d(TAG_ERROR, "removeFromList: " + e.getLocalizedMessage());
        }
    }

    private void updateValuesVariable() {
        values = new ArrayList<>(Arrays.asList(PaymentType.values()));
        values.removeAll(addedList);  // Remove already added PaymentTypes

        try {
            adapter.clear();
            adapter.addAll(values);
            adapter.notifyDataSetChanged();
            if (!values.isEmpty()) {
                paymentTypeSpinner.setSelection(0);
                updateTextFields(values.get(0));
            }

        } catch (Exception e) {
            Log.d(TAG_ERROR, "updateValuesVariable: " + e.getLocalizedMessage());
        }
    }
}

