package com.aishik212.merabillstest.utils;

import static com.aishik212.merabillstest.Constants.TAG_ERROR;

import android.content.Context;
import android.util.Log;

import com.aishik212.merabillstest.models.PaymentDetailsModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaymentManager {
    private static final String FILE_NAME = "LastPayment.txt";
    private static final Gson gson = new Gson();


    public static void saveDetailsToFile(Context context, List<PaymentDetailsModel> details) {
        StringBuilder text = new StringBuilder();
        for (PaymentDetailsModel paymentDetailsModel : details) {
            String json = gson.toJson(paymentDetailsModel);
            text.append(json).append("\n");
        }
        File file = new File(context.getFilesDir(), FILE_NAME);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text.toString());
        } catch (IOException e) {
            Log.d(TAG_ERROR, "saveDetailsToFile: " + e.getLocalizedMessage());
        }
    }

    // Load Payment Details from File
    public static List<PaymentDetailsModel> loadDetailsFromFile(Context context) {
        List<PaymentDetailsModel> array = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return array;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder stringBuilder = new StringBuilder();
            int content;
            while ((content = fis.read()) != -1) {
                stringBuilder.append((char) content);
            }
            String[] split = stringBuilder.toString().split("\n");
            for (String s : split) {
                PaymentDetailsModel paymentDetailsModel = gson.fromJson(s, PaymentDetailsModel.class);
                if (paymentDetailsModel != null) {
                    array.add(paymentDetailsModel);
                }
            }
            return array;
        } catch (Exception e) {
            Log.d(TAG_ERROR, "loadDetailsFromFile: " + e.getLocalizedMessage());
        }
        return array;
    }

}
