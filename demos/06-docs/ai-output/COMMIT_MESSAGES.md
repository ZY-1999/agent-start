# Commit Message 参考（AI 从 git diff 生成）

## 示例 1：单一功能改动

**输入 diff**（新增手机号查重 + 抛出 DuplicatePhoneException）：

```diff
+public UserDto create(UserCreateDto dto) {
+    if (userMapper.existsByPhone(dto.getPhone())) {
+        throw new DuplicatePhoneException(dto.getPhone());
+    }
...
```

**AI 产出：**

```text
feat(user): 注册接口增加手机号唯一性校验

- 创建用户前先按手机号查重，重复时抛 DuplicatePhoneException（409）
- 此前重复手机号依赖数据库唯一索引报错，前端拿到的
  是 500 而非业务错误，无法给出友好提示
```

## 示例 2：应该拆分的 diff

**输入 diff**（同时改了查询逻辑 + 升级了依赖版本）：

```diff
-<artifactId>mybatis-plus-boot-starter</artifactId>
-<version>3.5.3</version>
+<version>3.5.7</version>
...
 public Page<UserDto> list(UserQuery query) {
-    return userMapper.selectAll(query);
+    return userMapper.selectPage(query.toPage(), lambdaWrapper(query));
```

**AI 产出（含拆分建议）：**

```text
refactor(user): 用户列表查询改为分页条件构造器写法

- list() 从 selectAll 改为 selectPage + LambdaQueryWrapper
- 为后续多条件筛选（昵称/手机号/状态）做准备
```

> ⚠️ 这个 diff 建议拆成两个 commit：依赖升级（chore）和查询重构（refactor）
> 分开提交后回滚依赖时不会牵连业务代码：
>
> ```text
> chore(deps): mybatis-plus 3.5.3 -> 3.5.7
> refactor(user): 用户列表查询改为分页条件构造器写法
> ```

## 示例 3：修 bug

**AI 产出：**

```text
fix(order): 修复满减金额计算未包含优惠券的问题

- 满减判断用的是商品小计，未减去优惠券抵扣金额，
  导致用券订单永远达不到满减门槛
- 增加回归测试：用券且满足门槛的订单应同时享受满减
```

## 讲解词要点

- subject 50 字内、动词开头、说清"改了什么"；body 说清 **why** ——AI 的产出就是范本
- **AI 会建议拆 commit**：它比人更早发现"一个 diff 干了两件事"，这是意外收获
- 提交前 Review message 里的动机描述是否属实——AI 只见 diff 不见你的真实意图
