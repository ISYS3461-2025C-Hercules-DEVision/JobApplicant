package com.devision.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator routerBuilder(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authentication-service", r->r.path("/auth/**")
                        .uri("lb://AUTHENTICATION-SERVICE"))
                .route("applicant-service", r->r.path("/api/v1/applicants/**")
                        .uri("lb://APPLICANT-SERVICE"))
                .build();
    }
}
