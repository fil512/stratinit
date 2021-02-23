package com.kenstevens.stratinit.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.RestRequest;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class RestClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    @Autowired
    private Account account;
    private HttpClientContext context;

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> Result<T> get(String path, Class<T> valueClass, Object... args) {
        return getResult(path, valueClass, resultFunction());
    }

    public <T> Result<List<T>> getList(String path, Class<T> valueClass, Object... args) {
        return getResult(path, List.class, listResultFunction());
    }

    private Function<Class<?>, JavaType> resultFunction() {
        return vc -> mapper.getTypeFactory().constructParametricType(Result.class, vc);
    }

    private Function<Class<?>, JavaType> listResultFunction() {
        return vc -> {
            TypeFactory typeFactory = mapper.getTypeFactory();
            JavaType inner = typeFactory.constructParametricType(ArrayList.class, SIGame.class);
            return typeFactory.constructParametricType(Result.class, inner);
        };
    }

    private <T> Result<T> getResult(String path, Class<?> resultClass, Function<Class<?>, JavaType> typeFunction) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet httpGet = new HttpGet(baseUrl + path);
            httpGet.setHeader("Accept", "application/json");
            try (CloseableHttpResponse response = client.execute(httpGet, context)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    return failedResultFromStatusCode(response);
                }
                return mapper.readValue(response.getEntity().getContent(), typeFunction.apply(resultClass));
            }
        } catch (Exception e) {
            return new Result<>(e.getMessage(), false);
        }
    }

    private <T> Result<T> failedResultFromStatusCode(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return new Result<>("Request failed with status code " + statusCode + ": " + HttpStatus.getStatusText(statusCode), false);
    }

    public <T> Result<T> post(String path, RestRequest request, Class<T> valueClass) {
        return postResult(path, request, valueClass, resultFunction());
    }

    private <T> Result<T> postResult(String path, RestRequest request, Class<?> resultClass, Function<Class<?>, JavaType> typeFunction) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(baseUrl + path);
            String json = mapper.writeValueAsString(request);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            try (CloseableHttpResponse response = client.execute(httpPost, context)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    return failedResultFromStatusCode(response);
                }
                return mapper.readValue(response.getEntity().getContent(), typeFunction.apply(resultClass));
            }
        } catch (Exception e) {
            return new Result<>(e.getMessage(), false);
        }
    }

    public void setAccount() {
        HttpHost targetHost = new HttpHost(baseUrl);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
    }
}
