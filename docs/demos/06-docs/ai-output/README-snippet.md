# user-service · 快速开始（README 片段参考）

## 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | `pom.xml` 中 `maven.compiler.release=17` |
| MySQL | 8.0+ | 需要 `utf8mb4` |
| Maven | 3.8+ | 构建工具 |

## 本地启动

```bash
# 1. 初始化数据库（脚本位于项目内，非编造）
mysql -u root -p < scripts/schema.sql

# 2. 复制配置模板并填入本地数据库信息
cp config/application-local.example.yml config/application-local.yml

# 3. 启动（dev profile）
./scripts/run.sh dev

# 4. 冒烟验证
curl http://localhost:8080/actuator/health
```

## 目录结构

```
user-service/
├── scripts/          # schema.sql / run.sh（本地启动脚本）
├── config/           # 环境配置模板与本地配置（不入库）
├── src/main/java/
│   └── com.acme.user/
│       ├── controller/   # REST 接口层
│       ├── service/      # 业务逻辑层
│       ├── mapper/       # MyBatis-Plus 数据访问层
│       ├── dto/          # 请求/响应对象
│       └── exception/    # 业务异常与全局错误处理
└── src/test/java/        # 单元测试与集成测试
```

---

> 生成说明：所有命令均来自 `scripts/` 目录中的真实脚本，目录结构与实际工程一致。
> AI 被明确要求"命令必须真实存在，不要编造"——README 中不存在幻觉的容身之处。
