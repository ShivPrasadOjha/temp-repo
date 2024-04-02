package com.icici.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestRTGS {

    public static void main(String[] args) throws IOException {
        String apiUrl = "https://uat-onprem-dmz-hybrid.icicibank.com/api/v6/composite-payment";
        String jsonTemplate = "{\n" +
                "    \"AGGRID\": \"CUST0861\",\n" +
                "    \"CORPID\": \"PRACHICIB1\",\n" +
                "    \"USERID\": \"USER3\",\n" +
                "    \"URN\": \"WXYX1234\",\n" +
                "    \"AGGRNAME\": \"COMPOSITETESTING\",\n" +
                "    \"UNIQUEID\": \"2nx4m454236n22i303\",\n" +
                "    \"DEBITACC\": \"000451000301\",\n" +
                "    \"CREDITACC\": \"000405002777\",\n" +
                "    \"IFSC\": \"ICIC0000011\",\n" +
                "    \"AMOUNT\": 1696460,\n" +
                "    \"CURRENCY\": \"INR\",\n" +
                "    \"TXNTYPE\": \"TPA\",\n" +
                "    \"PAYEENAME\": \"AMARJYOTI AUTO PVT LTD\",\n" +
                "    \"REMARKS\": \"NA\",\n" +
                "    \"WORKFLOW_REQD\": \"N\"\n" +
                "}";
        AtomicInteger val= new AtomicInteger(1);
        String settlementId = "A1007" +val.getAndIncrement() + "y";
        String priorityCode = "00010";

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
