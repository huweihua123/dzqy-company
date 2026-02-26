# Windows 部署说明

## 环境要求

| 软件       | 版本要求  | 下载地址                                     |
| ---------- | --------- | -------------------------------------------- |
| JDK        | 17 或以上 | https://adoptium.net/                        |
| Node.js    | 18 或以上 | https://nodejs.org/                          |
| PostgreSQL | 15 或以上 | https://www.postgresql.org/download/windows/ |

---

## 首次部署步骤

### 第一步：安装并配置 PostgreSQL

1. 安装 PostgreSQL，记住设置的密码（默认用户名 `postgres`）
2. 使用 pgAdmin 或 psql 创建数据库：
   ```sql
   CREATE DATABASE dydz;
   ```
3. 如果密码不是 `123456`，修改 `dydz-backend/src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       password: 你的密码
   ```

### 第二步：一键打包

双击运行 `build.bat`，脚本会自动：
- 安装前端依赖并构建
- 将前端产物打包进后端 JAR
- 生成可执行的 `dydz-backend-0.1.0.jar`

> ⚠️ 首次运行需要下载依赖，请确保网络畅通，时间较长请耐心等待。

### 第三步：一键启动

双击运行 `start.bat`，脚本会自动：
- 检查 Java 环境
- 检查 PostgreSQL 服务
- 启动后端（自动初始化数据库表结构）
- 打开浏览器访问系统

访问地址：**http://localhost:10001**

---

## 日常使用

每次启动电脑后，只需双击 `start.bat` 即可。

---

## 常见问题

**Q: 启动后浏览器显示空白/报错？**  
A: 检查后端窗口是否有报错，最常见原因是数据库未启动或密码错误。

**Q: 端口被占用？**  
A: 修改 `application.yml` 中的 `server.port` 为其他端口（如 `10002`），同时更新 `start.bat` 中的浏览器访问地址。

**Q: build.bat 中前端构建失败？**  
A: 确认 Node.js 已正确安装，或尝试手动执行：
```bat
cd dydz-frontend
npm install
npm run build
```

**Q: 如何停止服务？**  
A: 关闭标题为"锻造记账-后端"的命令行窗口即可。

---

## 目录结构说明

```
company/
├── build.bat              ← 一键打包（首次部署或代码更新后运行）
├── start.bat              ← 一键启动（日常使用）
├── dydz-backend/          ← Spring Boot 后端
│   └── target/
│       └── dydz-backend-0.1.0.jar   ← 打包后的可执行文件
└── dydz-frontend/         ← React 前端（build 后产物会并入后端 JAR）
```
