package com.kenstevens.stratinit.rest;

import com.kenstevens.stratinit.remote.StratInit;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ManualRestTest {
    @Test
    public void getConfig() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/stratinit/" + StratInit.SERVER_CONFIG);


            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
            }
        }
    }
}
