package com.example.marketplace.registration.infrastructure;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
        basePackages = "com.example.marketplace.registration",
        entityManagerFactoryRef = "registrationEntityManagerFactory",
        transactionManagerRef = "registrationTransactionManager"
)
public class RegistrationConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("registration.datasource")
    public DataSourceProperties registrationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("registration.datasource.configuration")
    public HikariDataSource registrationDataSource(
            @Qualifier("registrationDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    @ConfigurationProperties("registration.jpa")
    public JpaProperties registrationJpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean registrationEntityManagerFactory(
            @Qualifier("registrationDataSource") DataSource dataSource,
            @Qualifier("registrationJpaProperties") JpaProperties jpaProperties) {

        var builder = new LocalContainerEntityManagerFactoryBean();
        builder.setDataSource(dataSource);
        builder.setPackagesToScan("com.example.marketplace.registration");
        builder.setPersistenceUnitName("registration");
        builder.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        builder.setJpaPropertyMap(new LinkedHashMap<>(jpaProperties.getProperties()));

        return builder;
    }

    @Primary
    @Bean
    public PlatformTransactionManager registrationTransactionManager(
            @Qualifier("registrationEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
}