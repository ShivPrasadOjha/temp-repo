package com.icici.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTestNEFT {

    public static void main(String[] args) throws IOException {
        String apiUrl = "https://uat-onprem-dmz-hybrid.icicibank.com/api/v6/composite-payment";
        String jsonTemplate = "{\n" +
                "    \"tranRefNo\": \"I2005640211006\",\n" +
                "    \"amount\": \"1.50\",\n" +
                "    \"senderAcctNo\": \"000451000301\",\n" +
                "    \"beneAccNo\": \"000405002777\",\n" +
                "    \"beneName\": \"NOIDA\",\n" +
                "    \"beneIFSC\": \"DLXB0000092\",\n" +
                "    \"narration1\": \"Test1\",\n" +
                "    \"narration2\": \"Test2\",\n" +
                "    \"crpId\": \"PRACHICIB1\",\n" +
                "    \"crpUsr\": \"USER3\",\n" +
                "    \"aggrId\": \"CUST1053\",\n" +
                "    \"urn\": \"SR232499824\",\n" +
                "    \"aggrName\": \"INVESTUP\",\n" +
                "    \"txnType\": \"RGS\",\n" +
                "    \"WORKFLOW_REQD\": \"N\"\n" +
                "}";
        AtomicInteger val= new AtomicInteger(1);
        String settlementId = "NEFT100" +val.getAndIncrement() + "y";
        String priorityCode = "00100";

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
                            String responseBody = EntityUtils.toString(response.getEntity());
                            System.out.println("Response Code: " + statusCode);
                            System.out.println("Response Body: " + responseBody);
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
