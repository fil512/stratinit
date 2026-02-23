package com.kenstevens.stratinit;

import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.config.ServerConfig;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void migrate() throws Exception {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        try (Liquibase liquibase = new Liquibase(
                "db/changelog/db.changelog-master.xml",
                new ClassLoaderResourceAccessor(),
                database)) {
            liquibase.update("");
        }
    }

    @Bean
    IServerConfig serverConfig() {
        return new ServerConfig(RunModeEnum.PRODUCTION);
    }
}
