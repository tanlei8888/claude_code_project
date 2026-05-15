---
paths:
  - "hedgehog-server/**"
---

# 后端代码注释规范

编写 Java 代码时必须自动添加注释。本规范适用于 hedgehog-server 所有 Java 源文件。

## 通用原则

1. **必须使用中文注释**
2. **注释必须准确反映代码意图**，不得猜测或编造
3. **简洁优先**：一句话能说清的不写两句
4. **不自解释的不注释**：如 `getUserId()` 不需要"获取用户ID"
5. **所有新编写的 Java 文件必须包含注释**

## 类注释（必须）

每个类、接口、枚举、注解必须包含类级别 Javadoc：

```java
/**
 * [一句话类职责描述]。
 *
 * <p>[可选补充说明]。
 */
```

## 方法注释（必须）

所有 public/protected 方法必须有 Javadoc：

```java
/**
 * [方法功能描述]。
 *
 * @param paramName [参数说明]
 * @return [返回值说明]
 * @throws XxxException [异常触发条件]
 */
```

以下情况可省略方法注释：
- `@Override` 方法且父类/接口契约已充分说明
- 简单的 getter/setter
- MyBatis Plus Mapper 接口的空方法（由框架自动生成实现）

## 字段注释（按需）

字段含义不明显时必须注释：

```java
/** 文章状态：0=草稿 1=已发布 2=定时发布 3=私密 */
private Integer status;

/** 非数据库字段：关联标签列表 */
@TableField(exist = false)
private List<BlogTag> tags;
```

枚举常量必须逐项注释含义。

## 行内注释

仅用于非显而易见的逻辑、特殊处理或算法步骤：

```java
// 未登录用户跳过点赞状态查询
if (userId == null) return;
```

## 各层级注释要求

| 层级 | 类注释 | 方法注释 | 字段注释 |
|------|:------:|:------:|:------:|
| common / util / exception | 必须 | public 方法必须 | 必须 |
| config | 必须 | @Bean 方法 / 关键逻辑必须 | 注入字段按需 |
| entity（DB 实体） | 必须 | 不适用 | 每个字段必须（说明数据库含义） |
| DTO / Request | 必须 | 不适用 | 每个字段必须 |
| mapper | 必须 | 不适用 | 不适用 |
| service 接口 | 必须 | 必须（契约定义） | 不适用 |
| service 实现 | 必须 | 非覆盖方法必须，覆盖方法按需 | 注入字段按需 |
| controller | 必须 | 必须（标注 HTTP 方法+路径） | 注入字段按需 |

## 反例（禁止的注释风格）

```java
// ❌ 废话注释
/** 设置用户名 */
public void setUsername(String username) { ... }

// ❌ 英文注释（本项目统一使用中文）
/** Get user by id */
public User getById(Long id) { ... }

// ❌ 猜测性注释
/** 可能用于权限检查？ */
private String role;
```
