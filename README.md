# Eduication 外语学习平台（Spring Boot 3）

基于 **Spring Boot 3** 的在线外语学习后端示例项目，覆盖以下核心能力：

- 登录/注册（JWT 鉴权）
- 软文引流模块
- 课程与资源模块（普通资源 / 会员资源）
- 记单词模块（打卡与复习进度）
- 会员订阅模块（套餐、订单、开通会员）
- 支付接入骨架（微信 / 支付宝）
- Spring AI 教学能力（国内/国外大模型，资源生成与学习对话）

> 当前实现是可运行的后端骨架，支付部分提供统一接口与回调流程，便于后续替换为真实 SDK/网关调用。

---

## 1. 技术栈

- Java 17
- Spring Boot 3.4.x
- Spring Security + JWT
- Spring Data JPA + H2
- Spring AI（OpenAI 模型 + OpenAI 兼容国内模型）
- Maven

---

## 2. 启动方式

```bash
mvn spring-boot:run
```

启动后：

- 服务地址：`http://localhost:8080`
- H2 控制台：`http://localhost:8080/h2-console`

---

## 3. 关键配置（`application.yml`）

```yaml
spring:
  ai:
    openai:
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
      api-key: ${OPENAI_API_KEY:demo-key}
      chat:
        options:
          model: ${OPENAI_MODEL:gpt-4o-mini}

app:
  ai:
    domestic:
      base-url: ${DOMESTIC_MODEL_BASE_URL:https://api.deepseek.com}
      api-key: ${DOMESTIC_MODEL_API_KEY:demo-key}
      model: ${DOMESTIC_MODEL_NAME:deepseek-chat}
```

- `spring.ai.openai.*`：国外模型
- `app.ai.domestic.*`：国内模型（OpenAI 兼容协议）

---

## 4. 模块接口概览

### 4.1 认证模块

- `POST /api/auth/register` 注册
- `POST /api/auth/login` 登录

登录后通过请求头携带：

```text
Authorization: Bearer <token>
```

### 4.2 软文引流

- `GET /api/marketing/public/articles` 公共软文列表
- `GET /api/marketing/public/articles/{id}` 软文详情
- `POST /api/marketing/articles` 创建软文（登录后）

### 4.3 课程/资源（普通 + 会员）

- `GET /api/courses` 按当前用户权限查询课程
- `POST /api/courses` 创建课程
- `POST /api/courses/{courseId}/resources` 创建课程资源
- `GET /api/courses/{courseId}/resources` 查询课程资源（自动校验会员）

### 4.4 记单词

- `POST /api/words` 创建单词
- `GET /api/words?language=英语` 查询单词（自动过滤会员词）
- `POST /api/words/{wordId}/review` 单词打卡
- `GET /api/words/progress` 复习进度

### 4.5 会员订阅 + 微信/支付宝

- `GET /api/membership/plans` 会员套餐列表（公开）
- `POST /api/membership/subscribe` 创建订阅订单（微信/支付宝）
- `GET /api/membership/orders` 我的订单
- `GET /api/membership/me` 我的会员状态
- `POST /api/payments/wechat/callback` 微信回调
- `POST /api/payments/alipay/callback` 支付宝回调

### 4.6 Spring AI 教学资源生成与学习交互

- `POST /api/ai/resources/generate` 生成教学资源
- `POST /api/ai/chat` 学习问答

`provider` 参数：

- `DOMESTIC`：国内模型
- `FOREIGN`：国外模型

---

## 5. 后续可扩展建议

1. 将支付示例适配器替换为微信支付 v3 SDK 与支付宝官方 SDK。
2. 增加 RBAC（管理员专用的课程/软文/套餐管理接口）。
3. 记单词模块升级为 SM-2 等间隔重复算法。
4. Spring AI 接入向量库（RAG），结合课程资料进行个性化问答。
