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
