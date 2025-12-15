package com.devision.config;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("application-service", r -> r
                .path("/applications/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://APPLICATION-SERVICE")
            )
            .build();
    }
}
