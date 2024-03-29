package com.example.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/cart-api/**")
                        .uri("lb://CARTMS"))

                .route(r -> r.path("/customer-api/**", "/customerorder-api/**")
                        .uri("lb://CUSTOMERMS"))

                .route(r -> r.path("/product-api/**")
                        .uri("lb://PRODUCTMS"))

                .route(r -> r.path("/payment-api/**")
                .uri("lb://PAYMENTMS"))
                .build();
    }

}
