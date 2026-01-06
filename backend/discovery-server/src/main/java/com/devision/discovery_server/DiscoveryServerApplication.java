package com.devision.discovery_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

	public static void main(String[] args) {
        System.out.println("Working directory = " + System.getProperty("user.dir"));
        SpringApplication.run(DiscoveryServerApplication.class, args);
	}

}
