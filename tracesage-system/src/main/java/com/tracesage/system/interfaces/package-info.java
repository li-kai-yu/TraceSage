/**
 * 用户接口层（Interfaces / 表现层）。
 *
 * <p>负责接收外部请求并返回结果，是系统对外暴露的唯一入口。本层只做参数校验、
 * DTO 与领域对象的转换、调用应用层服务，不写任何业务逻辑。</p>
 *
 * <ul>
 *     <li>{@code controller}：REST 接口，只做入参与出参编排</li>
 *     <li>{@code dto}：入参对象（Request/Command 载体），配合校验注解使用</li>
 *     <li>{@code vo}：出参对象（View Object），只暴露需要返回的字段</li>
 *     <li>{@code assembler}：DTO/VO 与领域对象之间的转换器（由 MapStruct 实现）</li>
 * </ul>
 *
 * <p>依赖方向：interfaces → application → domain ← infrastructure。</p>
 */
package com.tracesage.system.interfaces;
