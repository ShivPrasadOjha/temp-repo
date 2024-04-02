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
import java.util.concurrent.atomic.AtomicReference;

public class LoadTestIMPS {

    public static void main(String[] args) throws IOException {
        String apiUrl = "https://uat-onprem-dmz-hybrid.icicibank.com/api/v6/composite-payment";
        String jsonTemplate = "{\n" +
                "    \"localTxnDtTime\": \"20231013110800\",\n" +
                "    \"beneAccNo\": \"123456041\",\n" +
                "    \"beneIFSC\": \"NPCI0000001\",\n" +
                "    \"amount\": \"10\",\n" +
                "    \"tranRefNo\": \"19809808098091091\",\n" +
                "    \"paymentRef\": \"random\",\n" +
                "    \"senderName\": \"MFIN\",\n" +
                "    \"mobile\": \"9999999999\",\n" +
                "    \"retailerCode\": \"rcode\",\n" +
                "    \"passCode\": \"447c4524c9074b8c97e3a3c40ca7458d\",\n" +
                "    \"bcID\": \"IBCKer00055\"\n" +
                "}";
        AtomicInteger val= new AtomicInteger(1);
        String settlementId = "IMPS109" +val.getAndIncrement() + "y";
        String priorityCode = "01000";

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
