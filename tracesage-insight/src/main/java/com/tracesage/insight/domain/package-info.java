/**
 * 领域层（Domain）——DDD 的核心，不应依赖任何其他层。
 *
 * <p>本层只表达业务规则，不关心存储方式、不关心框架、不关心协议。</p>
 *
 * <ul>
 *     <li>{@code model.entity}：实体，有唯一标识、生命周期随标识变化</li>
 *     <li>{@code model.valueobject}：值对象，不可变、无标识、由属性相等判定</li>
 *     <li>{@code model.aggregate}：聚合根，一致性边界，外部只能通过聚合根访问内部对象</li>
 *     <li>{@code model.enums}：领域枚举与状态机</li>
 *     <li>{@code service}：领域服务，承载跨聚合、跨实体的业务规则</li>
 *     <li>{@code repository}：仓储接口（只定义，不实现），实现由基础设施层提供</li>
 *     <li>{@code gateway}：外部系统防腐层接口，隔离第三方依赖</li>
 *     <li>{@code event}：领域事件，用于解耦聚合之间的联动</li>
 * </ul>
 *
 * <p>禁止在领域层出现 {@code @Service}、{@code @Mapper}、{@code @TableName} 等技术注解。</p>
 */
package com.tracesage.insight.domain;
