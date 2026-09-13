#!/usr/bin/env bash
# ============================================================
# 生成大日志（现场演示版）：把 35 行样本放大 300 倍 ≈ 1 万行
# 用法：bash gen-big-log.sh  [输出文件]  [倍数]
# ============================================================
set -euo pipefail

HERE="$(cd "$(dirname "$0")/legacy" && pwd)"
OUT="${1:-$HERE/big-access.log}"
TIMES="${2:-300}"

cp "$HERE/access.log" "$OUT"
for ((i = 1; i < TIMES; i++)); do
    cat "$HERE/access.log" >> "$OUT"
done

# 打乱部分行的时间戳，避免"明显复制"的观感（可选）
echo "生成完成：$OUT"
wc -l "$OUT"
