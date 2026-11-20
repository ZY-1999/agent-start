# 08-project-demo — Agent 说明

order-admin 订单管理演示（Spring Boot 后端 + React 前端，单 jar 部署）；AI 编码工具进入本仓库先读这份说明。

## 工程概述

- 仓库名称：08-project-demo（应用名 order-admin）；形态：前后端同仓的单应用
- 目录与职责：`backend/`（Spring Boot 订单服务）、`frontend/`（React SPA）、`docker/` + `docker-compose.yml`（本地 MySQL 8，宿主 3307）、`docs/`（agents 契约、adr、codemap）、`.scratch/`（本地 issue tracker）
- 技术栈：Java 8（Spring Boot 2.7.18 + MyBatis-Plus 3.5.7 + Flyway）、TypeScript（React 18 + antd 5 + ECharts + Vite）、MySQL 8

## 语言

- 请使用简体中文与我交流，避免机翻，有特定含义的词语可以不翻译；
- 范围包括聊天对话，spec / prd / bug 等 issue，git 提交 message，代码注释等

## 构建与验证

- 环境要求：JDK 9+ 编译器（`--release 8`；本机测试用 JDK 21，环境坑见 `PROJECT_KNOWLEDGE.md`）、Maven 3、Node.js + npm、Docker（本地 MySQL）
- 构建：`mvn package`（prepare-package 阶段自动 npm build 并把 `frontend/dist` 拷进 `classpath:/static`，产出单 jar）
- 改动后验证：后端 `mvn test -DforkCount=0`（中文 Windows 下必须 `forkCount=0`）；前端 `cd frontend && npm run build`（含 tsc 类型检查）
- 运行 / 启动：`docker compose up -d` 后 `ORDER_DB_PASSWORD=order_demo mvn spring-boot:run`，浏览器访问 `http://localhost:8080/orders`
- 测试现状：后端有 MockMvc 集成测试（`OrderControllerApiTest` 等 19 个用例）；前端无自动化测试，前端改动以手工页面验证为准

## Agent Notice

### Clarify, plan, then execute

接到任意需求/任务先澄清、再计划、最后执行，三步不跳：

1. **澄清** —— 用 `/grill-me` 拷问需求，直到目标、边界、验收标准、影响面都明确；当决策需要沉淀为 ADR / 词汇表时改用 `/grill-with-docs`。上下文不全就直接提问，不要猜。
2. **计划** —— 给出分阶段计划：改哪些文件、按什么顺序、怎么验证。等用户确认。
3. **执行** —— 确认后才动手；执行中计划要变，先说明再改。

### Issue tracker

本地 markdown issue tracker：issue 存放在根目录 `.scratch/<YYYY-MM-DD>-<feature-slug>/`，是 `spec` / `prd` / `bug` 三类工作的载体。见 `docs/agents/issue-tracker.md`。

### Triage labels

五个规范 triage 角色，标签字符串与角色名一致：`needs-triage`、`needs-info`、`ready-for-agent`、`ready-for-human`、`wontfix`。见 `docs/agents/triage-labels.md`。

### Domain docs

单一上下文（single-context）：根目录一份 `CONTEXT.md` + `docs/adr/`，由 `/domain-modeling` 按需惰性创建。见 `docs/agents/domain.md`。

### Developer rules

开发规范，适用于人类与 AI。见 `docs/agents/developer.md`。

### Checklist

代码评审 Checklist，用于 review 或需求完成后的检查。见 `docs/agents/checklist.md`。

### CodeMap

探索代码库时从这里开始：`docs/codemap/` 下的项目级地形索引。项目级地图已生成：`docs/codemap/project.md`（入口、模块、数据、验证命令与风险区）；用 `/codemap` 添加 feature 地图或刷新项目地图；不确定地图是否可信时跑 `/codemap` 的 drift-check，报告漂移就更新受影响地图。

### Git contract

分支策略为 **solo**（直接在当前分支开发，不建 feature 分支）；提交信息无额外强制约定。`/sdd-flow` 和 `/tdd` 的提交与分支决策遵循它。见 `docs/agents/git-contract.md`。

### Third-party skills (zentao / confluence)

插件自带第三方技能：`zentao-cli` / `zentao-tour` 通过 `zentao` 命令（`npm install -g zentao-cli`）操作禅道，凭据建议以环境变量（`ZENTAO_URL` / `ZENTAO_ACCOUNT` / `ZENTAO_TOKEN` 或 `ZENTAO_PASSWORD`）在用户自己的 `~/.bashrc` 中导出，本地 profile 登录作为回退；`confluence` 通过 `confluence` 命令（`npm install -g confluence-cli`）操作 Confluence，凭据走 `confluence init` 或 `~/.config/confluence-cli/config.json` 的本地 profile。本机 `zentao` CLI（0.3.1）与 `confluence` CLI（2.30.0）均已安装。

### Java 开发规范

Java 代码与工程规范：命名风格、代码格式、OOP / 集合 / 并发规约、Javadoc 注释、应用分层（Web / Service / Manager / DAO）与二方库依赖、系统安全；按【强制】/【推荐】/【参考】分级。写 Java 代码时遵循。见 `docs/agents/JAVA开发规范.md`。

### 数据库规范

MySQL 规约：建表（is_xxx 布尔、必备三字段 id/create_time/update_time、逻辑删除）、索引命名与组合索引、SQL 语句、ORM 映射；按【强制】/【推荐】分级。涉及表结构与 SQL 时遵循。见 `docs/agents/数据库规范.md`。

## 默认边界

- 项目可以同时拥有 feature 级、spec 级和 project 级知识入口。
- 不要把 agent 运行数据（`.agent-memory/`、本地 SQLite / Milvus Lite db、trace / episode / candidate / asset）提交到仓库。
- 不要提交 `.env`、API key、token 或其他本地凭据。
- 不主动改动 `.gitignore`，包括主动恢复 `.gitignore`；提交和暂存文件时忠实遵守仓库现有 `.gitignore`。

## 最小记忆能力

- 对话中如果感知到稳定、可复用、跨任务会再次影响判断的项目信息或用户偏好，不要只留在聊天里。
- 优先沉淀到项目已有长期知识入口（根目录 `PROJECT_KNOWLEDGE.md`，不存在时自动创建）。
- 若知识属于当前任务过程，先写入 feature 级 / spec 级知识文档；若属于项目长期规则或反复踩坑，再作为 Project Sync Candidate，经确认后同步到长期知识。
- 记录时保持最小、可审查、可溯源：写清事实、来源、适用边界和验证证据；不要写入密钥、私人信息、未验证猜测或整段聊天记录。
- 触发 new chat、handoff、resume pack、上下文压缩或任务收尾时，必须扫描是否有可沉淀知识；没有也要说明"无可同步长期知识"。
