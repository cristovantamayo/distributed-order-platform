package com.cristovantamayo.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cristovantamayo")
@EnableJpaRepositories(basePackages = "com.cristovantamayo.order.repository")
@EntityScan(basePackages = "com.cristovantamayo.order.repository.entities")
public class LauncherApplication {
	public static void main(String[] args) {
		SpringApplication.run(LauncherApplication.class, args);
	}

}
