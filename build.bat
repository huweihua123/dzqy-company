@echo off
chcp 65001 >nul
title 锻造记账系统 - 打包构建
color 0B

echo ============================================================
echo   锻造小企业记账管理系统 - 一键打包
echo ============================================================
echo.

:: ============================================================
:: 1. 检查 Node.js
:: ============================================================
echo [1/3] 检查 Node.js 环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Node.js，请先安装 Node.js 18+
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)
echo       Node.js 检测通过 √
echo.

:: ============================================================
:: 2. 构建前端（输出到后端 static 目录）
:: ============================================================
echo [2/3] 构建前端（React + Vite）...
cd dydz-frontend

if not exist "node_modules" (
    echo       安装前端依赖（首次较慢）...
    call npm install
    if errorlevel 1 (
        echo [错误] 前端依赖安装失败
        pause
        exit /b 1
    )
)

call npm run build
if errorlevel 1 (
    echo [错误] 前端构建失败
    pause
    exit /b 1
)
cd ..
echo       前端构建完成 √
echo.

:: ============================================================
:: 3. 打包后端（生成可执行 JAR）
:: ============================================================
echo [3/3] 打包后端（Spring Boot Maven）...
cd dydz-backend
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo [错误] 后端打包失败，请检查 Java/Maven 环境
    pause
    exit /b 1
)
cd ..
echo       后端打包完成 √
echo.

echo ============================================================
echo   打包完成！现在可以运行 start.bat 一键启动系统
echo ============================================================
echo.
pause
