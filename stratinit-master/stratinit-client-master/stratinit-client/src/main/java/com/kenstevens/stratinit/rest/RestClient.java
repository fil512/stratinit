package com.kenstevens.stratinit.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RestClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> Result<T> get(String path, Class<T> valueClass, Object... args) {
        Function<Class<?>, JavaType> typeFunction = vc -> mapper.getTypeFactory().constructParametricType(Result.class, vc);

        return getResult(path, valueClass, typeFunction);
    }

    public <T> Result<List<T>> getList(String path, Class<T> valueClass, Object... args) {
        Function<Class<?>, JavaType> typeFunction = vc -> {
            TypeFactory typeFactory = mapper.getTypeFactory();
            JavaType inner = typeFactory.constructParametricType(ArrayList.class, SIGame.class);
            return typeFactory.constructParametricType(Result.class, inner);
        };

        return getResult(path, List.class, typeFunction);
    }

    private <T> Result<T> getResult(String path, Class<?> resultClass, Function<Class<?>, JavaType> typeFunction) {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestFactory rf = restTemplate.getRequestFactory();
        try {
            ClientHttpRequest request = rf.createRequest(new URI(baseUrl + path), HttpMethod.GET);
            ClientHttpResponse response = request.execute();
            return mapper.readValue(response.getBody(), typeFunction.apply(resultClass));
        } catch (Exception e) {
            // FIXME failure static method
            return new Result<>(e.getMessage(), false);
        }
    }
}
