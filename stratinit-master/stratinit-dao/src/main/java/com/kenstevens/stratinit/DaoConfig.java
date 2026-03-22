package com.kenstevens.stratinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.kenstevens.stratinit.repo")
@PropertySource("persistence.properties")
@EnableTransactionManagement
@ComponentScan
public class DaoConfig {
    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));

        // Railway injects DATABASE_URL as postgres://user:pass@host:port/dbname.
        // Individual env vars JDBC_URL / JDBC_USER / JDBC_PASS can also override
        // the defaults in persistence.properties via Spring's relaxed binding.
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            try {
                URI uri = URI.create(databaseUrl.replaceFirst("^postgres(ql)?://", "http://"));
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost()
                        + (uri.getPort() > 0 ? ":" + uri.getPort() : "")
                        + uri.getPath();
                String userInfo = uri.getUserInfo();
                int colon = userInfo != null ? userInfo.indexOf(':') : -1;
                String username = colon >= 0 ? userInfo.substring(0, colon) : userInfo;
                String password = colon >= 0 ? userInfo.substring(colon + 1) : "";
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse DATABASE_URL: " + databaseUrl, e);
            }
        } else {
            dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
            dataSource.setUsername(env.getProperty("jdbc.user"));
            dataSource.setPassword(env.getProperty("jdbc.pass"));
        }

        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.kenstevens.stratinit");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    private Properties additionalProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));

        return hibernateProperties;
    }
}
