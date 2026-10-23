# 开发规范（Developer）

本仓库的编码与文档规范，人类与 AI 同样适用。主体是 skill 资产规范；通用条目同样适用于脚本、讲义与文档。

## 一、通用规范

### 命名

- 名字说明"是什么"，不说明"怎么实现的"；避免只有作者懂的缩写。
- 同一概念在 skill、文档、issue 里用同一个词；换词等于换概念。

### 文档

- 注释与说明解释"为什么"，不复述"做了什么"。
- 改了行为就同步改文档；文档与实现冲突时以实现为准并当场修文档。

### 红线

- 禁止在文件、注释、日志、示例数据中出现真实密钥与客户数据。
- 禁止执行 `rm -rf`、`DROP`、`git push --force` 等破坏性命令。
- 禁止自动 push，必须人工确认。
- 不主动改动 `.gitignore`；提交与暂存忠实遵守仓库现有忽略规则。

### 提交前

- 改动必须实际验证（打开讲义、装一次插件、跑一次脚本），验证不通过不提交。
- 本仓库无自动化测试，不强行补测试框架，但要说清手工验证做了什么。

## 二、Skill 资产规范

本仓库的核心资产是 `skills/` 下的 skill。改 skill 按这一节。

### 目录与命名

- 一个 skill 一个目录，目录名即 skill 名（kebab-case）。
- 入口固定 `SKILL.md`；细则拆到同目录 `references/`，最多一层，不再套子目录。

### frontmatter

- `name` 与目录名一致。
- `description` 写"什么时候该用我"，含用户会说的触发词；不写"我内部做了什么"。
- 只有需要用户显式调用的 skill 才加 `disable-model-invocation: true`（如 `/setup-skills`）。

### 渐进披露

- `SKILL.md` 只放决策点与流程骨架；长清单、格式模板、完整示例放 `references/`。
- `SKILL.md` 明显过长（约 500 行以上）就该拆；拆出来的内容要能被单独读懂。

### 指令写法

- 用祈使句写动作，不写"你可以考虑…"这类无约束表述。
- 每个可判定步骤给出可验证的完成条件。
- 明确"何时必须停下来问用户"，以及停下来要问哪几件事。
- 部署到目标仓库的模板（如 `/setup-skills` 的 AGENTS.md 骨架）用中文，机器可读 token 保持原样。

### 多平台清单与版本

改 skill 内容必须同步以下五处，漏一处该平台拿到的就是旧内容：

- `.claude-plugin/plugin.json`
- `.codebuddy-plugin/plugin.json` 与 `.codebuddy-plugin/marketplace.json`
- `.cursor-plugin/plugin.json`
- `.zcode-plugin/plugin.json`

版本号遵循 semver：新增或改变行为升 minor，纯修正升 patch。改了 skill 结构或清单，同步 `docs/codemap/project.md`。

### 第三方 vendored skill

`skills/third-party-zentao/` 与 `skills/third-party-confluence/` 是原样引入的外部 skill。不改写其内部风格；升级时记录来源仓库与 commit，并同步本仓库版本号。
