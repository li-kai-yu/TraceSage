# TraceSage 迹智

> 分布式链路追踪 + 故障根因分析平台

TraceSage 是一个微服务架构的链路追踪与故障根因分析平台：业务应用接入埋点 SDK 后，
链路数据经 RabbitMQ 异步上报，落地 Elasticsearch；平台再基于 RAG 知识库 + LangChain4j Agent
对异常链路做根因分析并生成故障报告。

## 技术栈

| 层次 | 选型 |
| --- | --- |
| 基础 | JDK 17、Maven、Spring Boot 3.3.5 |
| 架构 | 微服务 + DDD 四层架构（interfaces / application / domain / infrastructure） |
| 微服务 | Spring Cloud 2023.0.3、Spring Cloud Alibaba 2023.0.3.2（Nacos 注册配置中心）、Gateway、OpenFeign |
| 安全 | Spring Security + JWT（jjwt） |
| 持久化 | MyBatis-Plus 3.5.7 + MySQL 8、Redis |
| 消息 | RabbitMQ |
| 搜索 | Elasticsearch 8 |
| AI | LangChain4j 1.18.1-beta28（AiService / RAG / Agent） |
| 文档 | Knife4j 4.5.0（OpenAPI3） |
| 工具 | Lombok、MapStruct、Hutool |

## 模块结构

```
TraceSage
├── tracesage-common                    公共能力聚合（不写业务）
│   ├── tracesage-common-core           统一返回 / 异常 / 常量 / 枚举 / 工具
│   ├── tracesage-common-web            全局异常处理 / 响应包装 / AOP 日志 / Knife4j
│   ├── tracesage-common-security       JWT / Spring Security / 登录用户上下文
│   ├── tracesage-common-data           MyBatis-Plus / MySQL / Redis
│   ├── tracesage-common-mq             RabbitMQ 交换机队列 / 消息常量
│   └── tracesage-common-es             Elasticsearch 客户端 / 索引常量
├── tracesage-trace-sdk                 链路埋点 SDK（给业务方引入）
├── tracesage-gateway                   API 网关：8080
├── tracesage-system                    系统服务：认证授权 + 链路采集/存储/查询：8081
└── tracesage-insight                   洞察服务：RAG + Agent 根因分析：8082
```

## 文档

- [01-架构与限界上下文](docs/01-架构与限界上下文.md)
- [02-DDD 分层开发规范](docs/02-DDD分层开发规范.md)
- [03-包结构与命名规范](docs/03-包结构与命名规范.md)
- [04-接口与统一响应规范](docs/04-接口与统一响应规范.md)
- [05-数据库设计规范](docs/05-数据库设计规范.md)

## 本地启动依赖

| 组件 | 端口 | 说明 |
| --- | --- | --- |
| Nacos | 8848 | 注册中心 + 配置中心 |
| MySQL | 3306 | 业务库 `tracesage` |
| Redis | 6379 | 缓存 / 令牌黑名单 |
| RabbitMQ | 5672 / 15672 | 链路数据异步上报 |
| Elasticsearch | 9200 | 链路数据存储与检索 |

所有连接信息均可通过环境变量覆盖，见各服务 `src/main/resources/application.yaml`。
