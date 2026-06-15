# NoobShop 开发文档

> 适用对象：参与 NoobShop 前后端开发、联调、测试与运维的团队成员。  
> 后端项目：`D:\javaproject\NoobShop`  
> 前端项目：`D:\develop\NoobShop`  
> 更新时间：2026-06-04。

## 1. 项目概览

NoobShop 是一个商城类应用，当前形态为：

| 层 | 技术栈 | 目录 |
| --- | --- | --- |
| 前端 | uni-app、Vue 3、Pinia、Vite | `D:\develop\NoobShop` |
| 后端 | Spring Boot 3.3、Java 17、MyBatis-Plus、Shiro、JWT | `D:\javaproject\NoobShop` |
| 数据 | MySQL、Redis、Elasticsearch、RocketMQ | 默认指向 `192.168.100.135` |
| 文件 | 阿里云 OSS | 通过环境变量配置 AccessKey |
| 实时通信 | Netty WebSocket | HTTP 8080，WS 8888 |

核心业务模块：

- 首页：Banner、公告、分类入口、热门商品、猜你喜欢。
- 商品：分类、搜索、详情、规格、评论、收藏。
- 购物车：添加、删除、数量修改、结算。
- 订单：创建、列表、详情、取消、支付成功、确认收货、物流、搜索。
- 用户：登录注册、资料、地址、收藏、反馈、关于我们。
- 聊天：WebSocket 在线咨询、会话列表、历史消息、未读数。
- 管理端基础能力：Banner、分类、公告、优惠券、商品关键词、商品图片。

## 2. 目录结构

### 2.1 后端目录

```text
src/main/java/com/app/noobshop
├── controller        # HTTP API Controller
│   ├── admin         # 管理端和部分公共业务接口
│   ├── user          # 用户端接口
│   └── tool          # 上传等工具接口
├── service           # 业务接口
├── service/impl      # 业务实现
├── mapper            # MyBatis-Plus Mapper
├── pojo
│   ├── dto           # 请求 DTO
│   ├── entity        # 数据库实体
│   ├── vo            # 响应 VO
│   └── emums         # 枚举
├── security          # Shiro + JWT 鉴权
├── infrastructure
│   ├── redis         # Redis Key/缓存配置
│   ├── es            # Elasticsearch 文档和仓储
│   ├── rocketmq      # MQ 常量与消费者
│   ├── netty         # WebSocket 聊天服务
│   └── mp            # MyBatis-Plus 配置
├── job               # 初始化与延迟任务
├── aop               # 缓存清理、购物车同步等切面
├── common            # Result、工具类、异常、上下文
└── config            # CORS、Swagger、Caffeine 等配置
```

### 2.2 前端目录

```text
D:\develop\NoobShop
├── pages             # uni-app 页面
├── components        # 业务组件，例如 TopNavbar、SpecPicker
├── store             # Pinia store
├── utils
│   ├── request.js    # 统一请求封装、Token 刷新
│   ├── api.js        # 部分接口集中封装
│   ├── websocket.js  # WebSocket 客户端
│   └── cacheUtil.js  # 缓存工具
├── static            # 图片等静态资源
├── pages.json        # 页面路由
├── vite.config.js    # H5 开发代理
└── package.json      # 前端脚本与依赖
```

## 3. 本地启动

### 3.1 环境要求

| 组件 | 建议版本/说明 |
| --- | --- |
| JDK | 17 |
| Maven | 使用项目自带 `mvnw.cmd` 或本机 Maven |
| Node.js | 能运行 Vite 5 与 uni-app CLI 的版本 |
| MySQL | 默认库名 `noobshop` |
| Redis | 默认端口 6379 |
| Elasticsearch | 7.12.x 兼容 |
| RocketMQ | name-server 默认 9876 |
| 阿里云 OSS | 上传接口需要有效 AccessKey |

### 3.2 后端配置

后端主配置：

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/redisCache.yml`

开发环境默认激活 `dev`。建议用环境变量覆盖敏感配置：

```powershell
$env:NOOBSHOP_DB_URL="jdbc:mysql://127.0.0.1:3306/noobshop?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true"
$env:NOOBSHOP_DB_USERNAME="root"
$env:NOOBSHOP_DB_PASSWORD="123456"
$env:NOOBSHOP_REDIS_HOST="127.0.0.1"
$env:NOOBSHOP_REDIS_PASSWORD="123456"
$env:NOOBSHOP_ES_URIS="http://127.0.0.1:9200"
$env:NOOBSHOP_ROCKETMQ_NAME_SERVER="127.0.0.1:9876"
$env:OSS_ACCESS_KEY_ID="your-access-key-id"
$env:OSS_ACCESS_KEY_SECRET="your-access-key-secret"
```

启动：

```powershell
.\mvnw.cmd spring-boot:run
```

打包：

```powershell
.\mvnw.cmd -DskipTests package
```

访问：

- HTTP：`http://127.0.0.1:8080`
- Swagger：`http://127.0.0.1:8080/swagger-ui.html`
- WebSocket：`ws://127.0.0.1:8888/ws/chat?token={token}`

### 3.3 前端启动

进入前端目录：

```powershell
cd D:\develop\NoobShop
npm install
npm run dev
```

H5 开发服务默认：

```text
http://127.0.0.1:8081
```

微信小程序开发构建：

```powershell
npm run dev:mp-weixin
```

## 4. 数据库与初始化

项目中可用 SQL：

| 位置 | 用途 |
| --- | --- |
| `sql/noobshop_frontend_db_fix.sql` | 前端联调补表/字段修复 |
| `sql/noobshop_missing_tables.sql` | 缺失表补齐 |
| `sql/noobshop_product_seed.sql` | 商品种子数据 |
| `sql/noobshop_other_seed.sql` | 其他种子数据 |
| `D:\develop\NoobShop\product.sql` | 前端目录中的商品数据 |
| `D:\develop\NoobShop\product_image.sql` | 前端目录中的商品图片数据 |

推荐初始化顺序：

1. 创建数据库 `noobshop`，字符集使用 `utf8mb4`。
2. 导入基础表结构 SQL。
3. 导入 `noobshop_missing_tables.sql` 和 `noobshop_frontend_db_fix.sql`。
4. 导入商品、商品图片、其他种子数据。
5. 启动 Redis、ES、RocketMQ 后再启动后端，避免初始化 Runner 报错。

## 5. 后端开发规范

### 5.1 Controller

- 统一放在 `controller` 包。
- 用户端接口按业务放到 `controller/user` 或公共 `/api` 控制器。
- 管理端接口应统一规划到 `/api/admin/**`，避免和公开 `/api/product/**` 混在一起。
- 请求参数优先使用 DTO，不要让 Controller 直接拼业务逻辑。
- 新增接口必须同步更新 `docs/API_DOCUMENTATION.md`。

示例：

```java
@RestController
@RequestMapping("/api/address")
public class AddressController {
    @PostMapping("/add")
    public Result insertAddress(@RequestBody @Valid AddressDTO addressDTO) {
        return addressService.insertAddress(addressDTO);
    }
}
```

### 5.2 Service

- Controller 只负责参数接收、鉴权上下文、返回封装。
- 业务规则放到 `service/impl`。
- 对 Redis、ES、MQ 的操作尽量通过已有 infrastructure 封装完成。
- 涉及缓存一致性时优先复用已有 AOP 注解：
  - `@SaveCartRedisCacheToMysqlAnnotation`
  - `@RemoveProductCollectionRedisCacheAnnotation`
  - `@RemoveOrderDetailRedisCacheAnnotation`
  - `@RemoveBannerRedisCacheAnnotation`
  - `@UpdateCategoryTreeRedisCacheAnnotation`

### 5.3 Mapper 与实体

- 实体类放 `pojo/entity`，字段保持驼峰，MyBatis-Plus 开启下划线转驼峰。
- Mapper XML 放 `src/main/resources/mapper`。
- 枚举字段使用 MyBatis-Plus 枚举处理器，新增枚举时确认数据库存储值。

### 5.4 返回与异常

- 成功统一 `Result.success()` 或 `Result.success(data)`。
- 业务失败优先抛业务异常或返回 `Result.error(message)`。
- 参数校验使用 Jakarta Validation，例如 `@NotBlank`、`@NotNull`、`@Valid`。
- JWT 异常由 `JwtAuthenticationHandler` 处理，普通异常由全局异常处理器处理。

## 6. 鉴权与登录态

### 6.1 HTTP 鉴权流程

1. 前端登录后保存 `token`、`refreshToken`、`userInfo`。
2. 每次请求在 Header 发送 `Authorization`。
3. Shiro 根据路径规则决定 `anon`、`optionalJwt` 或 `jwt`。
4. `JwtFilter` 解析 token，调用 Realm 认证。
5. 认证成功后将用户信息放入 `BaseContext`。
6. 请求结束后清理 `BaseContext`，避免 ThreadLocal 泄漏。

### 6.2 Token 刷新

前端 `utils/request.js` 遇到 `code === 10002` 会：

1. 暂停同类请求进入队列。
2. 调用 `POST /api/user/refresh/token`。
3. 保存新 token。
4. 重放原请求。
5. 如果刷新失败，清理登录态并跳转登录页。

### 6.3 当前需要修正的鉴权点

| 项 | 问题 | 建议 |
| --- | --- | --- |
| `Authorization` 值 | 前端发送 `Bearer xxx`，后端直接 parse header | 后端统一剥离 `Bearer ` 前缀，或前端只传 token |
| `/api/product/admin/**` | 被 `/api/product/**` 公开规则覆盖 | 调整 Shiro 匹配顺序或管理端路径 |
| `/api/upload/image` | 单图上传公开 | 如涉及用户头像/反馈凭证，可改为登录 |
| 角色权限 | Shiro 中角色规则被注释 | 管理端上线前启用 `roles[admin]` |

## 7. 前端开发规范

### 7.1 请求封装

优先使用：

- `utils/request.js`：底层请求、Token、错误处理。
- `utils/api.js`：可复用业务接口封装。

新增页面接口时，建议先在 `utils/api.js` 增加封装，再在页面调用，避免接口分散在页面里。

```js
export const productApi = {
  getProductDetail: (id) => request({
    url: '/api/product/detail',
    method: 'GET',
    params: { productId: id }
  })
}
```

### 7.2 H5 代理

当前 `vite.config.js`：

```js
proxy: {
  '/api': {
    target: 'http://127.0.0.1:8080',
    changeOrigin: true
  },
  '/ws': {
    target: 'http://127.0.0.1:8080',
    ws: true,
    changeOrigin: true
  }
}
```

后端 WebSocket 实际是 Netty 端口 8888。H5 联调聊天时建议改为：

```js
'/ws': {
  target: 'ws://127.0.0.1:8888',
  ws: true,
  changeOrigin: true
}
```

### 7.3 状态管理

Pinia 模块：

| Store | 责任 |
| --- | --- |
| `userStore` | 登录态、用户信息 |
| `cartStore` | 本地购物车、服务端同步 |
| `orderStore` | 订单状态 |
| `chatStore` | 会话、历史消息、未读数 |

购物车同步要以服务端 `PUT /api/cart/update` 为准。当前部分前端代码仍调用 `/api/cart/merge`，需要统一。

### 7.4 页面路由

页面集中在 `pages.json`。常用页面：

| 页面 | 路径 |
| --- | --- |
| 首页 | `pages/main/index/index` |
| 登录 | `pages/main/login/login` |
| 商品分类 | `pages/product/category/category` |
| 商品列表 | `pages/product/list/list` |
| 商品详情 | `pages/product/detail/detail` |
| 商品评价 | `pages/product/comment/comment` |
| 购物车 | `pages/cart/index/index` |
| 订单确认 | `pages/order/confirm/confirm` |
| 订单列表 | `pages/order/list/list` |
| 订单详情 | `pages/order/detail/detail` |
| 地址列表 | `pages/address/list/list` |
| 地址编辑 | `pages/address/edit/edit` |
| 用户中心 | `pages/user/index/index` |
| 在线咨询 | `pages/user/consult/consult` |

跳转时使用完整路径，例如：

```js
uni.navigateTo({ url: '/pages/order/detail/detail?orderNo=xxx' })
```

## 8. WebSocket 聊天开发

### 8.1 服务端流程

1. Spring 启动后 `ChatNettyServer` 自动启动 Netty。
2. Netty 监听 `netty.port`，默认 8888。
3. `JwtAuthHandler` 拦截 `/ws/chat?token=xxx` 握手并解析 JWT。
4. 用户 ID 绑定到 `UserChannelManager`。
5. `ChatHandler` 处理 `SEND_MSG`、`READ_REPORT`、`HEARTBEAT`。
6. 消息先入库，再尝试推送给目标在线用户。

### 8.2 客户端流程

1. 登录成功后调用 `socketService.connect(token)`。
2. 发送消息用 `sendMessage({ toUserId, msgType, content, productId })`。
3. 收到 `PUSH_MSG` 更新聊天列表和当前会话。
4. 进入会话页后调用 HTTP `clearUnread` 或 WebSocket `READ_REPORT`。
5. 页面卸载或退出登录时调用 `socketService.close()`。

### 8.3 消息类型建议

| `msgType` | 含义 |
| --- | --- |
| `0` | 文本 |
| `1` | 图片 |
| `2` | 商品卡片 |

后端当前没有强校验 `msgType` 枚举，前端和后端需要保持约定。

## 9. 缓存、ES 与 MQ

### 9.1 Redis

Redis 用途：

- 登录用户信息与 refresh token。
- 商品详情缓存。
- 商品收藏列表缓存。
- 购物车缓存。
- 订单详情缓存。
- 商品评论、点赞用户集合。
- 热门商品读写桶标记。

TTL 配置在 `redisCache.yml`。

开发新增缓存时：

1. 在 `RedisKeyGenerator` 中增加 key 生成方法。
2. 在 `redisCache.yml` 增加 TTL。
3. 写业务时明确缓存删除/更新时机。
4. 优先用 AOP 注解处理跨模块缓存失效。

### 9.2 Elasticsearch

ES 用于商品搜索与热门/相关商品查询：

- 文档：`ProductDocument`
- 仓储：`ProductEsRepository`
- 初始化：`EsIndexInitializerService`
- 拷贝：`EsCopyMapper`

新增商品字段如果要参与搜索，需要同步更新：

1. MySQL 实体。
2. ES 文档字段。
3. 索引 mapping。
4. 商品同步 MQ 消费逻辑。
5. 前端展示字段。

### 9.3 RocketMQ

当前配置默认 name-server：`192.168.100.135:9876`。

项目中有订单、优惠券、商品同步相关消费者。新增 MQ 主题时：

- 常量放到 `infrastructure/rocketmq/constant`。
- 消费失败消息入 `MqConsumerFailedMsg`。
- 消费逻辑必须保证幂等，尤其是订单状态与优惠券状态。

## 10. 文件上传与 OSS

上传接口依赖 `AliyunOSSUtils`，配置项：

```yaml
app:
  aliyun:
    oss:
      endpoint: oss-cn-beijing.aliyuncs.com
      access-key-id: ${OSS_ACCESS_KEY_ID}
      access-key-secret: ${OSS_ACCESS_KEY_SECRET}
      bucket-name: noobshop
```

前端上传应使用 `utils/request.js` 中的 `upload()` 或 `uni.uploadFile`，字段名必须匹配：

| 接口 | 字段名 |
| --- | --- |
| `/api/upload/image` | `file` |
| `/api/upload/images` | `files` |

不要用 JSON 请求体上传图片。

## 11. 联调检查清单

每次提交涉及接口的改动，至少检查：

- Controller 路径、HTTP 方法、请求参数是否和前端一致。
- 是否需要登录，Shiro 配置是否正确。
- 前端是否发送了正确 Content-Type。
- 返回 `data` 字段结构是否满足页面使用。
- 列表接口是否有空列表、最后一页、无 token、token 过期处理。
- 订单金额、运费、商品价格字段是否统一字符串或数字。
- 图片上传是否真实返回可访问 URL。
- WebSocket H5、小程序端是否都能连接。
- Swagger 是否能正常展示新增接口。

## 12. 当前待修复项

这些问题来自本次前后端代码对照，建议优先处理：

| 优先级 | 问题 | 影响 |
| --- | --- | --- |
| P0 | 前端部分调用 `/api/cart/merge`，后端不存在 | 登录后合并购物车失败 |
| P0 | 前端 `utils/api.js` 的退出登录是 DELETE，后端是 POST | 调用封装退出时失败 |
| P0 | H5 WebSocket 代理指向 8080，Netty 在 8888 | H5 在线咨询连接失败 |
| P1 | `/api/user/enterprise/auth` 前端调用但后端不存在 | 企业认证页面提交失败 |
| P1 | `/api/pay/wxpay` 当前未返回微信支付参数 | 真实支付不可用 |
| P1 | JWT Header 是否带 `Bearer` 未统一 | 可能导致认证失败 |
| P2 | `/api/order/scroll/query/list` 使用 GET Body | 部分客户端兼容性差 |
| P2 | `/api/product/categroy/list` 路径拼写错误但已被前端使用 | 如改正需保留兼容别名 |

## 13. 新功能开发流程

推荐流程：

1. 先在接口文档补充接口草案，明确路径、方法、参数、返回。
2. 后端新增 DTO、Controller、Service、Mapper。
3. 写最小可用单元/集成测试，或用 Swagger/Postman 验证。
4. 前端在 `utils/api.js` 增加接口封装。
5. 页面调用 API，处理 loading、空态、错误态。
6. 联调登录态、异常码、空数据、重复提交。
7. 更新 `docs/API_DOCUMENTATION.md` 和本开发文档。

## 14. 发布前检查

- `application-dev.yml` 中不要保留生产密钥。
- 管理端接口必须启用角色权限。
- Swagger 在生产环境按需关闭。
- MySQL、Redis、ES、RocketMQ 地址用环境变量注入。
- OSS、微信支付密钥只走环境变量或安全配置中心。
- 支付回调地址必须是公网 HTTPS，且验签。
- 前端构建产物不要提交无关调试日志。

