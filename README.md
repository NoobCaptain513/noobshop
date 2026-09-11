# 菜鸟商城 (NoobShop) 后端项目

## 📖 项目简介
菜鸟商城（NoobShop）是一个基于全新技术栈构建的前后端分离 B2C 电商单体项目。本项目覆盖了现代电商平台的核心业务链路，包括商品浏览、全文检索、购物车管理、订单流转、权限安全以及实时的在线客服系统。

项目以高可用、高并发为设计目标，在数据层和业务层进行了大量的工程化实践与优化，具备完整的电商基础设施能力。

## 🛠️ 技术栈选型
*   **基础环境**：Java 17, Spring Boot 3.x
*   **数据持久化**：MySQL 8.x, MyBatis & MyBatis-Plus
*   **多级缓存**：Caffeine (JVM 本地缓存), Redis (分布式缓存)
*   **搜索引擎**：Elasticsearch (提供商品高命中精准检索)
*   **消息队列**：RocketMQ (实现库存同步等异步业务解耦，并提供消费幂等与失败重试兜底)
*   **实时通信**：Netty (搭建高性能在线客服系统 WebSocket 服务)
*   **权限认证**：Spring Security + JWT Access Token + Redis Refresh Token（支持 Token 轮换与 RBAC 权限控制）
*   **工具与工程化**：Lombok, MapStruct, Redisson, Spring StateMachine, Flyway, SpringDoc OpenAPI (Swagger 3)

## ✨ 核心特性与架构亮点

### 1. 完善的电商业务闭环
*   **商品模块**：支持多级分类、动态规格选项、商品评价与点赞等业务。
*   **交易链路**：覆盖从加购、结算到下单的全流程；使用 **Spring StateMachine** 约束订单状态流转，并基于 **Redisson 延迟队列** 实现“超时未支付订单自动取消”，无需频繁扫表查询。
*   **海量状态流转**：通过 Redis ZSet 配合分布式锁与专属线程池，高效实现千万级优惠券到期等定时状态变更。

### 2. 高可用缓存与并发优化
*   针对首页和热门商品，搭建 **Caffeine + Redis 多级缓存架构**，大幅提升响应速度。
*   利用**布隆过滤器 (Bloom Filter)** 作为安全屏障拦截无效请求，有效解决**缓存穿透**问题；并通过逻辑过期及 Redisson 锁防范缓存击穿。
*   高频并发操作（如商品评论点赞）引入 Redis List 聚合削峰，借助自定义线程池执行 `@Scheduled` 批量合并落库，极大地降低了数据库 I/O 开销。

### 3. 标准的工程化落地
*   **优雅的对象转换**：使用 **MapStruct** 在编译期生成高性能的 `DTO -> Entity -> VO` 转换代码，丢弃低效且不安全的反射拷贝。
*   **隔离的线程池体系**：拒绝“一池多用”，为订单取消、商品同步、消息消费等各自配备独立的 `ThreadPoolTaskExecutor` 线程池，保障业务隔离与稳定性。
*   **标准化 API 文档**：全局接入 SpringDoc，利用 Profile 实现多环境隔离（生产环境自动屏蔽接口文档），支持全局 JWT 统一认证调试。
*   **数据库版本管理**：通过 **Flyway** 维护数据库结构演进，并提供 RBAC、库存字段及消息消费幂等等迁移脚本。

### 4. 实时在线客服系统
*   底层基于 Netty 构建 WebSocket 服务器，独立端口（8888）与 HTTP 服务（8080）分离。
*   支持单点对单点（用户对话客服）的实时聊天、消息到达确认（ACK）、离线未读消息统计，并将消息异步入库持久化。

## 📁 目录结构说明

```text
com.app.noobshop
├── controller        # 控制层（划分 admin/user/tool 进行接口隔离）
├── service           # 核心业务逻辑层
├── mapper            # MyBatis-Plus 数据访问层
├── pojo              # 模型层（包含 DTO/VO/Entity/Enum）
├── infrastructure    # 基础设施层（集成 Redis, ES, RocketMQ, Netty 等基建模块）
├── job               # 任务调度层（包含初始化、延迟任务、定时任务处理器）
├── security          # 安全层（Spring Security、JWT 认证、RBAC 授权与权限缓存）
├── statemachine      # 状态机层（订单状态与事件流转规则）
├── common            # 公共层（全局异常、通用工具类、MapStruct 映射配置）
└── config            # 全局配置类（Caffeine, Swagger, CORS 等）
```

