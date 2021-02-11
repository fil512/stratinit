package com.kenstevens.stratinit.main;

public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        // FIXME try upgrading to latest Spring
        // and using Spring Boot and new annotation based remoting
        // See https://www.baeldung.com/spring-remoting-http-invoker
        StratInitClient.main(args);
    }
}
