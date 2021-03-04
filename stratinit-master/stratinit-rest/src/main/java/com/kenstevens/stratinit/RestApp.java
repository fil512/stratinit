package com.kenstevens.stratinit;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RestApp {

    // FIXME add a websocket connection
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(RestApp.class)
                .run(args);
    }
}
