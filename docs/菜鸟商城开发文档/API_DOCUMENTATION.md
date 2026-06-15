# NoobShop 接口文档

> 适用对象：前端、后端、测试、产品联调成员。  
> 生成依据：后端 `D:\javaproject\NoobShop` 的 Controller/DTO/鉴权配置，以及前端 `D:\develop\NoobShop` 的页面与 `utils/api.js` 实际调用。  
> 更新时间：2026-06-04。

## 1. 基础约定

### 1.1 服务地址

| 环境 | HTTP Base URL | WebSocket URL | 说明 |
| --- | --- | --- | --- |
| 后端本地 | `http://127.0.0.1:8080` | `ws://127.0.0.1:8888/ws/chat?token={token}` | Spring Boot HTTP 端口 8080；Netty 聊天端口 8888 |
| 前端 H5 开发 | `http://127.0.0.1:8081` 代理 `/api` | 当前配置代理 `/ws` 到 8080 | H5 下 `request.js` 将 `baseUrl` 置空，经 Vite 代理转发 |

后端同时提供 Swagger：

- OpenAPI JSON：`GET /v3/api-docs`
- Swagger UI：`GET /swagger-ui.html`

### 1.2 认证方式

除公开接口外，请求头携带：

```http
Authorization: Bearer {accessToken}
```

后端 `JwtFilter` 实际直接读取 `Authorization` 头并传给 JWT 解析器。前端目前统一发送 `Bearer ${token}`，请确认登录返回的 token 与后端解析逻辑保持一致。

公开接口来自 `ShiroConfig`：

| 路径规则 | 鉴权 |
| --- | --- |
| `/api/user/login/**` | 公开 |
| `/api/user/refresh/**` | 公开 |
| `/api/user/create/account` | 公开 |
| `/api/user/forget/password` | 公开 |
| `/api/user/change/password` | 公开 |
| `/api/banner/list` | 公开 |
| `/api/product/**` | 公开 |
| `/api/category/**` | 公开 |
| `/api/notice/**` | 公开 |
| `/api/upload/image` | 公开 |
| `/api/about/us/introduce` | 公开 |
| `/api/user/product/comment/**/show` | 可选登录 |
| 其他所有接口 | 必须登录 |

注意：`/api/product/**` 目前会放行 `/api/product/admin/**`。如管理端上线，应收紧此规则。

### 1.3 统一返回结构

所有 HTTP 接口返回 `Result<T>`：

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

常用错误码：

| code | HTTP 状态 | 含义 | 前端处理 |
| --- | --- | --- | --- |
| `10001` | 401 | 无 token | 清理登录态并跳登录 |
| `10002` | 401 | access token 过期 | 前端调用 `/api/user/refresh/token` 刷新 |
| `10003` | 401 | refresh token 过期 | 清理登录态并跳登录 |
| `10004` | 401 | token 签名/格式错误 | 清理登录态并跳登录 |
| `500` | 通常 200/500 | 系统异常 | 展示错误信息 |

分页返回：

```json
{
  "list": [],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10,
  "totalPages": 10
}
```

游标返回：

```json
{
  "cursorCommonEntity": {
    "sortType": "default",
    "sortValue": "上一页末尾排序值",
    "sortId": 10001,
    "querySize": 20
  },
  "list": [],
  "isEnd": false
}
```

简单游标返回：

```json
{
  "simpleCursorCommonEntity": {
    "sortValue": "上一页末尾排序值",
    "sortId": 10001,
    "querySize": 20
  },
  "list": [],
  "isEnd": false
}
```

## 2. 前端当前已调用接口

这些接口来自 `D:\develop\NoobShop\utils\api.js`、`pages/**`、`store/**` 的实际调用，是联调优先级最高的集合。

| 页面/模块 | 接口 |
| --- | --- |
| 首页 | `GET /api/banner/list`、`GET /api/category/tree`、`GET /api/product/user/keyword/list`、`GET /api/notice/latest`、`GET /api/product/scroll/query/list` |
| 登录 | `POST /api/user/login/account`、`POST /api/user/create/account`、`POST /api/user/refresh/token` |
| 用户中心 | `GET /api/user/detail/get`、`PUT /api/user/detail/update`、`POST /api/upload/image` |
| 地址 | `GET /api/address/list`、`POST /api/address/add`、`PUT /api/address/update`、`DELETE /api/address/delete` |
| 商品 | `GET /api/product/detail`、`GET /api/product/related`、`GET /api/product/spec/price`、`GET /api/product/categroy/list`、`GET /api/product/search` |
| 评论 | `POST /api/user/product/comment/firstComment/show`、`POST /api/user/product/comment/secondComment/show`、`GET /api/user/product/comment/appendComment/show`、`POST /api/user/product/comment/firstComment/save`、`POST /api/user/product/comment/secondComment/save`、`POST /api/user/product/comment/firstComment/append`、`PUT /api/user/product/comment/like` |
| 购物车 | `GET /api/cart/list`、`POST /api/cart/add`、`DELETE /api/cart/products`、`PUT /api/cart/update` |
| 订单 | `POST /api/order/create`、`GET /api/order/page/list`、`GET /api/order/detail`、`PUT /api/order/cancel`、`PUT /api/order/confirmReceipt`、`DELETE /api/order/delete`、`GET /api/order/freight`、`GET /api/order/logistics`、`GET /api/order/search` |
| 支付 | `POST /api/pay/wxpay`、`PUT /api/order/pay/success` |
| 收藏 | `GET /api/user/collect/list`、`POST /api/user/collect/add`、`DELETE /api/user/collect/delete` |
| 反馈/关于 | `POST /api/feedback/add`、`GET /api/about/us/introduce` |
| 聊天 | `GET /api/chat/sessions`、`GET /api/chat/history/{contactId}`、`POST /api/chat/clearUnread/{contactId}`、`WS /ws/chat?token={token}` |

## 3. 用户与登录

Base path：`/api/user`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/login/account` | 公开 | 用户名密码登录 |
| `POST` | `/login/wechat` | 公开 | 微信快捷登录 |
| `GET` | `/info` | 登录 | 获取当前登录用户基础信息 |
| `POST` | `/logout` | 登录 | 退出登录，清除 Redis 登录态 |
| `POST` | `/create/account` | 公开 | 创建普通用户账户 |
| `PUT` | `/forget/password` | 公开 | 忘记密码重置 |
| `PUT` | `/change/password` | 公开 | 修改密码 |
| `POST` | `/refresh/token` | 公开 | 刷新 access token |
| `GET` | `/detail/get` | 登录 | 查询用户详情 |
| `PUT` | `/detail/update` | 登录 | 修改用户详情 |
| `POST` | `/collect/add` | 登录 | 添加收藏 |
| `DELETE` | `/collect/delete` | 登录 | 删除收藏 |
| `GET` | `/collect/list` | 登录 | 查询收藏列表 |

### `POST /api/user/login/account`

Body：

```json
{
  "username": "test",
  "password": "123456"
}
```

返回 `data` 由 `SysLoginService.loginByAccount` 决定，前端期望至少包含：

```json
{
  "accessToken": "xxx",
  "refreshToken": "xxx",
  "userInfo": {}
}
```

### `POST /api/user/create/account`

Query：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `username` | string | 是 | 用户名 |
| `password` | string | 是 | 密码 |
| `phone` | string | 是 | 手机号 |

### `POST /api/user/refresh/token`

Query 或 `application/x-www-form-urlencoded`：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `refreshToken` | string | 是 | 刷新 token |

### `PUT /api/user/detail/update`

Body：

```json
{
  "nickname": "张三",
  "avatar": "https://example.com/avatar.png",
  "phone": "13800138000"
}
```

### 收藏接口

`GET /api/user/collect/list` 使用简单游标查询参数：

| 参数 | 类型 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| `sortValue` | string | 否 | - | 上一页末尾排序值 |
| `sortId` | long | 否 | - | 上一页末尾 ID |
| `querySize` | int | 否 | 20 | 查询数量 |

`POST /api/user/collect/add`：

| 参数 | 位置 | 类型 | 必填 |
| --- | --- | --- | --- |
| `productId` | query | string | 是 |

`DELETE /api/user/collect/delete`：

| 参数 | 位置 | 类型 | 必填 | 示例 |
| --- | --- | --- | --- | --- |
| `productIds` | query | string | 是 | `10001,10002` |

## 4. 首页、分类、公告、Banner

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/banner/list` | 公开 | 首页轮播图 |
| `GET` | `/api/category/tree` | 公开 | 分类树 |
| `GET` | `/api/category/product/list/{categoryId}/{beginProductId}` | 公开 | 分类商品列表 |
| `GET` | `/api/notice/latest` | 公开 | 最新公告 |

### `GET /api/notice/latest`

| 参数 | 位置 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- | --- |
| `limit` | query | int | 否 | 5 |

### `GET /api/category/product/list/{categoryId}/{beginProductId}`

| 参数 | 位置 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- | --- |
| `categoryId` | path | string | 是 | - |
| `beginProductId` | path | string | 是 | - |
| `sortType` | query | string | 否 | `default` |

## 5. 商品

Base path：`/api`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/product/hot` | 公开 | 热门商品 |
| `GET` | `/product/brief/list` | 公开 | 按 ID 批量查商品简要信息 |
| `GET` | `/product/detail` | 公开，可选登录 | 商品详情 |
| `GET` | `/product/categroy/list` | 公开 | 分类下商品游标列表。注意路径拼写为 `categroy` |
| `GET` | `/product/search` | 公开 | 关键词搜索商品 |
| `GET` | `/product/related` | 公开 | 相关商品 |
| `GET` | `/product/spec/price` | 公开 | 商品规格价格 |
| `GET` | `/product/scroll/query/list` | 公开 | 首页猜你喜欢滚动列表 |
| `GET` | `/product/user/keyword/list` | 公开 | 用户端热门搜索词 |
| `GET` | `/product/admin/keyword/list` | 公开，建议收紧 | 管理端搜索词列表 |
| `PUT` | `/product/admin/keyword/update` | 公开，建议收紧 | 管理端更新搜索词 |
| `PUT` | `/product/admin/{productId}/images` | 公开，建议收紧 | 管理员替换商品图片集 |

### 查询参数

`GET /api/product/hot`

| 参数 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- |
| `limit` | int | 否 | 10 |

`GET /api/product/brief/list`

| 参数 | 类型 | 必填 | 示例 |
| --- | --- | --- | --- |
| `productIds` | string | 否 | `10001,10002` |

`GET /api/product/detail`

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `productId` | query | string | 是 | 商品 ID |
| `X-User-Id` | header | string | 否 | 后端接口声明支持，但前端实际靠 `Authorization` |

`GET /api/product/categroy/list`

| 参数 | 类型 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| `sortType` | string | 是 | - | 排序类型 |
| `sortValue` | string | 否 | - | 上一页末尾排序值 |
| `sortId` | long | 否 | - | 上一页末尾 ID |
| `querySize` | int | 否 | 20 | 查询数量 |
| `categoryId` | long | 否 | - | 分类 ID |
| `isFirstCategoryId` | boolean | 否 | false | 是否一级分类 |

`GET /api/product/search`

| 参数 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | - |
| `sortType` | string | 是 | - |
| `sortValue` | string | 否 | - |
| `sortId` | long | 否 | - |
| `querySize` | int | 否 | 20 |

`GET /api/product/related`

| 参数 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- |
| `productName` | string | 是 | - |
| `limit` | int | 否 | 10 |

`GET /api/product/spec/price`

| 参数 | 类型 | 必填 |
| --- | --- | --- |
| `productId` | string | 是 |
| `specId` | string | 是 |

## 6. 商品评论

Base path：`/api/user/product/comment`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/firstComment/show` | 可选登录 | 查询一级评论 |
| `POST` | `/secondComment/show` | 可选登录 | 查询二级评论 |
| `GET` | `/appendComment/show` | 可选登录 | 查询追评 |
| `POST` | `/firstComment/save` | 登录 | 发表一级评论 |
| `POST` | `/secondComment/save` | 登录 | 发表二级/回复评论 |
| `POST` | `/firstComment/append` | 登录 | 追加评论 |
| `GET` | `/count/show` | 登录 | 评论数量统计 |
| `PUT` | `/like` | 登录 | 点赞/取消点赞 |

### `POST /api/user/product/comment/firstComment/show`

Query：

| 参数 | 类型 | 必填 |
| --- | --- | --- |
| `productId` | string | 是 |

Body：

```json
{
  "sortType": "default",
  "sortValue": null,
  "sortId": null,
  "querySize": 20
}
```

### `POST /api/user/product/comment/secondComment/show`

Query：

| 参数 | 类型 | 必填 |
| --- | --- | --- |
| `firstCommentId` | string | 是 |

Body 同 `CursorCommonEntity`。

### `POST /api/user/product/comment/firstComment/save`

Body：

```json
{
  "productId": "10001",
  "productSpecId": "20001",
  "productSpecText": "颜色：黑色 | 尺寸：XL",
  "orderNo": "NO202606040001",
  "userNickname": "张三",
  "userAvatar": "https://example.com/avatar.jpg",
  "content": "质量很好",
  "imageUrls": "[\"https://example.com/img1.jpg\"]",
  "rating": 5,
  "isAnonymous": 0
}
```

### `POST /api/user/product/comment/secondComment/save`

Body：

```json
{
  "productId": "10001",
  "productSpecId": "20001",
  "parentId": "30001",
  "userNickname": "张三",
  "userAvatar": "https://example.com/avatar.png",
  "content": "回复内容",
  "replyUserId": "10002",
  "replyUserNickname": "李四",
  "isAnonymous": 0
}
```

### `PUT /api/user/product/comment/like`

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `productCommentId` | query | string | 是 | 评论 ID |
| `isLike` | query | int | 是 | `1` 点赞，`0` 取消 |
| `isFirstComment` | query | int | 是 | `1` 一级评论，`0` 二级评论 |

## 7. 购物车

Base path：`/api/cart`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/list` | 登录 | 查询购物车 |
| `POST` | `/add` | 登录 | 添加商品到购物车 |
| `DELETE` | `/clear` | 登录 | 清空购物车 |
| `DELETE` | `/products` | 登录 | 删除购物车商品 |
| `PUT` | `/update` | 登录 | 将前端购物车列表同步到数据库 |

`POST /api/cart/add` Body：

```json
{
  "productId": "10001",
  "specId": "20001",
  "quantity": 1
}
```

`DELETE /api/cart/products` Query：

| 参数 | 类型 | 必填 | 示例 |
| --- | --- | --- | --- |
| `productIds` | string | 是 | `10001,10002` |
| `specIds` | string | 是 | `20001,20002` |

`PUT /api/cart/update` Body：

```json
{
  "cartItems": [
    {
      "productId": "10001",
      "specId": "20001",
      "quantity": 2
    }
  ]
}
```

## 8. 地址

Base path：`/api/address`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/list` | 登录 | 地址列表 |
| `POST` | `/add` | 登录 | 新增地址 |
| `PUT` | `/update` | 登录 | 修改地址 |
| `DELETE` | `/delete` | 登录 | 删除地址 |

地址 Body：

```json
{
  "id": "1",
  "receiver": "张三",
  "phone": "13800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园 1 号",
  "isDefault": "DEFAULT"
}
```

`isDefault` 是后端枚举 `CommonDefault`，请以接口实际返回值为准进行回传。

## 9. 订单

Base path：`/api/order`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/create` | 登录 | 创建订单 |
| `GET` | `/list` | 登录 | 分页查询订单 |
| `GET` | `/page/list` | 登录 | 按页面名查询订单 |
| `GET` | `/detail` | 登录 | 订单详情 |
| `PUT` | `/cancel` | 登录 | 取消订单 |
| `PUT` | `/pay/success` | 登录 | 标记支付成功 |
| `PUT` | `/confirmReceipt` | 登录 | 确认收货 |
| `DELETE` | `/delete` | 登录 | 逻辑删除订单 |
| `GET` | `/freight` | 登录 | 计算运费 |
| `GET` | `/logistics` | 登录 | 查询物流 |
| `GET` | `/scroll/query/list` | 登录 | 滚动查询全部订单 |
| `GET` | `/search` | 登录 | 条件搜索订单 |

### `POST /api/order/create`

Body：

```json
{
  "addressId": "1",
  "remark": "尽快发货",
  "freight": "10.00",
  "totalAmount": "199.00",
  "orderItems": [
    {
      "productId": "10001",
      "specId": "20001",
      "quantity": 1,
      "price": "189.00",
      "productName": "商品名称",
      "productImage": "https://example.com/product.jpg",
      "specText": "颜色：黑色"
    }
  ]
}
```

前端期望创建成功后 `data.orderNo` 存在。

### 查询/操作参数

| 接口 | 参数 |
| --- | --- |
| `GET /api/order/list` | `pageNum` 默认 1；`pageSize` 默认 10；`status` 默认 `pendingPayment` |
| `GET /api/order/page/list` | `pageName`，前端订单页使用 |
| `GET /api/order/detail` | `orderNo` |
| `PUT /api/order/cancel` | `orderNo` 必填；`cancelReason` 可选 |
| `PUT /api/order/pay/success` | `orderNo` |
| `PUT /api/order/confirmReceipt` | `orderNo` |
| `DELETE /api/order/delete` | `orderNo` |
| `GET /api/order/freight` | `productIds` 逗号分隔；`addressId` |
| `GET /api/order/logistics` | `orderNo` |
| `GET /api/order/search` | `searchCondition`，可传商品名/订单号/快递单号 |

`GET /api/order/scroll/query/list` 当前后端声明为 GET + `@RequestBody ScrollQueryDTO`，部分客户端不稳定，建议改为 query 参数或 POST。

## 10. 支付

Base path：`/api/pay`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/wxpay` | 登录 | 微信支付预下单 |

Body：

```json
{
  "orderNo": "NO202606040001"
}
```

当前 `PayController.wxPay` 中真实微信支付调用被注释，接口返回 `Result.success()`，不返回 JSAPI 支付参数。前端当前在支付成功后继续调用 `PUT /api/order/pay/success`。

## 11. 文件上传

Base path：`/api`

| 方法 | 路径 | 鉴权 | Content-Type | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/upload/image` | 公开 | `multipart/form-data` | 单图上传，字段名 `file` |
| `POST` | `/upload/images` | 登录 | `multipart/form-data` | 多图上传，字段名 `files` |

单图返回：

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": "https://bucket.oss-cn-beijing.aliyuncs.com/xxx.jpg"
}
```

## 12. 反馈与关于我们

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/feedback/add` | 登录 | 提交意见反馈 |
| `GET` | `/api/about/us/introduce` | 公开 | 工厂/关于我们信息 |

反馈 Body：

```json
{
  "content": "反馈内容",
  "images": "[\"https://example.com/1.jpg\"]",
  "contact": "13800138000"
}
```

## 13. 聊天

### HTTP 接口

Base path：`/api/chat`

| 方法 | 路径 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/sessions` | 登录 | 当前用户会话列表 |
| `GET` | `/history/{contactId}` | 登录 | 与某联系人聊天历史 |
| `POST` | `/clearUnread/{contactId}` | 登录 | 清除某会话未读数 |

`GET /api/chat/history/{contactId}` 参数：

| 参数 | 位置 | 类型 | 必填 | 默认 |
| --- | --- | --- | --- | --- |
| `contactId` | path | long | 是 | - |
| `page` | query | int | 否 | 1 |
| `size` | query | int | 否 | 20 |

### WebSocket

连接：

```text
ws://127.0.0.1:8888/ws/chat?token={accessToken}
```

客户端发送消息：

```json
{
  "action": "SEND_MSG",
  "data": {
    "toUserId": 2,
    "msgType": 0,
    "content": "你好",
    "productId": 10001
  }
}
```

客户端已读上报：

```json
{
  "action": "READ_REPORT",
  "data": {
    "contactId": 2
  }
}
```

心跳：

```json
{
  "action": "HEARTBEAT",
  "data": 1780567200000
}
```

服务端推送消息：

```json
{
  "action": "PUSH_MSG",
  "data": {
    "fromUserId": 2,
    "msgType": 0,
    "content": "你好",
    "productId": 10001,
    "createTime": "2026-06-04 10:00:00"
  }
}
```

发送确认：

```json
{
  "action": "MSG_ACK",
  "data": {
    "msgId": 123,
    "status": "SUCCESS"
  }
}
```

## 14. 管理端接口

当前项目已有管理端接口，但前端小程序主要未调用。管理端接口上线前需要补权限。

### Banner 管理

| 方法 | 路径 | 鉴权现状 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/admin/banner/list` | 登录 | 管理员查询 Banner |
| `POST` | `/api/admin/banner/add` | 登录 | 新增 Banner |
| `PUT` | `/api/admin/banner/update` | 登录 | 更新 Banner |
| `DELETE` | `/api/admin/banner/delete` | 登录 | 删除 Banner |
| `PUT` | `/api/admin/banner/updateSort` | 登录 | 更新排序 |
| `PUT` | `/api/admin/banner/updateStatus` | 登录 | 更新状态 |

Banner Body：

```json
{
  "title": "首页广告",
  "imageUrl": "https://example.com/banner.jpg",
  "linkUrl": "/pages/product/detail/detail?productId=10001",
  "sort": 1,
  "status": "ACTIVE"
}
```

### 分类管理

| 方法 | 路径 | 鉴权现状 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/admin/category/add` | 登录 | 新增分类 |
| `DELETE` | `/api/admin/category/{categoryId}` | 登录 | 删除分类 |
| `PUT` | `/api/admin/category/{id}` | 登录 | 修改分类 |
| `PUT` | `/api/admin/category/{id}/status` | 登录 | 修改分类状态 |

Category Body：

```json
{
  "name": "沙发",
  "parentId": "0",
  "sort": 1,
  "iconUrl": "/static/images/default-category.png",
  "status": 1
}
```

### 公告管理

| 方法 | 路径 | 鉴权现状 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/admin/notice/add` | 登录 | 新增公告 |
| `PUT` | `/api/admin/notice/update` | 登录 | 更新公告 |
| `DELETE` | `/api/admin/notice/delete` | 登录 | 删除公告 |

### 优惠券管理

| 方法 | 路径 | 鉴权现状 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/admin/coupon/release` | 登录 | 创建优惠券活动 |

Body：

```json
{
  "activityName": "88折会员专属券",
  "couponType": 2,
  "faceValue": null,
  "discountRate": 8.8,
  "maxDiscount": 30.0,
  "minSpend": 0.0,
  "totalQuota": 1000,
  "validMode": 2,
  "validStart": null,
  "validEnd": null,
  "receiveValidDays": 15,
  "limitPerPerson": 1,
  "userLimitType": 1,
  "useScope": 1,
  "mutexGroupId": 0,
  "status": 1,
  "releaseTime": "2026-06-04T10:00:00"
}
```

## 15. 前后端联调注意事项

| 问题 | 当前状态 | 建议 |
| --- | --- | --- |
| 退出登录方法不一致 | 前端 `utils/api.js` 写的是 `DELETE /api/user/logout`，后端是 `POST /api/user/logout` | 前端改为 POST，或后端补 DELETE 映射 |
| 购物车合并路径不一致 | 前端 `cartStore.js`、登录页调用 `/api/cart/merge`，后端只有 `PUT /api/cart/update` | 前端统一改 `/api/cart/update`，或后端增加 `/merge` 别名 |
| 企业认证接口缺失 | 前端调用 `POST /api/user/enterprise/auth`，后端没有 Controller | 若保留页面，需要新增接口和表结构；否则隐藏入口 |
| WebSocket H5 代理目标不一致 | 前端 H5 代理 `/ws` 到 8080，Netty 实际监听 8888 | 将 Vite `/ws` 代理 target 改为 `ws://127.0.0.1:8888` |
| 支付预下单未实现 | `/api/pay/wxpay` 返回空成功，不返回微信支付参数 | 接入 `payService.wxPay(orderNo)` 后再联调真实支付 |
| `GET + RequestBody` | `/api/order/scroll/query/list` 使用 GET Body | 改为 `POST` 或使用 query 参数 |
| 管理端权限过宽 | `/api/product/**` 公开覆盖 `/api/product/admin/**` | 管理端路径独立到 `/api/admin/product/**` 或调整 Shiro 顺序 |
| 批量上传鉴权不一致 | `/api/upload/image` 公开，`/api/upload/images` 走登录 | 明确业务需要，统一鉴权策略 |

