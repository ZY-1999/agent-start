# 第三方 skill：禅道（vendored）

禅道官方的 vendored skill，共 2 个，不属于本仓库 fork 自 mattpocock/skills 的自研血脉（general/ dev/ sdd/），**不参与**与 skills 源仓的同步映射。两 skill 保持上游目录结构互为兄弟，内部相对链接（`../zentao-cli/SKILL.md` 等）依赖这一点，勿拆散。Confluence 的 vendored skill 在兄弟桶 [`skills/third-party-confluence/`](../third-party-confluence/README.md)。

## 收录清单

| Skill | 版本 | 用途 |
| ----- | ---- | ---- |
| [zentao-cli](./zentao-cli/SKILL.md) | 0.5.0 | 通过 `zentao` 命令查询与维护禅道的产品、项目、需求、任务、Bug，执行状态流转并汇总进展；含附件读取、工时单位换算、安装、配置与排障指引 |
| [zentao-tour](./zentao-tour/SKILL.md) | 0.5.0 | 按用户角色（PM/项目经理/测试/开发/高管）带用户上手禅道与 zentao-cli 的对话式导览 |

- **上游**：[easysoft/zentao-cli](https://github.com/easysoft/zentao-cli)（禅道官方，MIT），skill 位于其仓库 `skills/` 目录
- **引入基线**：commit `91646a8`（2026-11-20，main 分支 shallow clone）
- **引入日期**：2026-10-14；**最近同步**：2026-11-23（0.3.1 → 0.5.0，整目录覆盖）

## 前置依赖

两个 skill 都驱动 `zentao` 命令，需要全局安装 [zentao-cli](https://www.npmjs.com/package/zentao-cli) npm 包：`npm install -g zentao-cli`（或 `bun` / `pnpm`；一次性使用可 `npx zentao-cli`）。凭证**推荐环境变量方式**，由用户自行在 bash 的 `~/.bashrc` 里 export `ZENTAO_URL`、`ZENTAO_ACCOUNT`、`ZENTAO_TOKEN`（或 `ZENTAO_PASSWORD`）后 `source ~/.bashrc`——优先级高于本地配置，密码模式每次现换 Token、无过期问题。备选本机交互登录（`0.5.0` 为 `zentao auth login --web`，旧版本如 `0.3.1` 用 `zentao login --web`）。

> **版本提示**：skill 已同步到上游 `0.5.0`，但 npm 上的 `zentao-cli` 目前最新为 `0.4.4`（2026-11-12）。`0.5.0` 的部分命令（如 `zentao file read` 附件读取、`zentao setting work-hours` 工时单位、`auth status --verify`）在 `0.4.4` 上可能不可用；skill 一律以当前安装版本的离线帮助（`zentao --version` / `zentao help`）为准，帮助里没有的命令先提示升级 CLI，不要照抄示例。

## 更新方式

上游迭代后重新整目录覆盖本桶（`zentao add-skill --output <dir>` 或直接从上游 `skills/` 拷贝），并更新本 README 的版本与 commit 基线。除灾难性修复外**不手改**上游内容；确需本地修补时在本 README 记录偏离点，便于下次更新时重新评估。
