# Project Knowledge

构建与测试环境要点（2026-10-14 验证）：

- **编译/测试 JDK**：`maven.compiler.release=8` 需要 JDK 9+ 编译器；sdkman 默认 current 是 JDK8 会报 `无效的标记: --release`。用 `export JAVA_HOME=~/.sdkman/candidates/java/21.0.12-tem`。
- **surefire fork 崩溃**：中文 Windows（GBK）下 surefire 2.22.2 fork 的 JVM 管道直接断（dumpstream 里大量 `Unexpected IOException: I,1,...`），Tests run: 0。跑法：`mvn test -DforkCount=0`。
- **测试库**：`application-test.yml` 连 `jdbc:mysql://localhost:3307/order_db_test`，root/order_demo，由仓库根 `docker-compose.yml` 提供（`docker compose up -d`），healthy 后再跑测试。
- **本地起服务**：`ORDER_DB_PASSWORD=order_demo mvn spring-boot:run`（主库默认密码为空，连不上 compose 里的 MySQL）；8080 端口。
- **前端改动联调**：`mvn spring-boot:run` 用的是 `target/classes/static` 的旧拷贝，`npm run build` 后需重新拷贝 `frontend/dist` 到 `backend/target/classes/static/`（`mvn package` 会自动做，spring-boot:run 不会）。

## Candidates（待确认是否入库）

- `.gitignore`（仓库根，内容仅 `/docs/demos/08-project-demo/backend/target/`）：会话中由 IDE 自动生成并混入暂存区，已从 72eeddb 提交中拆出，现留在工作区——是否保留由维护者决定。
