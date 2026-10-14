# 第三方 skill（vendored）

从外部仓库原样引入的 skill，共 2 个，不属于本仓库 fork 自 mattpocock/skills 的自研血脉（general/ dev/ sdd/），**不参与**与 skills 源仓的同步映射。两 skill 保持上游目录结构互为兄弟，内部相对链接（`../zentao-cli/SKILL.md` 等）依赖这一点，勿拆散。

## 收录清单

| Skill | 版本 | 用途 |
| ----- | ---- | ---- |
| [zentao-cli](./zentao-cli/SKILL.md) | 0.3.1 | 通过 `zentao` 命令查询与维护禅道的产品、项目、需求、任务、Bug，执行状态流转并汇总进展；含安装、配置与排障指引 |
| [zentao-tour](./zentao-tour/SKILL.md) | 0.3.0-beta.1 | 按用户角色（PM/项目经理/测试/开发/高管）带用户上手禅道与 zentao-cli 的对话式导览 |

- **上游**：[easysoft/zentao-cli](https://github.com/easysoft/zentao-cli)（禅道官方，MIT），skill 位于其仓库 `skills/` 目录
- **引入基线**：commit `ce36cf5`（2026-10-04，main 分支 shallow clone）
- **引入日期**：2026-10-14

## 前置依赖

两个 skill 都驱动 `zentao` 命令，需要全局安装 [zentao-cli](https://www.npmjs.com/package/zentao-cli) npm 包：`npm install -g zentao-cli`（或 `bun` / `pnpm`；一次性使用可 `npx zentao-cli`）。凭证**推荐环境变量方式**，由用户自行在 bash 的 `~/.bashrc` 里 export `ZENTAO_URL`、`ZENTAO_ACCOUNT`、`ZENTAO_TOKEN`（或 `ZENTAO_PASSWORD`）后 `source ~/.bashrc`——优先级高于本地配置，密码模式每次现换 Token、无过期问题。备选本机交互登录（新版 `zentao login --web`，旧版本用该版本自己的登录方式）。

## 更新方式

上游迭代后重新整目录覆盖本桶（`zentao add-skill --output <dir>` 或直接从上游 `skills/` 拷贝），并更新本 README 的版本与 commit 基线。除灾难性修复外**不手改**上游内容；确需本地修补时在本 README 记录偏离点，便于下次更新时重新评估。
