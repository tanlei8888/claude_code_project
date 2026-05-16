# CLAUDE.md - Spring Boot 生产级代码规范

你是一位资深的 Spring Boot 专家。在为本项目编写、修改或审查 Java 代码时，请严格遵守以下 13 条规则。

## 1. 按功能分包
坚持 **按功能分包 (Package by Feature)**，而非按技术层分包。
* ✅ 正确: `com.example.invoice`, `com.example.customer`
* ❌ 错误: `com.example.controllers`, `com.example.services`, `com.example.repositories`

## 2. 仅使用构造器注入
依赖注入**只能**通过构造器进行，禁止使用 `@Autowired` 字段注入。
* 推荐使用 Lombok 的 `@RequiredArgsConstructor` 以保持代码简洁。
* 禁止在字段上直接标注 `@Autowired`。

## 3. Controller 必须轻薄
REST 控制器只负责 HTTP 翻译，**绝不包含任何业务逻辑**。
* Controller 方法只应做三件事：参数校验、调用 Service 方法、返回响应。
* 所有业务规则、数据处理和编排都应在 Service 层完成。

## 4. 严禁 Lombok `@Data` 用于 JPA 实体
JPA 实体上**禁止使用 `@Data`**，它生成的 `equals()` 和 `hashCode()` 会破坏 Hibernate 的脏检查机制。
* 推荐组合: `@Getter`, `@Setter`, `@EqualsAndHashCode(of = "id")`, `@ToString`。
* 始终明确指定 `@EqualsAndHashCode` 以仅包含 `id` 字段，避免懒加载代理引发的问题。

## 5. DTO 和值对象使用 Java Record
从 Java 16 开始，数据传输对象 (DTO) 和值对象应优先声明为 `record`。
* Record 天然不可变，语义清晰，且能极大减少样板代码。
* 仅在需要可变性或继承时才使用普通类。

## 6. 异常处理：绝不吞噬异常
捕获异常后**严禁不做任何处理 (Swallow)**。
* 必须记录日志、重新包装为业务异常、或重新抛出。
* 空 `catch` 块是绝对禁止的。

## 7. Stream API 只用于转换，副作用用循环
* **Stream** 用于过滤、映射、收集等数据转换操作。
* **for-each / for 循环** 用于执行有副作用的操作（如发送邮件、保存文件等）。
* 不要在 `forEach` 终端操作中放入大量副作用代码，这会使代码难以调试和测试。

## 8. 类保持小而专注
一个类**不超过 300 行**，并严格遵守单一职责原则。
* 如果类开始包含“和”这个词（比如 `UserService` 既管用户又管权限），说明该拆分了。
* 提取私有方法，或将部分逻辑委托给专门的组件。

## 9. 测试先行 (TDD 工作流) [暂不启用]
<!-- 在编写任何业务逻辑代码前，**必须先创建或更新测试**。
* 工作流: 编写测试 → 确认红灯 (测试失败) → 编写最小实现 → 确认绿灯 → 重构。
* 测试类使用 JUnit 5，Mock 使用 Mockito，断言使用 AssertJ。 -->

## 10. 善用 Optional，杜绝 null 返回
任何可能返回空值的 Service 方法，都应返回 `Optional<T>`，而不是 `null`。
* 这强制调用方处理缺失情况，从根源上杜绝 `NullPointerException`。
* 仅在方法返回类型上使用 `Optional`，不要用作字段类型或方法参数。

## 11. 配置属性使用强类型
禁止在代码中直接使用 `@Value` 或到处调用 `application.properties` 的 key。
* 必须创建专用的 `@ConfigurationProperties` 配置类，并启用 `@EnableConfigurationProperties`。
* 这使得配置易于管理、类型安全，且便于单元测试。

## 12. 数据库迁移必须版本化
所有数据库模式更改都必须通过 **Flyway** 或 **Liquibase** 进行版本控制。
* 禁止手写 SQL 直接修改数据库结构。
* 迁移脚本应包含清晰的版本号和描述信息，例如 `V1__create_user_table.sql`。

## 13. API 文档保持同步
所有公开的 REST 接口都必须有对应的 **Springdoc OpenAPI** 文档注解。
* 每个 `@RestController` 和方法都必须添加 `@Operation` 和 `@ApiResponses`。
* 任何代码变更如果影响了 API 契约，必须同步更新文档注解。