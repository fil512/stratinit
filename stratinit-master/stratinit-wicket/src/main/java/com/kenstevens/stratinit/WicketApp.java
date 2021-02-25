package com.kenstevens.stratinit;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WicketApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(WicketApp.class)
                .run(args);
    }
}
