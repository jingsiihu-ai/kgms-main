# KGMS 改造说明

这份项目包已经按“PC 端目录树 + 与手机端共享数据库”的方向整理。

## 已完成的主线改造

### 数据库
- `sql/init.sql` 已替换为新的统一数据库脚本。
- 共用表以 `users / resources / tags / resource_tags / tasks ...` 为主。
- PC 端专属表为 `kg_category / kg_favorites`。

### 后端
- 登录认证改为读取 `users` 表。
- 角色改为使用 `role_id`，映射为 `PRINCIPAL / PARENT / TEACHER / NURSE`。
- 资源主逻辑改为读取 `resources + tags + resource_tags + kg_category + kg_favorites`。
- 新增目录树接口：
  - `GET /api/categories/tree`
  - `POST /api/categories`
  - `PUT /api/categories/{id}`
  - `DELETE /api/categories/{id}`
- 标签字典改为直接操作共享 `tags` 表。

### 前端
- 新增目录管理页面 `CategoryManage.vue`。
- 资源中心改为目录树 + 筛选 + 资源卡片 + 详情抽屉。
- 用户管理改为使用 `users` 表字段：`username / name / phone / roleId / status`。
- 基础字典页改为只维护 `tags`。

## 需要你本地重点核对的两件事

### 1. 后端数据库配置
`backend/src/main/resources/application.yml`

默认已改成：
- 数据库名：`youeryuan`
- 密码：`your_password`

你需要替换成你本机实际 MySQL 配置。

### 2. 前后端联调前先导入新 SQL
执行：

```bash
mysql -uroot -p youeryuan < sql/init.sql
```

## 本环境中的验证情况

- 前端 `npm run build` 已成功通过。
- 当前环境没有 Maven 命令，所以我没法在这里完整跑 Spring Boot 编译；后端代码已经按当前仓库结构做了成套调整，但你本地仍需要执行一次 `mvn spring-boot:run` 或 `mvn package` 做最终验证。

## 如果本地启动后还有问题，优先检查

1. MySQL 表结构是否确实来自新的 `sql/init.sql`
2. `users.password` 是否仍使用 BCrypt
3. 上传目录是否可写
4. 手机端是否也连接到同一数据库
5. 手机端对 `resources.resource_type` 的约定是否仍为：
   - `0=图片`
   - `1=视频`
   - `2=文档`
