# Domain Docs

SDD skills 在探索代码库时应如何消费本仓库的领域文档。

## 探索前先读

- 根目录 **`CONTEXT.md`**；或根目录 **`CONTEXT-MAP.md`**（若存在）—— 它指向每个 context 一份 `CONTEXT.md`，读与主题相关的每一份。
- **`docs/adr/`** —— 读与即将工作的区域相关的 ADR；多上下文仓库还需检查 `src/<context>/docs/adr/`。

若这些文件不存在，**静默继续**。不要标记其缺失，也不要预先建议创建。`/domain-modeling`（经 `/grill-with-docs` 触达）会在术语或决策真正落定时惰性创建它们。

## 文件结构（单一上下文）

```
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

## 使用词汇表的术语

当你的输出命名一个领域概念（issue 标题、重构提案、假设、测试名）时，使用 `CONTEXT.md` 定义的术语，不要漂移到词汇表明确回避的同义词。

若所需概念尚不在词汇表中，这是一个信号 —— 要么你在发明项目不用的语言（重新考虑），要么存在真实缺口（记下来交给 `/domain-modeling`）。

## 标记 ADR 冲突

若你的输出与既有 ADR 矛盾，显式提出而不是静默覆盖：

> _与 ADR-0007（event-sourced orders）矛盾 —— 但值得重开，因为……_
