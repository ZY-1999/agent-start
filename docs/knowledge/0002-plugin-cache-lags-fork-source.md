---
date: 2026-11-22
source: session 2026-11-22 核实：源码 plugin.json 已 1.1.0，客户端插件缓存仍为 1.0.2
scope: 本仓（zy-skills fork 源码仓）+ ZCode/Claude 客户端插件机制
---

# 插件缓存滞后于 fork 源码

在本仓改 `skills/` 源码**不会立即影响实际执行的 skill**：客户端从插件缓存目录加载（如 `~/.zcode/cli/plugins/cache/zy-skills/.../<version>/`），缓存版本长期滞后于源码——2026-11-22 核实时源码已是 1.1.0，缓存仍停留在 1.0.2。

**Why:** 插件按 marketplace 版本安装与更新，fork 直接 push 源码不触发客户端刷新；只有 bump `plugin.json` 版本并在客户端更新/重装插件后，新行为才生效。

**Boundary:** 只影响"验证 skill 新行为"的场景；读源码、改源码本身不受影响。

**Evidence:** 2026-11-22 会话核实：本仓已同步 1.1.0（commit f47022b），上游 zy-skills fork 仓对应推送为 6d50cf8（属上游仓历史，不在本仓），缓存目录仍为 1.0.2，新增的 `/spec-sync`、`/spec-check` 在客户端不可见。

**Action:** 验证新 skill 行为前，先确认缓存版本号与 `plugin.json` 一致；发现行为与源码不符时，优先怀疑缓存滞后而不是改错地方。
