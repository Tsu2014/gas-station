# ⛽ gas-station 智慧加油站一体化解决方案

一个开箱即用的加油站业务平台，包含 **后端服务（Spring Boot 3）**、**运营管理后台（Vue2 + Element-UI）** 与 **微信小程序（原生）** 三部分，覆盖「用户找站 → 扫码加油 → 在线支付 → 会员积分/优惠券 → 油站后台运营管理」的完整闭环。

## 一、整体架构

```
┌─────────────────────┐   ┌─────────────────────┐
│  微信小程序 (mini)   │   │  运营管理后台 (admin) │
│  找油站/扫码/支付    │   │  油站·油价·订单·营销 │
└─────────┬───────────┘   └──────────┬──────────┘
          │  HTTPS / JSON             │
          └───────────┬───────────────┘
                      ▼
        ┌───────────────────────────────┐
        │  gas-station-server (8080)    │
        │  Spring Boot 3 + MyBatis-Plus │
        │  JWT 鉴权 / 分层 API           │
        └───────────────┬───────────────┘
                        ▼
                 MySQL 8（gs_* 业务表）
```

目录结构：

| 目录 | 说明 |
| --- | --- |
| `server/` | 后端服务，Spring Boot 3.2 + MyBatis-Plus 3.5 + MySQL 8 + JWT |
| `admin/` | 运营管理后台，Vue 2.6 + Element-UI 2.15 + Vue Router + Vuex + Axios + ECharts |
| `miniprogram/` | 微信小程序（原生框架，无需构建，微信开发者工具直接打开） |
| `sql/` | 数据库建表与初始化数据脚本 |

## 二、核心功能

**小程序端（C 端）**
- 微信授权登录（未配置 appid/secret 时自动降级为 mock 登录，方便本地联调）
- LBS 附近油站列表、油站详情（实时油价、油枪状态）
- 扫码 / 选枪加油：选择油枪与加油金额（或升数）→ 自动算价 → 下单
- 余额支付 / 微信支付（预留）、优惠券抵扣、积分累计
- 订单列表与详情、会员中心（余额、积分、优惠券）

**管理后台（B 端）**
- 数据看板：今日订单、销售额、会员数、油站数、近 7 日趋势图
- 油站管理、油枪管理、油价管理（挂牌价 / 优惠价）
- 订单管理（查询、详情、退款/取消）
- 会员管理（余额调整、启用禁用）
- 优惠券管理（创建、发放、核销统计）
- 员工/账号管理（站长、加油员、超管）

## 三、快速开始

### 1. 数据库

```bash
mysql -uroot -p -e "CREATE DATABASE gas_station DEFAULT CHARACTER SET utf8mb4;"
mysql -uroot -p gas_station < sql/gas_station.sql
```

默认内置账号（密码均为 `123456`，BCrypt 加密）：

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | 超级管理员 | 全部权限 |
| `manager` | 站长 | 绑定到示例油站 |
| `member` | — | 小程序端会员（openid: `mock_member_001`） |

### 2. 后端

```bash
cd server
# 修改 src/main/resources/application.yml 中的数据源
mvn spring-boot:run
```

启动后访问接口文档：http://localhost:8080/swagger-ui.html

### 3. 管理后台

```bash
cd admin
npm install
npm run dev      # http://localhost:9528
```

后端接口地址在 `admin/.env.development` 中配置（`VUE_APP_BASE_API`）。

### 4. 微信小程序

使用微信开发者工具导入 `miniprogram/` 目录：
1. 填写自己的 AppID（或用「测试号」）
2. 修改 `miniprogram/config.js` 中的 `baseUrl` 为本机局域网地址（真机调试）或 `http://localhost:8080`（模拟器需勾选「不校验合法域名」）
3. 后端 `application.yml` 中配置 `gas.wx.appid/secret` 后即为真实微信登录，未配置时走 mock 登录

## 四、接口约定

统一返回结构：

```json
{ "code": 0, "msg": "ok", "data": {} }
```

| code | 含义 |
| --- | --- |
| 0 | 成功 |
| 401 | 未登录 / Token 失效 |
| 403 | 无权限 |
| 500 | 业务或系统异常 |

请求头：`Authorization: Bearer <token>`

路径规范：
- `/api/auth/**`、`/api/public/**`：无需登录
- `/api/app/**`：小程序会员（角色 `MEMBER`）
- `/api/admin/**`：后台用户（角色 `ADMIN` / `MANAGER` / `STAFF`）

## 五、许可

MIT License
