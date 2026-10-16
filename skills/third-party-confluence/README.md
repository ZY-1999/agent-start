# 第三方 skill：Confluence（vendored）

从外部仓库原样引入的 skill，共 1 个，不属于本仓库 fork 自 mattpocock/skills 的自研血脉（general/ dev/ sdd/），**不参与**与 skills 源仓的同步映射。

## 收录清单

| Skill | 版本 | 用途 |
| ----- | ---- | ---- |
| [confluence](./confluence/SKILL.md) | 2.30.0（本地精简版） | 通过 `confluence` 命令读取、搜索、创建、更新、移动、删除 Confluence 页面与附件，并做格式转换；覆盖 CLI 全部子命令、配置与常见工作流 |

- **上游**：[pchuri/confluence-cli](https://github.com/pchuri/confluence-cli)（MIT），skill 位于其仓库 `plugins/confluence/skills/confluence/`
- **引入基线**：commit `6c0d4af`（2026-10-14，v2.30.0，main 分支 shallow clone）
- **引入日期**：2026-10-16

## 本地偏离记录

- 2026-10-16：应用户要求，将上游 SKILL.md（907 行英文原版）重写为 zy 风格中文精简版（约 150 行）——按 agent 工作流重组（开始工作 / 定位页面 / 命令速查 / 执行与核实 / 错误处理），删除 Pi Coding Agent 专属内容（工具面、protected writes 审批流）、面向人的安装引导与 `stats` / `install-skill` 等与任务无关的命令，并修正上游 Agent Tips 与 Page ID Resolution 关于 display URL 是否支持的自我矛盾（以「支持但按标题精确匹配、不可靠」为准）。**CLI 命令语义未做任何改动**，仍以上游 CLI 2.30.0 为准。

## 前置依赖

skill 驱动 `confluence` 命令，需要全局安装 npm 包：`npm install -g confluence-cli`。凭证用本机配置文件 `~/.config/confluence-cli/config.json`（或用 `confluence init` 交互生成），示例：

```json
{
  "activeProfile": "ongoal",
  "profiles": {
    "ongoal": {
      "domain": "10.2.16.235:8090",
      "protocol": "http",
      "allowInsecureHttp": true,
      "authType": "bearer",
      "token": "<your-auth-token>"
    }
  }
}
```

更多初始化方式见上游 <https://github.com/pchuri/confluence-cli>。

## 更新方式

上游迭代后**不要**直接整目录覆盖——本桶 SKILL.md 已本地重写（见上方偏离记录）。正确做法：对照上游新版 CLI 的 `--help` 与 README，人工比对新增/变更的命令语义，合入本桶精简版结构；若上游重写了 skill，再评估是否重新引入并重做精简。确需再手改时继续在本 README 追加偏离点。
