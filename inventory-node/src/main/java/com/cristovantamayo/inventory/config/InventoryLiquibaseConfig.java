package com.cristovantamayo.inventory.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.lang.reflect.Method;

@Slf4j
@Configuration
@Profile("inventory")
public class InventoryLiquibaseConfig {

    public InventoryLiquibaseConfig() {
        System.out.println("🚀!!! INVENTORY LIQUIBASE BEAN HAS BEEN INITIALIZED !!!🚀");
    }

    @Value("${spring.inventory.liquibase.changelog}")
    private String inventoryChangelog;

    @Value("${spring.inventory.liquibase.default-schema:APP_INVENTORY}")
    private String inventoryDefaultSchema;

    @Value("${spring.inventory.liquibase.changelog-table:CHANGELOG_INVENTORY}")
    private String inventoryChangelogTableName;

    @Value("${spring.inventory.liquibase.changelog-lock-table:CHANGELOG_LOCK_INVENTORY}")
    private String inventoryChangelogLockTableName;

    @Lazy
    @Bean(name = "inventoryLiquibase")
    public SpringLiquibase liquibase(@Qualifier("inventoryDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(inventoryChangelog);
        liquibase.setLiquibaseSchema(inventoryDefaultSchema);
        liquibase.setDefaultSchema(inventoryDefaultSchema);
        liquibase.setDatabaseChangeLogTable(inventoryChangelogTableName);
        liquibase.setDatabaseChangeLogLockTable(inventoryChangelogLockTableName);
        return liquibase;
    }
}
