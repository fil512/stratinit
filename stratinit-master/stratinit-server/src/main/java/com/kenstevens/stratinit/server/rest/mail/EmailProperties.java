package com.kenstevens.stratinit.server.rest.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stratinit.email")
public class EmailProperties {
    private String fromAddress = "hq@strategicinitiative.org";
    private String adminAddress = "ken.stevens@sympatico.ca";

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }
}
