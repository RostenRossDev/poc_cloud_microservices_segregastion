package com.poc.configServer.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@RefreshScope //para que se actualicen los archivos de configuracion al hacer un /actuator/refresh
@EnableConfigServer // Para indicar que el micro servicio es un config server.
@SpringBootApplication
public class ConfigServerDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerDbApplication.class, args);
	}

	
}
