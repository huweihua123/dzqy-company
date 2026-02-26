@echo off
chcp 65001 >nul
title 锻造记账系统 - 启动中...
color 0A

echo ============================================================
echo   锻造小企业记账管理系统
echo ============================================================
echo.

:: ============================================================
:: 1. 检查 Java
:: ============================================================
echo [1/4] 检查 Java 环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Java，请先安装 JDK 17 或以上版本
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)
echo       Java 检测通过 √
echo.

:: ============================================================
:: 2. 检查 PostgreSQL 是否在运行
:: ============================================================
echo [2/4] 检查 PostgreSQL 服务...
sc query postgresql-x64-15 >nul 2>&1
if errorlevel 1 (
    sc query postgresql >nul 2>&1
    if errorlevel 1 (
        echo [警告] 未检测到 PostgreSQL 服务，尝试手动启动...
        net start postgresql-x64-15 >nul 2>&1
        net start postgresql >nul 2>&1
    )
)
:: 等待数据库就绪
timeout /t 2 /nobreak >nul
echo       PostgreSQL 检测完成 √
echo.

:: ============================================================
:: 3. 启动后端（使用已打包的 jar，或直接用 mvn）
:: ============================================================
echo [3/4] 启动后端服务...

set JAR_FILE=dydz-backend\target\dydz-backend-0.1.0.jar

if exist "%JAR_FILE%" (
    echo       使用已打包的 JAR 启动...
    start "锻造记账-后端" cmd /k "java -jar %JAR_FILE% && pause"
) else (
    echo       未找到 JAR，使用 Maven 编译并启动（首次较慢，请耐心等待）...
    if not exist "dydz-backend\mvnw.cmd" (
        echo [错误] 未找到 mvnw.cmd，请先执行 build.bat 打包项目
        pause
        exit /b 1
    )
    start "锻造记账-后端" cmd /k "cd dydz-backend && mvnw.cmd spring-boot:run && pause"
)

echo       后端启动中，等待 10 秒...
timeout /t 10 /nobreak >nul
echo.

:: ============================================================
:: 4. 打开浏览器
:: ============================================================
echo [4/4] 打开浏览器...
start http://localhost:10001
echo       已打开浏览器 √
echo.

echo ============================================================
echo   系统已启动！
echo   访问地址: http://localhost:10001
echo   关闭后端: 关闭"锻造记账-后端"窗口即可
echo ============================================================
echo.
pause
