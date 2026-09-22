#!/usr/bin/env bash
# ============================================================
# Demo 07 参考答案（AI 生成的典型产出，已人工验证可运行）
# 用法：cd 到本目录后执行  bash solution.sh
# ============================================================
set -euo pipefail

cd "$(dirname "$0")/../legacy"

echo "================ 任务 1：Top 5 IP ================"
# 原理拆解：
#   grep -oE '[0-9]{1,3}(\.[0-9]{1,3}){3}'  -o 只输出匹配到的部分（而不是整行），
#                                          -E 扩展正则，{1,3} 不用写 \{1,3\}
#   [0-9]{1,3}                             一段 1~3 位数字
#   (\.[0-9]{1,3}){3}                      后面跟三组 ".数字"
#   sort | uniq -c                          排序后把相邻重复行计数合并（uniq 只处理相邻行，所以必须先 sort）
#   sort -rn                                按数字逆序（-r 逆序 -n 按数值，否则 "10" 会排在 "9" 前面）
#   head -5                                 取前 5
# 坑：如果日志里有端口（10.0.0.1:8080），这个正则会把 8080 之后的数字段误伤，
#     更稳的写法可以锚定到第 5 列：awk '{print $5}' access.log | sort | uniq -c | sort -rn | head -5
grep -oE '[0-9]{1,3}(\.[0-9]{1,3}){3}' access.log | sort | uniq -c | sort -rn | head -5

echo
echo "================ 任务 2：status=500 的时间戳 + 接口名 ================"
# 原理拆解（awk 方案，最稳）：
#   /status=500/          只处理匹配该正则的行
#   $1 " " $2             第 1、2 列：日期、时间（默认按空白分列）
#   "\t" $6               制表符 + 第 6 列（HTTP 方法）
#   $7                    第 7 列（接口路径）
#   --errorexit?          不需要；awk 匹配不到行就无输出，退出码仍为 0
awk '/status=500/ {print $1 " " $2 "\t" $6 " " $7}' access.log

# 纯 grep -oE 备选（如果非要一条 grep）：
#   grep -oE '^[0-9-]+ [0-9:]+.*?(GET|POST|PUT|DELETE|PATCH) /api/[a-z]+' access.log
#   坑：grep 基本正则不支持 \d，必须用 [0-9]；懒惰匹配 .*? 需要 -P（PCRE）

echo
echo "================ 任务 3：env=test -> env=prod（先备份 + diff） ================"
# 原理拆解：
#   cp config.properties config.properties.bak        第一步：备份
#   sed -i.bak 's/env=test/env=prod/g' config.properties
#       -i.bak    原地编辑，同时把原始内容存为 config.properties.bak
#                 （与手动 cp 二选一即可，这里为了演示"两种备份方式"都写）
#       s/.../.../g   g 标志：替换行内所有匹配，不写 g 每行只替换第一处
#   坑：macOS 是 BSD sed，-i 后面必须跟后缀参数：sed -i.bak 's/.../.../g'
#       GNU sed 的 -i 后缀可省略——跨平台脚本必须注意
# 坑：s/env=test/env=prod/g 若不加单词边界，会误伤 env=test2 之类的值；
#     更严谨的写法：sed 's/env=test$/env=prod/'  （行尾锚定，properties 一行一个键值对）
cp config.properties config.properties.bak
sed -i 's/env=test/env=prod/g' config.properties

echo "---- 替换后 diff（左：< 旧文件，右：> 新文件）----"
# 注意看 diff：注释行里的 "env=test" 也被替换了！
# 这就是"没有锚定边界会误伤"的活教材——更严谨的写法见上方 $/ 锚定说明
diff config.properties.bak config.properties || true
echo
echo "备份文件：config.properties.bak"
