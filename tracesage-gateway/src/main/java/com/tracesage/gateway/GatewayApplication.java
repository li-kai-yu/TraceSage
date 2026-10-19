package com.tracesage.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * TraceSage 网关启动类。
 *
 * <p>网关基于 Spring Cloud Gateway（WebFlux 响应式模型），
 * 因此本模块严禁引入 spring-boot-starter-web，否则会导致网关无法启动。</p>
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
