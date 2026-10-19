package com.tracesage.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TraceSage 系统服务启动类。
 *
 * <p>职责：认证授权（RBAC）与链路数据的采集、清洗、存储、查询。</p>
 */
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.tracesage.system.infrastructure.persistence.mapper")
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
