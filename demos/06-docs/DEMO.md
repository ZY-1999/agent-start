# Demo 06：文档（接口文档 / README / Commit Message）

> 对应讲义章节：[2.6 场景六：文档](../../index.html#s2-6)

## 目标

展示"从代码生成自然语言"这一 AI 舒适区：所有人都同意文档重要、所有人都懒得写，现在写文档的成本趋近于零。

三个子任务一口气演示：

1. Controller 源码 → 接口文档（Markdown）
2. `git diff` → 规范化 Commit Message
3. 项目结构 → README 片段

## 传统方式 vs AI 方式

| | 传统方式 | AI 方式 |
|---|----------|---------|
| 耗时 | 每份 30 分钟 ~ 2 小时（所以永远没写） | 每份 30 秒 |
| 质量 | 赶时间随便写， gradually 过期 | 结构完整，且改完代码可以秒级重新生成（文档不过期的关键） |

## 演示步骤

### 子任务 1：接口文档

1. 打开 `legacy/UserController.java`——常见 的 Spring MVC Controller
2. 用 Prompt A 生成文档，对照 `ai-output/API.md`
3. 强调：**核对默认值、错误码、必填项**这些事实性描述（AI 会从代码里读，但读漏了没人兜底）

### 子任务 2：Commit Message

1. 现场对一个假想的改动跑 `git diff`（或直接展示提前准备的 diff）
2. 用 Prompt B 生成，对照 `ai-output/COMMIT_MESSAGES.md`
3. 讲 Conventional Commits 的类型怎么选——AI 选得比大多数人准

### 子任务 3：README

1. 用 Prompt C，对照 `ai-output/README-snippet.md`

## 推荐 Prompt

**A. 接口文档：**

```text
把下面的 Spring MVC Controller 转成接口文档（Markdown），要求：
1. 每个接口一个章节：方法+路径、用途、参数表（名/类型/必填/说明）、
   成功响应示例、错误响应（从异常处理推断错误码）
2. 从 @Valid 注解推断参数校验规则并写进参数表
3. 不确定的（如分页默认值代码里没有）标注"待补充"而不是编造
4. 面向的读者是前端和第三方接入方

[粘贴 UserController.java]
```

**B. Commit Message：**

```text
根据下面的 git diff 写一条 Conventional Commits 格式的提交信息：
- type 从 feat/fix/refactor/docs/test/chore 里选
- subject 用中文，50 字以内，动词开头
- body 列出关键变更点（bullet），说明 why 而不只是 what
- 如果这个 diff 应该拆成多个 commit，直接告诉我拆分建议

[粘贴 git diff 输出]
```

**C. README：**

```text
根据下面的目录结构和构建脚本，为这个模块写 README 的"快速开始"章节：
包含环境要求、本地启动步骤、目录结构说明表。启动命令必须真实存在（从脚本里读），不要编造。

[粘贴目录树 + pom.xml 关键段落]
```

## 讲解要点

- **"这类任务为什么是舒适区"**：从代码（结构化）到文档（自然语言）是单向翻译，几乎没有"创造"成分，AI 失误率很低
- **"文档终于可以不过期了"**：以前文档烂是因为改完代码没人记得改文档；现在"改完代码重新生成一遍"的成本是 30 秒
- **注意 Prompt A 第 3 条**："不确定的标注待补充而不是编造"——把第三章的幻觉问题提前堵住
- **Commit Message 的隐藏收益**：AI 会建议你拆 commit——它比你更早发现"这个 diff 干了两件事"

## 失误预案

- AI 编造了代码里不存在的错误码 → 讲幻觉问题：事实性内容必须核对
- 文档风格和团队模板不一致 → 投喂团队模板作为示例，呼应第四章实践一"给示例"
