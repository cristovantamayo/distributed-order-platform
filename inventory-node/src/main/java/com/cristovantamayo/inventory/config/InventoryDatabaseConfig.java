package com.cristovantamayo.inventory.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Profile("inventory")
@EnableJpaRepositories(
        basePackages = "com.cristovantamayo.inventory.repository",
        entityManagerFactoryRef = "inventoryEntityManagerFactory",
        transactionManagerRef = "inventoryTransactionManager"
)
public class InventoryDatabaseConfig {

    @Bean(name = "inventoryDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.inventory.datasource")
    public DataSourceProperties inventoryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "inventoryDataSource")
    public DataSource inventoryDataSource() {
        return inventoryDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "inventoryEntityManagerFactory")
    @DependsOn("inventoryLiquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("inventoryDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        // Define quais pacotes do submódulo contêm as classes @Entity
        em.setPackagesToScan("com.cristovantamayo.inventory.repository.entities");

        // Alinhado ao Hibernate 7 usado no Spring Boot 4
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Define as propriedades de dialeto explicitamente para a persistência
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean(name = "inventoryTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("inventoryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public EntityManagerFactoryDependsOnPostProcessor inventoryDependsOnPostProcessor() {
        return new EntityManagerFactoryDependsOnPostProcessor("inventoryLiquibase");
    }

}
