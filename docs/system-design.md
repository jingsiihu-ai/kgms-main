# 系统设计说明

## 技术栈
- 后端：Spring Boot 2.7.x、Spring Security、MyBatis、JWT、MySQL 5.7
- 前端：Vue2、Vue Router、Vuex、ElementUI、Axios
- 存储：StorageService 抽象（Local / Aliyun OSS）

## 核心模块
1. 认证与权限：JWT + RBAC（三角色：园长/教师/保育员）。
2. 资源全生命周期：多文件上传、元数据提取、分类绑定（班级/主题/标签）、可见性控制。
3. 多维智能检索：组合条件 + 关键词全文检索（MySQL FULLTEXT）。
4. 便捷入口：热门标签、最近上传、我的收藏。

## 可见性策略
- `PUBLIC`：所有登录用户可见。
- `CLASS_ONLY`：仅同班用户可见。
- `PRIVATE`：仅上传者可见。
- `PRINCIPAL` 角色可查看全部资源。

## 元数据提取
- 基础字段：文件名、类型、大小、MIME。
- 图片增强：EXIF 标签抽取（部分关键项）。
- 内容检索：通过 Tika 提取可检索文本，写入 `searchable_text`。

## 数据库要点
- `kg_resource` 建立全文索引：`title, original_filename, description, searchable_text`。
- 多对多关系表：`kg_resource_class/theme/tag`。
- 收藏表：`kg_favorite`，`(user_id, resource_id)` 唯一约束。

## 主要 API
- 认证：`POST /api/auth/login`、`GET /api/auth/me`
- 用户：`GET/POST/PUT /api/users`、`GET /api/roles`
- 基础数据：`/api/classes`、`/api/themes`、`/api/tags`
- 资源：上传、详情、更新、删除、下载、搜索、最近上传、热门标签
- 收藏：收藏/取消收藏、`GET /api/users/me/favorites`
