package com.cristovantamayo.launcher;

import com.cristovantamayo.order.config.RabbitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.cristovantamayo",
		// Bloqueia na raiz do Spring 4 todas as tentativas de gerar bancos automáticos ocultos
		excludeName = {
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
				"org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
				"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
				"org.springframework.boot.autoconfigure.jdbc.DataSourceInitializationAutoConfiguration", // BLOQUEIA O ERRO ATUAL
				"org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
		})
@EnableConfigurationProperties(RabbitProperties.class)
public class LauncherApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(LauncherApplication.class);

		Map<String, Object> properties = new HashMap<>();
		properties.put("spring.autoconfigure.exclude", String.join(",",
				"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
				"org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
				"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
				"org.springframework.boot.autoconfigure.jdbc.DataSourceInitializationAutoConfiguration",
				"org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
		));

		app.setDefaultProperties(properties);
		app.run(args);
	}

}
