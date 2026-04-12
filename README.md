# 幼儿园资源管理与检索系统

## 目录结构
- `backend`：Spring Boot + MyBatis 后端
- `frontend`：Vue2 + ElementUI 前端
- `sql`：数据库初始化脚本
- `docs`：系统设计说明

## 1. 初始化数据库
```bash
mysql -uroot -p1616116 < sql/init.sql
```

## 2. 启动后端
```bash
cd backend
mvn "-Dmaven.repo.local=.m2" spring-boot:run
```
默认地址：`http://127.0.0.1:8080`

## 3. 启动前端
请先确认 Node 可用（你当前环境路径为 `C:\nvm4w\nodejs`）：
```powershell
cd frontend
& 'C:\nvm4w\nodejs\npm.cmd' install
& 'C:\nvm4w\nodejs\npm.cmd' run serve
```
默认地址：`http://127.0.0.1:8081`

## 默认演示账号
- 园长：`principal` / `123456`
- 教师：`teacher_a` / `123456`
- 保育员：`nurse_a` / `123456`

## 对象存储说明
当前默认 `local` 存储（用于本地开发）。
如需切换阿里云 OSS，请修改 `backend/src/main/resources/application.yml`：
- `app.storage.provider: oss`
- 填写 `app.storage.oss.endpoint/bucket/access-key-id/access-key-secret/base-url`

未配置完整 OSS 参数时，系统会自动回退到本地存储。
