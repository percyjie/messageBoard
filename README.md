# 留言板 · Spring Boot 3 + MyBatis-Plus

原项目是 2016 年的 SSH（Spring MVC + Hibernate + JSP）留言板。本版本保留原有功能，使用 Spring Boot 3、MyBatis-Plus 和 Thymeleaf 重写，并重做了响应式 UI。

## 技术栈

| 层次 | 技术 |
|---|---|
| 后端 | Spring Boot 3.5、Java 17 |
| 持久层 | MyBatis-Plus 3.5、H2（开发）、MySQL 8（生产） |
| 视图 | Thymeleaf、Bootstrap 5.3（本地静态资源） |
| 鉴权 | HttpSession + HandlerInterceptor |
| 密码 | BCrypt（`spring-security-crypto`） |
| 构建 | Maven（jar 打包） |

## 功能

**公开访问**
- 留言墙：按时间倒序展示留言，每页 5 条
- 展示头像、昵称、时间、来源 IP、留言内容
- 未登录访客可以浏览，但不能发布

**普通用户**
- 注册（注册后自动登录）、登录、退出
- 发布留言
- 查看个人资料、修改昵称/邮箱/密码
- 上传/更换头像
- 查看并删除自己的留言

**管理员**
- 独立管理员登录（仅 `role = 2` 账号可用）
- 新增管理员
- 分页查看全部用户、编辑用户资料、删除用户（级联删除其留言）
- 查看并删除任意用户的留言
- 不能删除当前登录的管理员账号

## 演示账号

开发环境（H2）启动时会自动写入演示数据：

| 角色 | 邮箱 | 密码 |
|---|---|---|
| 管理员 | `percyjie@gmail.com` | `123456` |
| 普通用户 | `licyun@qq.com` | `12345678` |
| 普通用户 | `licyun@gmail.com` | `12345678` |
| 普通用户 | `licyun@163.com` | `12345678` |

## 快速启动（H2，无需安装数据库）

要求：JDK 17、Maven 3.9+（IntelliJ IDEA 自带的 Maven 也可以）。

```bash
mvn spring-boot:run
```

浏览器打开 <http://localhost:8080>。

默认使用 dev 配置：H2 内存数据库，重启后数据重置，并自动初始化演示数据。

H2 控制台：<http://localhost:8080/h2-console>
- JDBC URL：`jdbc:h2:mem:messageboard`
- 用户名：`sa`
- 密码：留空

## 使用 MySQL 8

1. 建库建表：

```bash
mysql -u root -p < src/main/resources/db/schema-mysql.sql
```

2. 设置数据库连接（按需修改）：

```bash
# macOS / Linux / Git Bash
export DB_USERNAME=root
export DB_PASSWORD=your-password

# Windows PowerShell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-password"
```

可选环境变量：
- `DB_URL`：覆盖默认 JDBC URL
- `UPLOAD_DIR`：头像存储目录，默认 `./uploads`

3. 使用 prod 配置启动：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

或打包后运行：

```bash
mvn clean package
java -jar target/message-board-1.0.0.jar --spring.profiles.active=prod
```

生产配置默认关闭演示数据。首次部署可以临时把 `src/main/resources/application-prod.yml` 中的 `app.demo-data.enabled` 改为 `true`，启动后生成管理员账号，再改回 `false` 并重启；也可以自行向 `t_user` 表插入 BCrypt 密码哈希。

## 主要配置

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `app.page-size` | `5` | 每页条数 |
| `app.upload-dir` | `./uploads` | 头像存储目录 |
| `app.demo-data.enabled` | dev 为 `true`，prod 为 `false` | 是否初始化演示数据 |
| `spring.servlet.multipart.max-file-size` | `2MB` | 头像大小上限 |
| `server.servlet.session.timeout` | `30m` | 会话超时 |
| `server.port` | `8080` | 端口 |

## 目录结构

```
src/main/java/com/percy/messageboard/
├── config/          # Web、MyBatis-Plus、属性、全局模型、异常处理
├── controller/      # 首页、认证、个人中心、留言、管理后台
├── dto/             # 表单、Session 用户、留言/用户列表投影
├── entity/          # User、Message
├── init/            # 演示数据初始化
├── interceptor/     # 用户/管理员登录拦截
├── mapper/          # MyBatis-Plus Mapper
├── service/         # 业务逻辑
└── util/            # IP、时间、重定向、Session key

src/main/resources/
├── db/              # H2 / MySQL 建表脚本
├── mapper/          # 留言联表分页 SQL
├── templates/       # Thymeleaf 页面与公共片段
└── static/          # app.css、app.js、Bootstrap 5、favicon
```

## 与旧版的差异

这是重写版本，不是旧数据迁移：

- 使用新表 `t_user` / `t_message`，旧 `gbook.sql` 不再使用
- 密码由明文改为 BCrypt，个人中心不再展示密码
- 留言列表改为服务端分页，旧 AJAX 接口 `messageJson-{page}` 已移除
- 旧淘宝 IP 归属地接口早已失效，改为保存并展示客户端 IP，不再发起外部请求
- 昵称长度从“至少 6 位”放宽到 2–20 位，兼容 2–4 字中文名
- 删除用户会级联删除其留言，不再因外键约束报错

## 安全说明

- 所有页面输出使用 `th:text`，自动转义，修复旧版存储型 XSS
- 删除、修改全部使用 POST + PRG，并校验归属关系，修复旧版越权删除
- 上传头像校验文件类型、扩展名与大小，使用 UUID 文件名，防止路径穿越
- 登录成功后轮换 Session ID；退出时销毁会话
- 管理员登录要求 `role = 2`，管理员不能删除自己

已知限制：项目未引入 Spring Security，因此没有 CSRF Token；当前通过 POST-only 变更操作和 `SameSite=Lax` 会话 Cookie 降低风险。如需更严格的安全控制，可在后续引入 Spring Security。
