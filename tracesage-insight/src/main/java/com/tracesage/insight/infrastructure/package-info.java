/**
 * 基础设施层（Infrastructure）。
 *
 * <p>负责技术实现细节：数据库、缓存、消息、搜索引擎、向量库、大模型调用、配置装配。
 * 本层通过实现领域层定义的仓储接口与网关接口，把技术细节与领域模型彻底隔离开。</p>
 *
 * <ul>
 *     <li>{@code persistence.mapper}：MyBatis-Plus Mapper 接口</li>
 *     <li>{@code persistence.po}：数据库持久化对象（PO）</li>
 *     <li>{@code persistence.converter}：PO 与领域实体之间的转换器</li>
 *     <li>{@code persistence.repository}：领域仓储接口的实现类</li>
 *     <li>{@code config}：本层技术组件的装配配置</li>
 *     <li>{@code es}：Elasticsearch 索引操作</li>
 *     <li>{@code ai}：LangChain4j 模型客户端与 AiService 实现</li>
 *     <li>{@code vector}：向量库与文本向量化（RAG 知识库）</li>
 * </ul>
 *
 * <p>PO 不允许出现在 interfaces 与 application 层的入参出参中。</p>
 */
package com.tracesage.insight.infrastructure;
