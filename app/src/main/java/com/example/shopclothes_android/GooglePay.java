package com.example.shopclothes_android;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
public class GooglePay {
    private static final String TAG = "GooglePay";

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }

    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject()
                .put("type", "PAYMENT_GATEWAY")
                .put("parameters", new JSONObject()
                        // Verify these credentials in your Braintree dashboard
                        .put("gateway", "braintree")

                        .put("braintree:merchantId", "twcyjynpyk3b88bj") // Replace with actual merchant ID
                        .put("braintree:clientKey", "sandbox_9yxmfwk3_twcyjynpyk3b88bj") // Replace with actual client key
                        .put("braintree:apiVersion", "v1")
                        .put("braintree:sdkVersion", "3.50.0") // hoặc phiên bản hiện tại bạn đang dùng

                );
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("INTERAC")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        return new JSONObject()
                .put("type", "CARD")
                .put("parameters", new JSONObject()
                        .put("allowedAuthMethods", getAllowedCardAuthMethods())
                        .put("allowedCardNetworks", getAllowedCardNetworks())
                        .put("billingAddressRequired", false) // Set to false initially for testing
                        .put("billingAddressParameters", new JSONObject()
                                .put("format", "MIN") // Use MIN format for testing
                        ));
    }

    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());
        return cardPaymentMethod;
    }

    public static PaymentsClient createPaymentsClient(Context context) {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Use TEST for development
                .build();
        return Wallet.getPaymentsClient(context, walletOptions);
    }

    public static JSONObject getPaymentDataRequest(long priceCents) {
        try {
            JSONObject paymentDataRequest = getBaseRequest();
            paymentDataRequest.put("allowedPaymentMethods",
                    new JSONArray().put(getCardPaymentMethod()));

            JSONObject transactionInfo = new JSONObject()
                    .put("totalPrice", String.format(Locale.US, "%.2f", priceCents / 100.0))
                    .put("totalPriceStatus", "FINAL")
                    .put("currencyCode", "USD")
                    .put("countryCode", "US"); // Add country code

            paymentDataRequest.put("transactionInfo", transactionInfo);

            JSONObject merchantInfo = new JSONObject()
                    .put("merchantName", "Your Shop Name") // Use your actual merchant name
                    .put("merchantId", "your_google_merchant_id"); // Add if you have one

            paymentDataRequest.put("merchantInfo", merchantInfo);

            // Add shipping address requirement if needed
            paymentDataRequest.put("shippingAddressRequired", false);
            paymentDataRequest.put("emailRequired", true);

            Log.d(TAG, "Payment request created: " + paymentDataRequest.toString());
            return paymentDataRequest;

        } catch (JSONException e) {
            Log.e(TAG, "Error creating payment data request", e);
            return null;
        }
    }

    public static JSONObject getIsReadyToPayRequest() {
        try {
            JSONObject request = getBaseRequest()
                    .put("allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            Log.d(TAG, "IsReadyToPay request created: " + request.toString());
            return request;
        } catch (JSONException e) {
            Log.e(TAG, "Error creating isReadyToPay request", e);
            return null;
        }
    }
}