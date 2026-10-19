package com.tracesage.insight;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TraceSage 洞察服务启动类。
 *
 * <p>职责：RAG 知识库检索与 LangChain4j Agent 根因分析，生成故障分析报告。</p>
 */
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.tracesage.insight.infrastructure.persistence.mapper")
@SpringBootApplication
public class InsightApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightApplication.class, args);
    }
}
