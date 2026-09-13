# 08-project-demo — Agent 说明

## Agent skills

### Issue tracker

本地 markdown issue tracker：issue 存放在根目录 `.scratch/<YYYY-MM-DD>-<feature-slug>/`，是 `spec` / `prd` / `bug` 三类工作的载体。见 `docs/agents/issue-tracker.md`。

### Triage labels

五个规范 triage 角色，标签字符串与角色名一致：`needs-triage`、`needs-info`、`ready-for-agent`、`ready-for-human`、`wontfix`。见 `docs/agents/triage-labels.md`。

### Domain docs

单一上下文（single-context）：根目录一份 `CONTEXT.md` + `docs/adr/`，由 `/domain-modeling` 按需惰性创建。见 `docs/agents/domain.md`。

### CodeMap

探索代码库时从这里开始：`docs/codemap/` 下的项目级地形索引。本仓库尚未生成（空仓库）——首批代码落地后运行 `/codemap` 生成 `docs/codemap/project.md`；之后用 `/codemap` 添加 feature 地图或刷新；不确定地图是否可信时跑 `/codemap` 的 drift-check，报告漂移就更新受影响地图。

### Git contract

分支策略为 **solo**（直接在当前分支开发，不建 feature 分支）；提交信息无额外强制约定。`/sdd-flow` 和 `/tdd` 的提交与分支决策遵循它。见 `docs/agents/git-contract.md`。

### Java 开发规范

Java 代码与工程规范：命名风格、代码格式、OOP / 集合 / 并发规约、Javadoc 注释、应用分层（Web / Service / Manager / DAO）与二方库依赖、系统安全；按【强制】/【推荐】/【参考】分级。写 Java 代码时遵循。见 `docs/agents/JAVA开发规范.md`。

### 数据库规范

MySQL 规约：建表（is_xxx 布尔、必备三字段 id/create_time/update_time、逻辑删除）、索引命名与组合索引、SQL 语句、ORM 映射；按【强制】/【推荐】分级。涉及表结构与 SQL 时遵循。见 `docs/agents/数据库规范.md`。
