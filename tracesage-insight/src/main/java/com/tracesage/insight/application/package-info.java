/**
 * 应用层（Application）。
 *
 * <p>负责用例编排：接收用户接口层的请求，加载聚合根、调用领域服务完成业务，
 * 最后通过仓储接口持久化并发布领域事件。本层不包含业务规则，业务规则一律下沉到领域层。</p>
 *
 * <ul>
 *     <li>{@code service}：应用服务，一个公开方法对应一个用例</li>
 *     <li>{@code command}：写操作入参（增删改）</li>
 *     <li>{@code query}：读操作入参（查询条件）</li>
 *     <li>{@code dto}：应用层内部流转的数据载体</li>
 *     <li>{@code assembler}：领域对象与应用层 DTO 之间的转换器</li>
 * </ul>
 *
 * <p>事务边界：由应用服务方法上的 {@code @Transactional} 控制，禁止在领域层开事务。</p>
 */
package com.tracesage.insight.application;
