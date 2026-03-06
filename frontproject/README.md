# 论坛系统 (Forum System)

> 我的第一个全栈项目 - 一个模仿柏码论坛设计的前后端分离社区系统

## 📝 项目简介

这是我从零开始学习 SpringBoot + Vue 全栈开发时完成的第一个完整项目。项目灵感来自柏码论坛，很多地方都是照猫画虎做的。当时对做项目没什么经验，项目结构基本都是模仿人家的，但还是增长了不少经验，后来做的项目就比较有自己的规划了。

项目采用前后端分离架构，实现了一个功能完善的论坛系统，包含用户管理、帖子发布、评论互动等核心功能。

## ⚠️ 项目状态

**本项目已停止维护**

这个项目其实还存在一些 bug，不过不是代码本身的问题，而是一些依赖库更新后变得不兼容了。比如图片上传功能，因为调用库的 bug 会导致图片以 base64 的方式直接存入数据库，而不是上传到 OSS 再把 URL 存入数据库。

不过我觉得到这里就可以了，毕竟是一个学习和实践的项目，学到东西就够了。对我来说这个项目的使命已经完成，用这些经验去构建新项目的价值，比重新调试一个初学者的模仿向旧项目要大得多。

## ✨ 主要功能

### 用户模块
- 用户注册/登录/登出
- 邮箱验证码注册
- 密码找回（邮箱验证）
- 个人信息管理
- 隐私设置

### 论坛模块
- 帖子发布（支持富文本编辑）
- 帖子分类管理
- 帖子浏览/编辑/删除
- 评论功能
- 点赞/收藏

### 其他功能
- 图片上传（MinIO 对象存储）
- 天气信息展示
- 接口限流防刷
- JWT 无状态认证

## 🛠️ 技术栈

### 后端 (backproject)
- **框架**: Spring Boot 3.4.2
- **语言**: Java 17
- **安全**: Spring Security + JWT
- **数据库**: MySQL + MyBatis Plus
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **对象存储**: MinIO
- **邮件**: Spring Mail
- **API 文档**: SpringDoc OpenAPI

### 前端 (frontproject)
- **框架**: Vue 3 + Vite
- **UI 组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP 客户端**: Axios
- **富文本编辑器**: Quill

## 📦 项目结构

```
myproject/
├── backproject/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── filter/          # 过滤器
│   │   │   │   ├── listener/        # 监听器
│   │   │   │   ├── Mapper/          # 数据访问层
│   │   │   │   ├── service/         # 业务逻辑层
│   │   │   │   └── util/            # 工具类
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   └── pom.xml
│
└── frontproject/         # 前端项目
    ├── src/
    │   ├── components/   # 组件
    │   ├── router/       # 路由配置
    │   ├── views/        # 页面视图
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── vite.config.js
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis
- RabbitMQ
- MinIO

### 后端启动

1. 配置数据库和中间件
```bash
# 修改 backproject/src/main/resources/application.yml
# 配置 MySQL、Redis、RabbitMQ、MinIO 连接信息
```

2. 启动后端服务
```bash
cd backproject
mvn spring-boot:run
```

后端服务将运行在 `http://localhost:8081`

### 前端启动

1. 安装依赖
```bash
cd frontproject
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

3. 构建生产版本
```bash
npm run build
```

## 📊 数据库设计

主要数据表：
- `account` - 用户账户
- `account_details` - 用户详细信息
- `account_privacy` - 隐私设置
- `topic` - 帖子
- `topic_type` - 帖子分类
- `topic_comment` - 评论
- `store_image` - 图片存储记录
- `interact` - 用户交互记录（点赞/收藏）

## 🔐 安全特性

- JWT Token 认证
- Spring Security 权限控制
- 接口限流防刷
- CORS 跨域配置
- 密码加密存储

## 💡 学习收获

通过这个项目，我学习并实践了：
- Spring Boot 全家桶的使用
- 前后端分离架构设计
- RESTful API 设计规范
- JWT 认证机制
- Redis 缓存应用
- 消息队列的使用
- 对象存储服务集成
- Vue 3 组合式 API
- 现代前端工程化

## 📄 许可证

MIT License

---

⭐ 如果这个项目对你有帮助，欢迎 Star！
