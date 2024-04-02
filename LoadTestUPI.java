package com.icici.test;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestUPI {

    public static void main(String[] args) throws IOException {
        String apiUrl = "https://uat-onprem-dmz-hybrid.icicibank.com/api/v6/composite-payment";
        String jsonTemplate = "{\n" +
                "    \"device-id\": \"400438400438400438400438\",\n" +
                "    \"mobile\": 7988000014,\n" +
                "    \"channel-code\": \"MICICI\",\n" +
                "    \"profile-id\": \"2996304\",\n" +
                "    \"seq-no\": \"UPI4735768169179\",\n" +
                "    \"account-number\": \"\",\n" +
                "    \"use-default-acc\": \"D\",\n" +
                "    \"account-type\": null,\n" +
                "    \"payee-va\": \"Vaibhav1312@icici\",\n" +
                "    \"payer-va\": \"uattesting0014@icici\",\n" +
                "    \"amount\": 20,\n" +
                "    \"pre-approved\": \"P\",\n" +
                "    \"default-debit\": \"N\",\n" +
                "    \"default-credit\": \"N\",\n" +
                "    \"txn-type\": \"merchantToPersonPay\",\n" +
                "    \"remarks\": \"ZOMATO\",\n" +
                "    \"mcc\": 6011,\n" +
                "    \"merchant-type\": \"ENTITY\",\n" +
                "    \"urn\": null,\n" +
                "    \"vpa\": \"Vaibhav1312@icici\",\n" +
                "    \"account-provider\": \"74\",\n" +
                "    \"payee-name\": null\n" +
                "}";
        AtomicInteger val= new AtomicInteger(1);
        String settlementId = "UPI179" +val.getAndIncrement() + "y";
        String priorityCode = "10000";

        int numberOfThreads = 100;
        int loopCount = 5;

        AtomicInteger counter = new AtomicInteger(1);

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    for (int j = 0; j < loopCount; j++) {
                        String transactionRefNo = "ICI234569"+ counter.getAndIncrement()+"ff";
                        String jsonPayload = String.format(jsonTemplate, transactionRefNo);

                        HttpPost httpPost = new HttpPost(apiUrl);

                        // Add JSON payload, settlementId, and priorityCode to the request
                        StringEntity entity = new StringEntity(jsonPayload);
                        httpPost.setEntity(entity);
                        httpPost.addHeader("Content-Type", "application/json");
                        httpPost.addHeader("settlement_id", settlementId);
                        httpPost.addHeader("x-priority", priorityCode);

                        // Execute the request
                        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//                             Handle response if needed
                            int statusCode = response.getStatusLine().getStatusCode();
//                            String responseBody = EntityUtils.toString(response.getEntity());
                            System.out.println("Response Code: " + statusCode);
//                            System.out.println("Response Body: " + responseBody);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}
