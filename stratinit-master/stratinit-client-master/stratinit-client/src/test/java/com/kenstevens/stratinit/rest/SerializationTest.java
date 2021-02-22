package com.kenstevens.stratinit.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.kenstevens.stratinit.BaseStratInitClientTest;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.remote.Result;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;

public class SerializationTest extends BaseStratInitClientTest {
    final Logger logger = LoggerFactory.getLogger(getClass());
    ObjectMapper mapper = new ObjectMapper();

    @Value("classpath:/games-result.json")
    Resource resourceFile;

    @Test
    public void testDeserializeGameResult() throws IOException {
        TypeFactory typeFactory = mapper.getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(ArrayList.class, SIGame.class);
        JavaType type = typeFactory.constructParametricType(Result.class, inner);
        Result result = mapper.readValue(resourceFile.getInputStream(), type);
        logger.info(result.toString());
    }
}
