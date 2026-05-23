package com.cristovantamayo.order.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("order")
public class OrderLiquibaseConfig {

    public OrderLiquibaseConfig() {
        System.out.println("🚀!!! ORDER LIQUIBASE BEAN HAS BEEN INITIALIZED !!!🚀");
    }
    
    @Value("${spring.order.liquibase.changelog}")
    private String orderChangelog;

    @Value("${spring.order.liquibase.default-schema:APP_ORDER}")
    private String orderDefaultSchema;

    @Value("${spring.order.liquibase.changelog-table:CHANGELOG_ORDER}")
    private String orderChangelogTableName;

    @Value("${spring.order.liquibase.changelog-lock-table:CHANGELOG_LOCK_ORDER}")
    private String orderChangelogLockTableName;
    
    @Bean(name = "orderLiquibase") // Unique bean name prevents startup crash
    public SpringLiquibase liquibase(@Qualifier("orderDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(orderChangelog);
        liquibase.setLiquibaseSchema(orderDefaultSchema);
        liquibase.setDefaultSchema(orderDefaultSchema);
        liquibase.setDatabaseChangeLogTable(orderChangelogTableName);
        liquibase.setDatabaseChangeLogLockTable(orderChangelogLockTableName);
        return liquibase;
    }
}

