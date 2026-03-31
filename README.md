# 兴趣社交圈子

基于前后端分离架构的兴趣社交平台，让用户发现志同道合的人。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vue Router + Pinia + Element Plus + WangEditor |
| 后端 | Spring Boot 3.2 + Spring Security + JWT + Spring Data JPA |
| 数据库 | MySQL 8.0 |

## 核心功能

- **用户系统**：邮箱注册登录，JWT Token 鉴权
- **内容发布**：富文本编辑器，多图上传
- **信息流**：帖子列表，无限滚动加载
- **互动功能**：点赞、收藏、评论及回复
- **个人主页**：用户资料编辑，帖子/收藏展示

---

## 环境要求

| 组件 | 版本建议 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |

---

## 项目环境部署流程

以下顺序适用于**全新机器**或**新服务器**从零部署；路径以仓库根目录 `interest-circle/` 为准。命令默认在 **Linux / macOS（bash）** 下执行；**Windows 桌面开发请看下文「Windows 环境部署流程」**。

### 1. 安装基础软件

在 Linux（如 Ubuntu）上示例：

```bash
# MySQL（按发行版选择安装方式，以下为示意）
sudo apt update && sudo apt install -y mysql-server openjdk-17-jdk maven

# Node.js 建议使用 nvm 或官方 LTS 安装包，确保 node -v >= 18
```

确认版本：

```bash
java -version   # 需为 17
mvn -v
node -v
mysql --version
```

### 2. 启动 MySQL 并创建数据库

```bash
# 视环境选择其一：systemd 或 service
sudo systemctl start mysql
# 或：sudo service mysql start
```

创建库（字符集与 `application.yml` 中一致）：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS interest_circle DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

为应用单独建账号（生产环境推荐，示例）：

```sql
CREATE USER 'circle'@'localhost' IDENTIFIED BY '你的强密码';
GRANT ALL PRIVILEGES ON interest_circle.* TO 'circle'@'localhost';
FLUSH PRIVILEGES;
```

然后在 `backend/src/main/resources/application.yml` 中把 `spring.datasource.username` / `password` 改成上述账号。

### 3. 配置后端

编辑 `backend/src/main/resources/application.yml`：

- **数据源**：`url`、`username`、`password` 指向你的 MySQL。
- **JWT**：生产环境务必修改 `app.jwt.secret`。
- **HTTPS**：默认启用内嵌 `keystore.p12`；本地前端 `vite.config.js` 里代理指向 `https://localhost:8080`，浏览器需信任自签证书或按需关闭 SSL（仅开发环境）。
- **文件上传**：`app.upload.path`（默认 `./uploads/`）在部署目录下需存在且进程可写：

```bash
mkdir -p backend/uploads
```

第三方能力（腾讯云 IM、七牛等）按实际密钥填写；**勿将真实密钥提交到公开仓库**，生产建议用环境变量或外部配置中心。

### 4. 生成数据库表结构

当前 `spring.jpa.hibernate.ddl-auto` 为 `update`，**首次启动后端**时会根据 JPA 实体自动建表/增量更新。

```bash
cd backend
mvn -q -DskipTests package
mvn spring-boot:run
# 或使用：java -jar target/*.jar
```

看到应用正常监听 `8080` 且无 JPA 报错后，表已就绪。根目录下的 `schema.sql` 为早期表结构参考，**可能与当前实体不完全一致**，优先以 JPA 为准。

### 5. 部署数据库数据（演示 / 测试数据）

仓库提供 `backend/test-data.sql`，包含用户、圈子、帖子、评论、点赞、收藏、关注、通知等**演示数据**，用于联调或展示环境。

**导入前注意：**

1. 必须先完成上一步，保证**所有表已存在**。
2. 脚本中部分记录引用了 **用户 `id = 1`**（如圈子成员、关注关系）。请先通过**前端注册**或接口创建第一个用户，使其主键为 `1`，再导入；若库里已有其它数据导致 `id` 不是 `1`，需相应修改 `test-data.sql` 中的外键或先插入 `id=1` 的用户行。
3. 脚本使用固定主键插入；若表中已有冲突数据，请先**清空相关表**或仅在空库上使用。

导入命令（在仓库根目录执行，密码按实际修改）：

```bash
mysql -u root -p interest_circle < backend/test-data.sql
```

脚本开头的 `@pwd` 为 BCrypt 哈希，**所有演示用户使用同一哈希**；若需统一成已知明文密码，可在后端用 `BCryptPasswordEncoder` 生成新哈希替换 `@pwd` 后重新导入。

### 6. 从已有环境迁移整库数据（生产常用）

若要把**线上一整库**迁到新机器，使用官方逻辑备份即可：

**在源库导出：**

```bash
mysqldump -u root -p --single-transaction --routines --triggers interest_circle > interest_circle_dump.sql
```

**在目标机导入：**

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS interest_circle DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p interest_circle < interest_circle_dump.sql
```

导入后检查 `application.yml` 与目标 MySQL 地址、账号一致，再启动应用。大表可加压缩与并行参数，按运维规范调整。

### 7. 启动前端

```bash
cd frontend
npm install
npm run dev
```

开发环境默认 <http://localhost:5173>；若后端部署在其它主机或关闭 HTTPS，请同步修改 `frontend/vite.config.js` 里 `proxy` 的 `target`。

**生产构建：**

```bash
cd frontend
npm run build
```

将 `dist/` 交给 Nginx、OSS 或任意静态资源服务；配置反向代理将 `/api`、`/uploads`、`/ws` 指到后端地址。

---

## Windows 环境部署流程

在 **Windows 10/11** 上本地开发与运行的推荐顺序。若使用 **WSL2**，可直接按上文 Linux 流程在 WSL 终端中操作。

### 1. 安装运行环境

任选其一安装（需把各软件的 `bin` 目录加入 **系统环境变量 Path**，新开终端后生效）：

| 软件 | 建议方式 |
|------|----------|
| **JDK 17** | [Eclipse Temurin 17](https://adoptium.net/) 或 Oracle JDK 17 安装包 |
| **Maven** | [Apache Maven](https://maven.apache.org/download.cgi) 解压后配置 `MAVEN_HOME` 与 Path |
| **Node.js** | [Node.js LTS](https://nodejs.org/) 安装包（需 ≥ 18） |
| **MySQL 8** | [MySQL Community Server](https://dev.mysql.com/downloads/mysql/) 安装向导，记住 root 密码 |

也可用 **winget**（管理员 PowerShell 示例，版本以实际为准）：

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
winget install OpenJS.NodeJS.LTS
winget install Oracle.MySQL
```

在 **命令提示符（cmd）** 或 **PowerShell** 中确认：

```text
java -version
mvn -v
node -v
mysql --version
```

### 2. 启动 MySQL 并创建数据库

1. `Win + R` 输入 `services.msc`，找到 **MySQL** 或 **MySQL80**，确保状态为「正在运行」；或在**管理员**命令行执行：`net start MySQL80`（服务名以安装时为准）。
2. 使用 **MySQL Command Line Client**、**MySQL Workbench** 或已加入 Path 的 `mysql` 登录 root，执行：

```sql
CREATE DATABASE IF NOT EXISTS interest_circle
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 获取代码与后端配置

```powershell
cd C:\Users\你的用户名\Projects
git clone https://github.com/bluemountain1231/Conversation.git
cd Conversation
```

若仓库中未提交 `application.yml`（仅提供模板），请复制并编辑：

```powershell
copy backend\src\main\resources\application.example.yml backend\src\main\resources\application.yml
```

用记事本或 VS Code 打开 `application.yml`，将 `spring.datasource.password` 等改为本机 MySQL 与 JWT、第三方密钥（说明见上文「配置后端」）。

创建上传目录（在仓库根目录 `Conversation\` 下）：

```powershell
mkdir backend\uploads -Force
```

### 4. 启动后端

```powershell
cd backend
mvn -q -DskipTests package
mvn spring-boot:run
```

首次成功启动后，JPA 会在库中**自动建表**。保持该窗口运行。

### 5. （可选）导入演示数据 `test-data.sql`

前提：表已生成；且已存在 **用户 `id = 1`**（先在前端注册第一个账号），说明与 Linux 章节相同。

在仓库根目录 `Conversation\` 下：

**命令提示符（cmd）：**

```cmd
mysql -u root -p interest_circle < backend\test-data.sql
```

**PowerShell：**

```powershell
Get-Content -Raw backend\test-data.sql | mysql -u root -p interest_circle
```

### 6. 启动前端

新开一个终端：

```powershell
cd Conversation\frontend
npm install
npm run dev
```

浏览器访问 **http://localhost:5173**。若后端为自带 HTTPS 的 `https://localhost:8080`，需在浏览器接受自签证书，且保持 `frontend\vite.config.js` 中 `proxy` 的 `target` 与后端一致。

### 7. 生产构建（Windows 上打包前端）

```powershell
cd frontend
npm run build
```

生成的 `frontend\dist\` 可拷贝到 Windows 上的 **IIS**、内网 Nginx（若已安装）或任意静态站点；API 仍由可部署在服务器或本机的 Spring Boot 提供。

### 8. 常见问题

- **找不到 `mvn` / `java` / `mysql`**：检查 Path 是否包含上述工具的 `bin` 目录，**重新打开**终端。
- **8080 被占用**：在 `application.yml` 中修改 `server.port`，并同步修改前端代理地址。
- **MySQL 连接失败**：确认服务已启动、防火墙允许本机连接、`application.yml` 中主机为 `localhost`、端口 `3306` 与安装一致。

---

## 快速开始（本地最小步骤）

1. MySQL 创建库 `interest_circle`，与 `application.yml` 一致。  
2. `cd backend && mvn spring-boot:run`  
3. `cd frontend && npm install && npm run dev`  
4. （可选）按上文导入 `backend/test-data.sql` 填充演示数据。

---

## API 接口

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/auth/register | 注册 | 否 |
| POST | /api/auth/login | 登录 | 否 |
| GET | /api/users/me | 获取当前用户 | 是 |
| PUT | /api/users/me | 更新个人资料 | 是 |
| GET | /api/users/:id | 获取用户信息 | 否 |
| POST | /api/posts | 发帖 | 是 |
| GET | /api/posts | 帖子列表 | 否 |
| GET | /api/posts/:id | 帖子详情 | 否 |
| DELETE | /api/posts/:id | 删除帖子 | 是 |
| POST | /api/posts/:id/like | 点赞/取消 | 是 |
| POST | /api/posts/:id/favorite | 收藏/取消 | 是 |
| GET | /api/posts/user/:userId | 用户帖子 | 否 |
| GET | /api/posts/favorites | 我的收藏 | 是 |
| GET | /api/posts/:id/comments | 评论列表 | 否 |
| POST | /api/posts/:id/comments | 发表评论 | 是 |
| POST | /api/upload/image | 上传图片 | 是 |

---

## 项目结构

```
interest-circle/
├── backend/
│   ├── pom.xml
│   ├── test-data.sql           # 演示/测试数据（导入到 interest_circle）
│   └── src/main/java/com/interest/circle/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── exception/
│       ├── repository/
│       ├── security/
│       └── service/
├── frontend/
│   └── src/
│       ├── api/
│       ├── components/
│       ├── router/
│       ├── stores/
│       └── views/
├── miniprogram/                # 微信小程序（可选）
└── schema.sql                  # 数据库参考脚本（以 JPA 实体为准）
```
