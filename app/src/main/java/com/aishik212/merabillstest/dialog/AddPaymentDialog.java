package com.aishik212.merabillstest.dialog;

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
import com.aishik212.merabillstest.models.PaymentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPaymentDialog extends Dialog {

    private final ArrayList<PaymentType> addedList = new ArrayList<>();
    private final OnPaymentAddedListener listener; // Callback listener
    List<PaymentType> values;
    ArrayAdapter<PaymentType> adapter;
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
                if (position != -1 && !values.isEmpty()) {
                    updateTextFields(values.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int position = paymentTypeSpinner.getSelectedItemPosition();
                if (position != -1 && !values.isEmpty()) {
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

            listener.onPaymentAdded(getAmount(), selectedPaymentType, getProvider(), getTransID());

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
        Log.d("texts", "updateTextFields: " + position);
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
            Log.d("ERROR", "setAmount: " + e.getLocalizedMessage());
        }
    }

    public String getAmount() {
        try {
            amt = editTextAmount.getText().toString();

        } catch (Exception e) {
            Log.d("ERROR", "getAmount: " + e.getLocalizedMessage());
        }
        return amt;
    }

    public void setAmount(String amount) {
        try {
            this.amt = amount;
            if (editTextAmount != null) {
                editTextAmount.setText(amount);
            }
        } catch (Exception e) {
            Log.d("ERROR", "setAmount: " + e.getLocalizedMessage());
        }
    }

    public int getPaymentTypeIndex() {
        try {
            index = paymentTypeSpinner.getSelectedItemPosition();
        } catch (Exception e) {
            Log.d("ERROR", "getPaymentTypeIndex: " + e.getLocalizedMessage());
        }
        return index;
    }

    public void setPaymentTypeIndex(int index) {
        this.index = index;
    }

    public String getProvider() {
        try {
            provider = editTextProvider.getText().toString();
        } catch (Exception e) {
            Log.d("ERROR", "getProvider: " + e.getLocalizedMessage());
        }
        return provider;
    }

    public void setProvider(String provider) {
        try {
            this.provider = provider;
            if (editTextProvider != null) {
                editTextProvider.setText(provider);
            }
        } catch (Exception e) {
            Log.d("ERROR", "setAmount: " + e.getLocalizedMessage());
        }
    }

    public String getTransID() {
        try {
            transId = editTextTransId.getText().toString();
        } catch (Exception e) {
            Log.d("ERROR", "getTransID: " + e.getLocalizedMessage());
        }
        return transId;
    }

    public void removeFromList(PaymentType selectedPaymentType) {
        try {
            addedList.add(selectedPaymentType);
            updateValuesVariable();
        } catch (Exception e) {
            Log.d("ERROR", "removeFromList: " + e.getLocalizedMessage());
        }
    }

    public void addToList(PaymentType selectedPaymentType) {
        try {
            addedList.remove(selectedPaymentType);
            updateValuesVariable();
        } catch (Exception e) {
            Log.d("ERROR", "removeFromList: " + e.getLocalizedMessage());
        }
    }

    private void updateValuesVariable() {
        values = new ArrayList<>(Arrays.asList(PaymentType.values()));
        for (PaymentType p : addedList) {
            values.remove(p);
        }
        try {
            adapter.clear();
            adapter.addAll(values);
            adapter.notifyDataSetChanged();
            if (!values.isEmpty()) {
                paymentTypeSpinner.setSelection(0);
                updateTextFields(values.get(0));
            }

        } catch (Exception e) {
            Log.d("ERROR", "updateValuesVariable: " + e.getLocalizedMessage());
        }
    }
}

