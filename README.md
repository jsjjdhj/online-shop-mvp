# 在线购物平台 MVP

**课程**：软件质量与测试课 2025-2026-2 学期

> 在线购物平台 MVP（最小可行产品），基于 Spring Boot + Vue 3 + MySQL 单体架构，涵盖完整的电子商务核心流程。

---

## 项目结构

```
软件测试课设/
├── shop-backend/          # 后端：Spring Boot 项目
├── shop-frontend/         # 前端：Vue 3 项目
└── docs/                  # 项目文档
    ├── 需求文档/          # 需求说明
    ├── 设计文档/          # 概要设计、详细设计
    ├── 测试文档/          # 测试用例、测试报告、自动化脚本
    ├── 会议记录/          # 团队会议记录
    └── 部署指南/          # 部署手册
```

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5 + MyBatis-Plus |
| 前端框架 | Vue 3.4 + Vite 5 + Pinia + Vue Router 4 |
| UI 组件 | Element Plus 2.7 |
| 数据库 | MySQL 8.0 (开发库 + 生产库) |
| 缓存 | Redis |
| 认证 | JWT (jjwt 0.12) |
| 构建工具 | Maven (后端) / Vite (前端) |
| 单元测试 | JUnit 5 + MockMvc / Vitest + Vue Test Utils |
| E2E 测试 | Playwright |
| 覆盖率 | JaCoCo |

## 核心功能模块

| 模块 | 功能 |
|------|------|
| 用户模块 | 注册、登录、连续5次失败锁定15分钟 |
| 商品模块 | 列表展示、模糊搜索、详情查看 |
| 购物车模块 | 添加、数量修改(1-99)、删除、合计金额 |
| 订单模块 | 结算(原子事务)、支付、取消、状态流转 |
| 管理员模块 | 商品CRUD、订单确认/发货 |

## 团队分工

| 角色 | 成员 | 职责 |
|------|------|------|
| 后端主程 + API 自动化 | A | T1(后端开发+单测) + T7(接口自动化) |
| 前端主程 + UI 自动化 | B | T2(前端开发+单测) + T8(E2E自动化) |
| 全栈支撑 + 手工测试 | C | T3(管理端全栈) + T6(手工测试) |
| 性能压测与调优 | D | T9(并发压测) + T10(SLA分析) + T11(资源监控) |
| 测试经理与文档 | E | T4+T5(用例设计) + T12(文档统筹) |

## 快速启动

### 后端
```bash
cd shop-backend
mvn clean install
# 配置 application-dev.yml 中的数据库连接
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 前端
```bash
cd shop-frontend
npm install
npm run dev
```

### 数据库
执行 `shop-backend/src/main/resources/db/init.sql` 初始化数据库表结构和默认管理员账户。

## 接口文档

详见 [shop-backend/docs/API接口文档.md](shop-backend/docs/API接口文档.md)

## 测试策略

- **单元测试**：JUnit 5 覆盖核心业务逻辑，JaCoCo 确保语句覆盖率 > 80%
- **接口自动化**：Python/REST Assured 覆盖输入规格中的边界值和非法输入
- **E2E 自动化**：Playwright 覆盖核心购物闭环（登录→搜索→加车→结算→订单确认）
- **性能测试**：JMeter 50/200 并发场景，90% 响应时间 SLA 验证
- **手工测试**：覆盖 UI 标识必测检查点及全流程冒烟测试

## 许可

本课程设计项目用于教学目的。
