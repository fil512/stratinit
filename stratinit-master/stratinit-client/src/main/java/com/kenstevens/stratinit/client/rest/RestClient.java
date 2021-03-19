package com.kenstevens.stratinit.client.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kenstevens.stratinit.client.api.IClientInterceptor;
import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RestClient {
    private final Set<IClientInterceptor> interceptors = new HashSet<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private HttpClientContext context;
    @Autowired
    private Account account;

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> Result<T> get(String path, final Class<T> valueClass) {
        return getResult(path, valueClass, resultFunction());
    }

    public <T> Result<List<T>> getList(String path, final Class<T> valueClass) {
        return getResult(path, List.class, listResultFunction(valueClass));
    }

    private Function<Class<?>, JavaType> resultFunction() {
        return vc -> mapper.getTypeFactory().constructParametricType(Result.class, vc);
    }

    private Function<Class<?>, JavaType> listResultFunction(final Class<?> valueClass) {
        return vc -> {
            TypeFactory typeFactory = mapper.getTypeFactory();
            JavaType inner = typeFactory.constructParametricType(ArrayList.class, valueClass);
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
                Result<T> result = mapper.readValue(response.getEntity().getContent(), typeFunction.apply(resultClass));
                callGetInterceptors(path, result);
                return result;
            }
        } catch (Exception e) {
            callGetFailInterceptors(path, e);
            return Result.falseInstance((Class<T>) resultClass, e);
        }
    }

    private <T> Result<T> failedResultFromStatusCode(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return new Result<>("Request failed with status code " + statusCode + ": " + HttpStatus.getStatusText(statusCode), false);
    }

    public <T> Result<T> post(String path, IRestRequestJson request, Class<T> valueClass) {
        return postResult(path, request, valueClass, resultFunction());
    }

    private <T> Result<T> postResult(String path, IRestRequestJson request, Class<?> resultClass, Function<Class<?>, JavaType> typeFunction) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(baseUrl + path);
            String json = mapper.writeValueAsString(request);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = client.execute(httpPost, context)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    return failedResultFromStatusCode(response);
                }
                Result<T> result = mapper.readValue(response.getEntity().getContent(), typeFunction.apply(resultClass));
                callPostInterceptors(path, request, result);
                return result;
            }
        } catch (Exception e) {
            callPostFailInterceptors(path, request, e);
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

    public void registerInterceptor(IClientInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    private <T> void callGetInterceptors(String path, Result<T> result) {
        interceptors.forEach(i -> i.get(path, result));
    }

    private <T> void callPostInterceptors(String path, IRestRequestJson request, Result<T> result) {
        interceptors.forEach(i -> i.post(path, request, result));
    }

    private void callGetFailInterceptors(String path, Exception e) {
        interceptors.forEach(i -> i.getFail(path, e));
    }

    private void callPostFailInterceptors(String path, IRestRequestJson request, Exception e) {
        interceptors.forEach(i -> i.postFail(path, request, e));
    }
}
