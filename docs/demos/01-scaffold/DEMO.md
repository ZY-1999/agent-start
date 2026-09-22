# Demo 01：脚手架代码（CRUD / DTO / 单元测试）

> 对应讲义章节：[2.1 场景一：脚手架代码](../../index.html#s2-1)

## 目标

用一段结构化的 Prompt，让 AI 一次生成"订单管理"的完整脚手架：实体、DTO（带校验）、Service（CRUD + 过滤）、单元测试。观众亲眼看到：**手写至少 40 分钟的代码，30 秒出骨架**。

## 传统方式 vs AI 方式

| | 传统方式 | AI 方式 |
|---|----------|---------|
| 耗时 | 40 ~ 60 分钟（含测试） | 30 秒生成 + 5 分钟人工检查 |
| 工作内容 | 建 5 个文件、敲几百行样板代码 | 写清楚需求约束、Review 生成结果 |
| 智力含量 | 接近零（纯体力） | 集中在"把需求描述清楚" |

## 演示步骤

1. 打开对话窗口，先给观众看 `task.md` 里的需求（就是平时口头接的需求）
2. 现场输入下方 Prompt（可以提前存成剪贴板，但要口头解释每一段为什么这么写）
3. 生成过程中讲解：AI 在等什么、生成顺序是什么
4. 生成后**当场过一遍关键点**（见"讲解要点"），展示 `ai-output/` 作为对照
5. 如果条件允许：把生成代码贴进 IDE，跑一下测试

## 推荐 Prompt

```text
用 Java 17 + Spring Boot 3（仅 spring-context，不需要 web 和数据库）写一个订单管理的脚手架：

1. Order 实体：id(String)、customerName(String)、amount(BigDecimal)、
   status(OrderStatus 枚举: CREATED/PAID/CANCELLED)、createdAt(LocalDateTime)
2. OrderCreateDto / OrderUpdateDto：
   - customerName 加 @NotBlank，长度 1~64
   - amount 加 @NotNull @DecimalMin("0.00")，精度两位小数
3. OrderService：ConcurrentHashMap 内存存储，提供 create / update / delete / getById /
   list(status 可选过滤)，id 用 UUID，找不到时抛 OrderNotFoundException
4. OrderService 的 JUnit 5 单元测试：覆盖正常流 + 不存在时抛异常 + 状态过滤

要求：
- 遵循《阿里巴巴 Java 开发手册》命名规范
- 每个类生成到独立文件，给出完整文件路径
- 不要引入 Lombok 之外的第三方依赖（Lombok 可用）
```

## 讲解要点（生成时说这些话）

- **"注意我给了什么约束"**：技术栈、字段清单、校验规则、存储方式、异常行为、编码规范——这就是第四章讲的"上下文 + 约束 + 示例"
- **"它不会问我问题，我必须一次说清"**：这正是 Prompt 写作和写需求文档的共同点
- **"现在我的角色变了"**：从"敲代码的人"变成"验收的人"——检查校验注解对不对、异常定义全不全、测试断言是否有效
- 指着 `OrderService.java` 说：**"这段代码没有一行是创造性的，但它去年占了我大概 30% 的编码时间"**

## Review 检查单（现场对照走一遍）

- [ ] 编译通过（幻觉检查的第一道关）
- [ ] 校验注解和需求一致：`@DecimalMin("0.00")` 是否包含负数拒绝
- [ ] `ConcurrentHashMap` 的使用是否正确（AI 有时会用非线程安全的方式生成 id）
- [ ] 测试断言是否有效（有没有"永真断言"）
- [ ] 异常类型是自定义的还是 AI 顺手用了 `RuntimeException`

## 失误预案

- AI 生成了数据库/JPA 依赖 → 展示迭代修正："去掉 JPA，改成内存 Map"，顺便预告第四章"迭代修正"
- 编译不过 → 正好讲第三章"幻觉问题"
- 现场模型不可用 → 直接展示 `ai-output/` 并对照 Prompt 讲解
