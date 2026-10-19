# 02 DDD 分层开发规范

每个业务服务内部固定分四层。**新增功能时，先判断属于哪一层，再决定类放在哪里。**

## 1. 四层职责

| 层 | 包 | 职责 | 可以有 | 绝对禁止 |
| --- | --- | --- | --- | --- |
| 用户接口层 | `interfaces` | 接请求、返结果、参数校验、对象转换 | `@RestController`、`@Valid`、DTO、VO、Assembler | 业务判断、直接调用 Mapper、直接操作 `Repository` |
| 应用层 | `application` | 用例编排、事务边界、加载/保存聚合、发事件 | `@Service`、`@Transactional`、Command、Query、DTO | 写业务规则、直接写 SQL、直接依赖对方上下文实体 |
| 领域层 | `domain` | 业务规则、不变量、状态流转 | 实体、值对象、聚合根、领域服务、仓储/网关接口、领域事件 | Spring 注解、MyBatis 注解、`@Transactional`、任何框架依赖 |
| 基础设施层 | `infrastructure` | 技术实现：DB、缓存、MQ、ES、外部调用 | `@Mapper`、`@Repository`、PO、Converter、MQ 监听器、配置类 | 业务判断、被上层直接依赖其 PO |

## 2. 一次请求的完整链路

以「用户登录」为例：

```
AuthController.login(LoginDTO)
   └─ LoginCommand (interfaces.dto → application.command)
        └─ AuthAppService.login(LoginCommand)          @Transactional
             ├─ UserRepository.findByUsername(...)     ← domain 定义的接口
             │     └─ UserRepositoryImpl 调用 UserMapper（infrastructure）
             ├─ User.checkPassword(...)                ← 业务规则在领域层
             ├─ PasswordEncoder / TokenProvider        ← 通过 domain.gateway 接口调用
             └─ 返回 LoginResultDTO
   └─ AuthAssembler.toVO(LoginResultDTO)
Result<LoginVO>
```

要点：
- Controller **只有三行**：转换入参 → 调应用服务 → 转换出参。
- 密码校验规则写在 `User` 实体里，不写在 Service 里。
- 应用服务里不出现 `SELECT`、`UPDATE`、`@TableName`。
- Repository **只定义接口**，实现类在 `infrastructure.persistence.repository`。

## 3. 聚合设计原则

1. **一致性边界**：一个事务只修改一个聚合。需要同时改多个聚合时，用领域事件 + 最终一致性。
2. **小聚合**：聚合根只持有真正需要强一致的数据，其余用 ID 引用。
3. **外部只能通过聚合根访问内部对象**，不允许 `order.getItems().get(0).setPrice(...)` 这种穿透。
4. **聚合根负责保护不变量**：所有状态变更走聚合根的方法，方法内做校验。
5. 值对象不可变，用 `record` 或 final class + 工厂方法，不做 setter。

```java
// domain/model/aggregate/User.java —— 业务规则写在领域层
public class User {
    private final UserId id;
    private String password;

    /** 修改密码：规则内聚在实体中 */
    public void changePassword(String raw, String encoded) {
        if (!PasswordPolicy.isValid(raw)) {
            throw new BizException(ResultCode.PARAM_INVALID, "密码强度不足");
        }
        this.password = encoded;
    }
}
```

## 4. 仓储（Repository）规范

```java
// domain/repository/UserRepository.java —— 只定义，不实现
public interface UserRepository {
    User findById(UserId id);
    User findByUsername(String username);
    void save(User user);
}

// infrastructure/persistence/repository/UserRepositoryImpl.java —— 实现
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper userMapper;              // MyBatis-Plus
    private final UserConverter userConverter;        // PO <  > Entity

    @Override
    public User findById(UserId id) {
        UserPO po = userMapper.selectById(id.value());
        return userConverter.toEntity(po);
    }
    // ...
}
```

- 接口方法用**领域语言**（`findByUsername`），不用 SQL 语言（`selectOneByUsername`）。
- 返回领域对象，**不返回 PO**。
- 复杂查询（列表、分页、统计）可以走独立的 `QueryRepository` + 自定义 SQL，避免污染领域仓储。

## 5. 三层对象模型与转换

| 对象 | 所属层 | 用途 | 命名 |
| --- | --- | --- | --- |
| DTO / Command / Query | interfaces / application | 接口入参、用例入参 | `XxxDTO`、`XxxCommand`、`XxxQuery` |
| VO | interfaces | 接口出参 | `XxxVO` |
| Entity / ValueObject | domain | 业务模型 | `Xxx`、`XxxId` |
| PO | infrastructure | 数据库映射 | `XxxPO` |

转换用 MapStruct，禁止手写 `BeanUtils.copyProperties` 一把梭：

```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    User toEntity(UserPO po);
    UserPO toPO(User user);
}
```

**PO 绝对不允许出现在 Controller 的入参和出参上**，防止表结构泄漏到对外契约。

## 6. 事务规范

- 事务边界**只在应用层**，注解加在 `application/service` 的方法上。
- 领域层、基础设施层不开事务。
- 事务内不做远程调用（Feign、ES、LLM），否则长事务会拖垮连接池。
- 只读查询用 `@Transactional(readOnly = true)`，或直接不加事务。

## 7. 领域事件规范

- 事件是「已经发生的事实」，命名用**过去式**：`TraceIngestedEvent`、`FaultCaseCreatedEvent`。
- 事件定义在 `domain/event`，只包含 ID 与必要快照，不塞整个聚合。
- 跨服务事件通过 MQ 投递，消费者在自己上下文里做防腐转换，不直接反序列化成对方实体。

## 8. 常见反模式（代码评审一律打回）

| 反模式 | 问题 | 正确做法 |
| --- | --- | --- |
| Controller 直接注入 Mapper | 跳过应用层与领域层 | 走 AppService |
| Service 里写 `if (状态判断)` 一堆业务分支 | 业务规则散落 | 下沉到实体/领域服务 |
| 领域层 import `com.baomidou...` | 领域被技术污染 | 用仓储接口隔离 |
| 一个 Service 类 800 行 | 用例未拆分 | 按用例拆 AppService |
| 跨服务直接查对方数据库 | 上下文耦合 | Feign 接口 / MQ 事件 |
| Event/PO 复用一个类 | 变更互相影响 | 分离 PO / Entity / DTO / VO |
