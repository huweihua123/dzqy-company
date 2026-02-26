# 锻造小企业记账管理系统 详细设计文档

版本: v0.1
日期: 2026-02-10

## 1. 目标与范围

### 1.1 目标
- 支持锻造小企业的订单、应收、工人薪资、出勤、统计报表管理。
- 批量导入小额订单，自动统计欠款、核销、账单导出。
- 适配多工种薪资规则，记录工资发放与账户余额变动。
- 提供日/周/月统计与可视化导出。

### 1.2 不包含内容
- 暂不做登录认证、权限控制。
- 暂不做多组织/多租户。
- 不做移动端，仅 Web 端。

## 2. 技术选型

### 2.1 后端
- 语言/框架: Java 21 + Spring Boot 3.x
- ORM: Spring Data JPA + Hibernate
- 数据库: PostgreSQL 15+
- 数据迁移: Flyway
- 校验: Hibernate Validator
- 文件: Apache POI / OpenCSV
- API 文档: springdoc-openapi
- 构建: Maven

### 2.2 前端
- 框架: React 18 + Vite
- UI: Ant Design
- 图表: ECharts
- 状态管理: Zustand
- 请求: Axios

### 2.3 部署
- Nginx + Spring Boot 单体部署
- PostgreSQL 独立服务
- 本地文件存储(后期可替换为对象存储)

## 3. 业务模块与边界

### 3.1 订单管理
- 批量导入每日多笔小订单
- 订单字段: 货主、材质、规格、下料重量、金额
- 应收统计: 自动计算欠款
- 核销: 支持部分核销/全额核销
- 账单导出: PDF/Excel

### 3.2 工资管理
- 工种类型: 锻造岗位(大师傅/二师傅)、车床工、锯料工
- 规则: 计件(吨价) / 计时(小时) / 日薪
- 工资核算: 结合产量与出勤
- 工资发放: 记录每笔发放、余额变动

### 3.3 员工管理
- 工人基础信息、岗位等级、薪资标准
- 出勤登记: 每日状态

### 3.4 统计报表
- 日/周/月汇总产量
- 各货主订单量
- 工人工资、完成吨位
- 可视化与导出

## 4. 领域模型与数据表

> 说明: 字段仅列关键字段，最终以 DDL 为准

### 4.1 货主
- owners(id, name, contact, phone, address, remark, created_at)

### 4.2 材质
- materials(id, name, code, remark)

### 4.3 规格
- specs(id, name, description)

### 4.4 订单
- orders(id, order_no, owner_id, material_id, spec_id, order_date, status)
- orders(receivable_amount, paid_amount, outstanding_amount)
- orders(created_at, updated_at)

### 4.5 订单明细
- order_items(id, order_id, product_name, qty, weight, price, amount)

### 4.6 应收核销
- receivable_writeoffs(id, order_id, writeoff_date, amount, remark)

### 4.7 工人
- workers(id, name, role, level, phone, status, base_salary, remark)

### 4.8 出勤
- attendance(id, worker_id, work_date, status, work_hours, remark)

### 4.9 薪资规则
- salary_rules(id, role, rule_type, price_per_ton, daily_salary, hourly_salary)

### 4.10 产量记录
- production_records(id, worker_id, order_id, work_date, tonnage)

### 4.11 工资发放
- salary_payments(id, worker_id, pay_date, amount, remark)

### 4.12 账户流水
- account_logs(id, worker_id, change_amount, balance, type, created_at)

## 5. 核心业务流程

### 5.1 订单导入流程
1. 上传 Excel/CSV
2. 校验字段完整性、格式、数值范围
3. 批量写入订单与明细
4. 自动计算 receivable_amount/outstanding_amount

### 5.2 核销流程
1. 输入核销金额
2. 校验 <= outstanding_amount
3. 写入 receivable_writeoffs
4. 更新订单 paid_amount/outstanding_amount

### 5.3 工资核算
- 锻造岗位: 日产量 * 吨价 + 出勤
- 车床/锯料: 日薪 * 出勤天数
- 计时岗位: 小时 * 单价

### 5.4 工资发放
1. 生成工资单(核算结果)
2. 确认发放金额
3. 写入 salary_payments
4. 写入 account_logs(余额变更)

## 6. API 设计(初稿)

### 6.1 订单管理
- POST /orders/import
- POST /orders
- GET /orders
- GET /orders/{id}
- POST /orders/{id}/writeoff
- GET /orders/{id}/export

### 6.2 工资管理
- GET /salary-rules
- POST /salary-rules
- GET /salary/calc?month=
- POST /salary/pay

### 6.3 员工管理
- GET /workers
- POST /workers
- PUT /workers/{id}
- POST /attendance
- GET /attendance?date=

### 6.4 报表管理
- GET /reports/production?period=day|week|month
- GET /reports/orders?period=
- GET /reports/salary?period=
- GET /reports/export?type=

## 7. 前端页面结构

### 7.1 订单管理
- 订单列表(筛选/导入/导出)
- 订单详情(明细/核销记录/账单)

### 7.2 工资管理
- 薪资规则设置
- 工资核算列表
- 工资发放记录

### 7.3 员工管理
- 工人信息列表
- 出勤登记

### 7.4 报表
- 日/周/月统计图表
- 导出按钮

## 8. 开发指导与里程碑

### 8.1 开发优先级
1. 核心数据表与订单导入
2. 应收核销与账单导出
3. 员工管理与出勤
4. 工资核算与发放
5. 报表

### 8.2 后端工程结构建议
- controller/
- service/
- repository/
- domain/
- dto/
- mapper/
- config/

### 8.3 前端工程结构建议
- pages/
- components/
- services(api)
- store/
- utils/

### 8.4 测试策略
- API 单元测试 + 集成测试
- 核心计算逻辑单元测试

---

后续可根据此文档继续细化:
- PostgreSQL DDL 具体表结构
- API 请求/响应字段
- 工资计算规则细化
- 前端页面交互与表单校验
