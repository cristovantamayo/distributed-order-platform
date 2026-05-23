package com.cristovantamayo.order.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@Profile("order")
@EnableJpaRepositories(
        basePackages = "com.cristovantamayo.order.repository",
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderDatabaseConfig {

    @Primary
    @Bean(name = "orderDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.order.datasource")
    public DataSourceProperties orderDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "orderDataSource")
    public DataSource orderDataSource() {
        return orderDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "orderEntityManagerFactory")
    @DependsOn("orderLiquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("orderDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        // Define quais pacotes do submódulo contêm as classes @Entity
        em.setPackagesToScan("com.cristovantamayo.order.repository.entities");

        // Alinhado ao Hibernate 7 usado no Spring Boot 4
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Define as propriedades de dialeto explicitamente para a persistência
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Primary
    @Bean(name = "orderTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public EntityManagerFactoryDependsOnPostProcessor orderDependsOnPostProcessor() {
        // Restricts the order entity manager to ONLY wait for the order migration tracker
        return new EntityManagerFactoryDependsOnPostProcessor("orderLiquibase");
    }

}
