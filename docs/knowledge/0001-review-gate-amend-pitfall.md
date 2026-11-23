---
date: 2026-10-13
source: zy-skills 与 agent-start 两仓实测（review 门禁 hook v1.0.2 上线时）
scope: 装有 zy-skills review 门禁 PreToolUse hook 的仓库
---

# review 门禁下 amend 会带入无关暂存

在 review 门禁 hook 生效的仓库里，`git commit --amend` 被放行后会把**当时暂存区的全部内容**并入目标提交——包括与本次修正无关、只是碰巧 staged 的文件。

**Why:** 门禁的哈希标记按提交内容放行；amend 重写的是整个提交对象，暂存区里任何额外内容都会随之进入历史，且不易察觉。

**Boundary:** 普通 `git commit`（非 amend）不受影响；未装该 hook 的仓库不适用。

**Evidence:** 2026-10-13 hook 上线（v1.0.2）时在 zy-skills、agent-start 两仓实测发现；机制见 review skill 第 6 步对 diff 哈希的约定。

**Action:** amend 前先 `git status` 确认暂存区只含本次要并入的内容；不确定就改用新提交。
